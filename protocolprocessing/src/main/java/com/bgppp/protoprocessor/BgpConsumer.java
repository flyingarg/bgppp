package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import org.apache.log4j.*;

import com.bgppp.protoprocessor.utils.*;

public class BgpConsumer extends Thread {
	public static Logger log = Logger.getLogger(BgpConsumer.class.getName());
	private BgpConfig bgpConfig = null;
	private Link link = null;
	private int PORT = GetProperties.bgpPort;
	private boolean isRunning = false;
	private BgpProducer bgpProducer = null;
	public HashMap<String, BgpConsumerThread> connsFromPeers = new HashMap<String, BgpConsumerThread>();
	private FSMState fsmState = FSMState.IDLE;
	private ServerSocket serverSocket = null;
	
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		if(isRunning == false){
			for(String consumerThreadName : connsFromPeers.keySet()){
				connsFromPeers.get(consumerThreadName).timeUp();
				this.isRunning = false;
				try{
					this.serverSocket.close();
				}catch(Exception e){
					log.error("Error closing socket : " + e.getMessage() );
				}
			}
		}else{
			this.isRunning = isRunning;
		}
	}

	public BgpConsumer(BgpConfig bgpConfig, Link link) {
		this.bgpConfig = bgpConfig;
		this.link = link;
		this.setName(bgpConfig.getRouterName() + "_consumer_" + link.getSourceAddress());
	}

	@Override
	public void run() {
		setFsmState(FSMState.IDLE);
		try {
			InetAddress address = bgpConfig.getAddressAndMaskByName(link.getSourceAddressName()).getAddress();
			if (address == null) {
				log.error("Address is null, what did you do ? ");
			}
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(address, PORT));
			setRunning(true);
			while(isRunning()){
				Socket listen = serverSocket.accept();
				log.info("C("+address+") in CONNECT state " + listen.getRemoteSocketAddress().toString());
				setFsmState(FSMState.CONNECT);
				getBgpProducer().timeUp();
				BgpConsumerThread consumerThread = new BgpConsumerThread(
						this
						,listen
						,new DataInputStream(listen.getInputStream())
						,new DataOutputStream(listen.getOutputStream())
						,getBgpProducer()
						,this.getName());
				String remoteAddress = listen.getRemoteSocketAddress().toString();
				remoteAddress = remoteAddress.split(":")[0];
				connsFromPeers.put(
						getBgpConfig().getRouterName() + link.getSourceAddress().toString() + remoteAddress
						, consumerThread);
				consumerThread.start();
			}
		}catch(Exception e){
			log.error("Exception starting consumerThreads " + e.getMessage());
			e.printStackTrace();
		}
	}
	public BgpConfig getBgpConfig(){
		return this.bgpConfig;
	}
	public boolean isConsumerAlive(){
		return this.link.isAlive();
	}
	public BgpProducer getBgpProducer() {
		return bgpProducer;
	}
	public void setBgpProducer(BgpProducer bgpProducer) {
		this.bgpProducer = bgpProducer;
	}
	public HashMap<String, BgpConsumerThread> getConnsFromPeers(){
		return this.connsFromPeers;
	}
	public FSMState getFsmState() {
		return fsmState;
	}
	public void setFsmState(FSMState fsmState) {
		if(!this.fsmState.equals(fsmState)){
			if(fsmState == FSMState.ESTABLISHED){
				addPeer(getOneDamnedConThread().nameOfRouterConnectedTo);
				link.setDestinationId(getOneDamnedConThread().nameOfRouterConnectedTo);
				link.setAlive(true);
			}if(FSMState.ESTABLISHED == this.fsmState && FSMState.IDLE == fsmState){
				getBgpConfig().getRuleStore().removeRulesFrom(getOneDamnedConThread().nameOfRouterConnectedTo+"=="+this.getName(), true);
			}
		}
		this.fsmState = fsmState;
	}
	
	private void addPeer(String s){
		bgpConfig.getRuleStore().addPeers(s+"=="+this.getName(), (BgpOperations)this.getOneDamnedConThread());
	}
	public BgpConsumerThread getOneDamnedConThread(){
		if(connsFromPeers.size() == 0)
			return null;
		for(String key : connsFromPeers.keySet()){
			if(connsFromPeers.get(key) != null){
				return connsFromPeers.get(key);
			}
		}
		return null;
	}

}

package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
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
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public BgpConsumer(BgpConfig bgpConfig, Link link) {
		this.bgpConfig = bgpConfig;
		this.link = link;
		this.setName(bgpConfig.getRouterName() + "_consumer_" + link.getSourceAddressName());
	}

	@Override
	public void run() {
		log.info("Starting Consumer : " + bgpConfig.getRouterName() + ":" +link);
		ServerSocket serverSocket = null;
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
				log.info("Preventing " + "Producer : " + bgpConfig.getRouterName() + ":" +link + " from connecting and closing thread");
				getBgpProducer().setRunning(false);
				BgpConsumerThread consumerThread = new BgpConsumerThread(listen
						,new DataInputStream(listen.getInputStream())
						,new DataOutputStream(listen.getOutputStream())
						,getBgpProducer()
						,this.getName());
				InetAddress remoteAddress = ((InetSocketAddress)listen.getRemoteSocketAddress()).getAddress();
				connsFromPeers.put(getBgpConfig().getRouterName()+link.getSourceAddressName() + (remoteAddress == link.getDestinationAddress() ? link.getDestinationAddress().toString() : remoteAddress.toString()), consumerThread);
				consumerThread.start();
			}
		}catch(Exception e){
			log.info("Exception starting consumerThreads " + e.getMessage());
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
}

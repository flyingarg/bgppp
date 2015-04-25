package com.bgppp.protoprocessor;

import java.nio.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.*;

import org.apache.log4j.*;

import com.bgppp.protoprocessor.timers.*;
import com.bgppp.protoprocessor.utils.*;
import com.bgppp.protoprocessor.packet.BgpOpenPacket;
import com.bgppp.protoprocessor.packet.BgpUpdatePacket;

public class BgpProducer extends BgpOperations implements TimerListener{
	public static Logger log = Logger.getLogger(BgpProducer.class.getName());
	private BgpConfig bgpConfig = null;
	private Link link = null;
	private DataOutputStream outputStream = null;
	private DataInputStream inputStream = null;
	private boolean isRunning = true;

	public KeepAliveTimer kaTimer = null;
	public ConnectRetryTimer connectRetryTimer = null;
	public HoldTimer holdTimer = null;
	private Socket socket = null;
	private KeepAliveSender kaSender = null;
	
	private int totalBgpPackets = 0;
	private int countKA = 0;
	private int countOpen = 0;
	private int countUpdate = 0;
	private int countOthers = 0;
	private int countNotification = 0;
	
	private FSMState fsmState = FSMState.IDLE;
	boolean isEstablished = false;

	private String nameOfRouterConnectedTo = "";
	public String destinationAddress = "";
	public boolean isRunning() {
		return isRunning;
	}
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	public BgpProducer(BgpConfig bgpConfig, Link link){
		this.bgpConfig = bgpConfig;
		this.link = link;
		this.setName(bgpConfig.getRouterName()+"_producer_"+link.getDestinationAddress());
		this.kaTimer = new KeepAliveTimer(this.getName(), (new Date()).getTime(), this);
		this.connectRetryTimer = new ConnectRetryTimer("", (new Date()).getTime(), this);
		this.holdTimer = new HoldTimer("", (new Date()).getTime(), this);
		this.kaSender = new KeepAliveSender("KAS-"+this.getName());
	}
	
	@Override
	public void timeUp() {
		log.info("Terminating Producer " + this.getName());
		Thread.currentThread().getStackTrace();
		try {
			setRunning(false);
			if(socket != null)
			socket.close();
			this.kaTimer.setRunning(false);
			this.connectRetryTimer.setRunning(false);
			this.holdTimer.setRunning(false);
			this.kaSender.setRunning(false);
			setRunning(false);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		setFsmState(FSMState.IDLE);
	}

	@Override
	public void run(){
		setFsmState(FSMState.IDLE);
		InetSocketAddress inetSocketSource = new InetSocketAddress(link.getSourceAddress(), GetProperties.clientPort);
		InetSocketAddress inetSocketDestination = new InetSocketAddress(link.getDestinationAddress(), GetProperties.bgpPort);
		try{
			socket = new Socket();
			socket.bind(inetSocketSource);
		}catch(Exception e){
			log.error("Socket error : " + e.getMessage());e.printStackTrace();
		}
		boolean isConnected = false;
		boolean bgpOpenCommandProcessed = false;
		try{
			int randomInt = ControlledRandom.nextInt(2,10);
			BgpProducer.sleep(randomInt * 1000);
		}catch(InterruptedException e){
			log.error(e.getMessage());
		}
		this.kaTimer.start();
		while(isRunning()){
			try {
				if(!isConnected && socket!=null){
					socket.setSoTimeout(TimeOutUtils.READ_SO_TIMEOUT);
					socket.connect(inetSocketDestination);
					outputStream = new DataOutputStream(socket.getOutputStream());
					inputStream = new DataInputStream(socket.getInputStream());
					this.kaSender.setInputStream(inputStream);
					this.kaSender.setOutputStream(outputStream);
					isConnected = true;
					log.info("P("+socket.getLocalAddress().toString()+") in CONNECT state " + socket.getRemoteSocketAddress().toString());
					setFsmState(FSMState.CONNECT);
				}
			}catch (UnknownHostException e) {
				log.error("Unknown Host "+e.getMessage());
				e.printStackTrace();
				timeUp();
			} catch(SocketTimeoutException exception){
				log.error("No response from " + link.getDestinationAddress());
				link.setAlive(false);
				timeUp();
			} catch(ConnectException exception){
				try {
					log.error("Could not connect to " + link.getDestinationAddress());
					isConnected = false;
					link.setAlive(false);
					Thread.sleep(TimeOutUtils.RECONNECT_TIME);
					timeUp();
				} catch (InterruptedException e) {
					log.error("Interrupter " + e.getMessage());
					e.printStackTrace();
				}
			} catch (IOException e) {
				log.error("IOException : " + e.getMessage());
				e.printStackTrace();
				timeUp();
			}
			if(isConnected && !bgpOpenCommandProcessed){
				String asNumber = getName().split("_")[0];
				destinationAddress = socket.getRemoteSocketAddress().toString().split(":")[0].replaceAll("\\/", "");
				bgpOpenCommandProcessed = toSendOPEN(inputStream, outputStream, log, destinationAddress, asNumber);
				setFsmState(FSMState.OPEN_SENT);
			}
			if(isConnected && bgpOpenCommandProcessed){
				try{
					byte[] packRest = null;
					int ffByteCount = 0;
					while(this.isRunning()){
						if(!this.kaTimer.isRunning()){
							this.setRunning(false);
							socket.close();
						}
						byte bite = 0;
						try{
							bite = inputStream.readByte();
						}catch(EOFException e){
						}
						if(bite == (byte)255){
							ffByteCount+=1;
						}else{
							ffByteCount = 0;
						}
						if(ffByteCount == 16){
							totalBgpPackets+=1;
							byte[] packLen = new byte[2];
							inputStream.read(packLen);
							byte[] packType = new byte[1];
							inputStream.read(packType);
							if(getInt(packLen) >= 19) 
								packRest = new byte[getInt(packLen)-19];
							inputStream.read(packRest);
							ffByteCount = 0;
							if(getInt(packType) == 2 || getInt(packType) == 1){
								ByteBuffer completePacket = ByteBuffer.allocate(getInt(packLen));
								completePacket.put(getMarker());
								completePacket.put(packLen);
								completePacket.put(packType);
								completePacket.put(packRest);
								processPacket(getInt(packLen),getInt(packType),completePacket.array());
							}else{
								processPacket(getInt(packLen),getInt(packType),packRest);
							}
						}
					}
				}catch(Exception e){
					log.error("parsing packet : " + e.getMessage());
					e.printStackTrace();
					timeUp();
				}
			}
		}
		if(!isRunning()){
			log.info("P("+link.getSourceAddress().toString()+") to " + link.getDestinationAddress().toString() + " Terminated");
			try {
				if(socket!=null)socket.close();
				if(inputStream!=null)inputStream.close();
				if(outputStream!=null)outputStream.close();
				setFsmState(FSMState.IDLE);
			} catch (IOException e) {
				log.error("Error closing connections" + e.getMessage());
			}
		}
	}

	public void processPacket(int packLen,int packType,byte[] packRest){
		String type="";
		if(packType == 4)type="KeepAlive";else if(packType == 1)type="Open";else if(packType == 2)type="Update";else if(packType == 3)type="Notification";
		log.info("Packet received, type "+type+" length "+ packRest.length);
		switch(packType){
			case 4: countKA++;
					this.kaTimer.resetCounter();
					if(!kaSender.isRunning())kaSender.start();
					if(!isEstablished){
						setFsmState(FSMState.ESTABLISHED);
						isEstablished = true;
						log.info("P("+socket.getLocalAddress().toString()+") in ESTABLISHED state " + socket.getRemoteSocketAddress().toString());
					}
					log.info("1");
					break;
			case 1: this.kaTimer.resetCounter();
					BgpOpenPacket op = new BgpOpenPacket(packRest);
					this.nameOfRouterConnectedTo = "" + op.getAsNumber();
					if(!kaSender.isRunning())kaSender.start();
					setFsmState(FSMState.OPEN_CONFIRM);
					countOpen++;
					break;
			case 2: countUpdate++;
					BgpUpdatePacket up = new BgpUpdatePacket(packRest);
					String bgpOperationName = nameOfRouterConnectedTo+"=="+this.getName();
					bgpConfig.getRuleStore().addRule(up.getRule(nameOfRouterConnectedTo, bgpOperationName));
					countUpdate++;
					break;
			case 3: countNotification++;
					break;
			default:countOthers++;
					break;
		}
	}

	public void toSendUpdate(BgpUpdatePacket packet){
		super.toSendUpdate(packet.prepareUpdateSegment(), this.inputStream, this.outputStream, BgpProducer.log);
	}
	public void addPeer(String s){
		bgpConfig.getRuleStore().addPeers(s+"=="+this.getName(), (BgpOperations)this);
	}

	public int getCountKA(){
		return this.countKA;
	}
	public int getCountOpen(){
		return this.countOpen;
	}
	public int getCountUpdate(){
		return this.countUpdate;
	}   
	public int getCountNotification(){
		return this.countNotification;
	}   

	public FSMState getFsmState() {
		return fsmState;
	}   

	public void setFsmState(FSMState fsmState) {
		if(!this.fsmState.equals(fsmState)){
			if(fsmState == FSMState.ESTABLISHED){//When first establish happens
				addPeer(nameOfRouterConnectedTo);
				this.link.setDestinationId(nameOfRouterConnectedTo);
				this.link.setAlive(true);
			}if(FSMState.ESTABLISHED == this.fsmState && FSMState.IDLE == fsmState){//Enters ideal from established, ie connection is lost.
				getBgpConfig().getRuleStore().removeRulesFrom(nameOfRouterConnectedTo+"=="+this.getName());
			}
		}
		this.fsmState = fsmState;
	}

	public int getCountOthers(){
		return this.countOthers;
	}   
	public int getCountTotal(){
		return this.totalBgpPackets;
	}   
	public int getCountMalformed(){
		return this.countOthers;
	}   

	public BgpConfig getBgpConfig(){
		return this.bgpConfig;
	}   
}

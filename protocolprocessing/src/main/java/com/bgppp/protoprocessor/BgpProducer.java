package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.log4j.*;

import com.bgppp.protoprocessor.utils.TimeOutUtils;
import com.bgppp.protoprocessor.utils.ControlledRandom;
import com.bgppp.protoprocessor.utils.GetProperties;

public class BgpProducer extends BgpOperations implements TimerListener{
	public static Logger log = Logger.getLogger(BgpProducer.class.getName());
	private BgpConfig bgpConfig = null;
	private Link link = null;
	private int PORT = GetProperties.bgpPort;
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
	private FSMState fsmState = null;
	public boolean isRunning() {
		return isRunning;
	}
	public void setRunning(boolean isRunning) {
		if(!isRunning)this.kaTimer.setRunning(false);
		this.isRunning = isRunning;
		setFsmState(FSMState.IDLE);
	}
	public BgpProducer(BgpConfig bgpConfig, Link link){
		this.bgpConfig = bgpConfig;
		this.link = link;
		this.setName(bgpConfig.getRouterName()+"_producer_"+link.getDestinationAddress());
		this.kaTimer = new KeepAliveTimer(this.getName(), (new Date()).getTime(), this);
		this.connectRetryTimer = new ConnectRetryTimer("", (new Date()).getTime(), this);
		this.holdTimer = new HoldTimer("", (new Date()).getTime(), this);
	}
	
	@Override
	public void timeUp() {
		try {
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
		setRunning(false);
	}

	@Override
	public void run(){
		setFsmState(FSMState.IDLE);
		log.info("Starting Producer : " + bgpConfig.getRouterName() + ":" +link);
		InetSocketAddress inetSocketAddress = new InetSocketAddress(link.getDestinationAddress(), PORT);
		socket = new Socket();
		boolean isConnected = false;
		boolean bgpOpenCommandProcessed = false;
		try{
			int randomInt = ControlledRandom.nextInt(2,10);
			log.info("Sleeping for " + (randomInt*1000) );
			BgpProducer.sleep(randomInt * 1000);
		}catch(InterruptedException e){
			log.error(e.getMessage());
		}
		this.kaTimer.start();
		while(isRunning()){
			log.info(isConnected+"/"+bgpOpenCommandProcessed+"/"+link.isAlive());
			try {
				if(!isConnected){
					socket.setSoTimeout(TimeOutUtils.READ_SOMTIMEOUT);
					log.info("Starting connection");
					socket.connect(inetSocketAddress);
					outputStream = new DataOutputStream(socket.getOutputStream());
					inputStream = new DataInputStream(socket.getInputStream());
					this.kaSender = new KeepAliveSender("KAS-"+this.getName(),inputStream, outputStream, log);
					isConnected = true;
					setFsmState(FSMState.CONNECT);
					log.info("Connected");
				}
			}catch (UnknownHostException e) {
				log.error("Unknown Host "+e.getMessage());
				e.printStackTrace();
			} catch(SocketTimeoutException exception){
				log.error("No response from " + link.getDestinationAddress());
				link.setAlive(false);
			} catch(ConnectException exception){
				try {
					log.error("Could not connect to " + link.getDestinationAddress());
					isConnected = false;
					link.setAlive(false);
					Thread.sleep(TimeOutUtils.RECONNECT_TIME);
				} catch (InterruptedException e) {
					log.error("Interrupter " + e.getMessage());
					e.printStackTrace();
				}
			} catch (IOException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
			if(isConnected && !bgpOpenCommandProcessed){
				log.info(socket.getLocalAddress().toString());
				bgpOpenCommandProcessed = toSendOPEN(inputStream, outputStream, log, socket.getLocalAddress().toString());
				setFsmState(FSMState.OPEN_SENT);
			}
			if(isConnected && bgpOpenCommandProcessed){
				log.info("Connected and open command sent");
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
							processPacket(getInt(packLen),getInt(packType),packRest);
						}
					}
					log.info("CT exited isRunning");
				}catch(Exception e){ 
					timeUp();
				}
			}
		}
		if(!isRunning()){
			log.info("Someone set running to false");
			try {
				if(socket!=null)socket.close();
				if(inputStream!=null)inputStream.close();
				if(outputStream!=null)outputStream.close();
			} catch (IOException e) {
				log.error("Error closing connections" + e.getMessage());
			}
		}
	}

	public void processPacket(int packLen,int packType,byte[] packRest){
		log.info("Packet received, type "+packType+" length "+ packLen);
		switch(packType){
			case 4: log.info("KeepAlive Packet");
					countKA++;
					this.kaTimer.resetCounter();
					if(!kaSender.isRunning())kaSender.start();
					setFsmState(FSMState.ESTABLISHED);
					break;
			case 1: log.info("Open Packet");
					this.kaTimer.resetCounter();
					if(!kaSender.isRunning())kaSender.start();
					setFsmState(FSMState.OPEN_CONFIRM);
					countOpen++;
					break;
			case 2: log.info("Update Packet");
					countUpdate++;
					break;
			case 3: log.info("Notificaiton Packet");
					countNotification++;
					break;
			default:log.info("Malformed Packet");
					countOthers++;
					break;
		}

	}   

	private void toSendUPDATE(){

	}
	private void toSendNOTIFICATION(){

	}

	private String readResponse(DataInputStream inStream){
		String response = "";
		try {
			response = inStream.readLine();
			return response;
		} catch(SocketTimeoutException exception){
			log.info("Waited for SOME response till "+TimeOutUtils.READ_SOMTIMEOUT+" seconds, nothing happened.");
			setFsmState(FSMState.IDLE);
			link.setAlive(false);
			setRunning(false);
		} catch (IOException e) {
			log.info(e.getMessage());
		}
		return response;
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
	public boolean isProducerAlive(){
		return this.link.isAlive();
	}

}

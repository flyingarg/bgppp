package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import org.apache.log4j.*;

import com.bgppp.protoprocessor.timers.ConnectRetryTimer;
import com.bgppp.protoprocessor.timers.HoldTimer;
import com.bgppp.protoprocessor.timers.KeepAliveSender;
import com.bgppp.protoprocessor.timers.KeepAliveTimer;
import com.bgppp.protoprocessor.timers.TimerListener;

public class BgpConsumerThread extends BgpOperations implements TimerListener{
	public static Logger log = Logger.getLogger(BgpConsumerThread.class.getName());
	public Socket listen = null;
	public DataInputStream inputStream = null;
	public DataOutputStream outputStream = null;
	public boolean running = true;
	public boolean alive = true;
	public Long keepAliveTimer = (new Date()).getTime();
	public KeepAliveTimer kaTimer = null;
	private KeepAliveSender kaSender = null;
	public ConnectRetryTimer connectRetryTimer = null;
	public HoldTimer holdTimer = null;
	private BgpProducer bgpProducer = null;
	private BgpConsumer consumer = null;

	private int totalBgpPackets = 0;
	private int countKA = 0;
	private int countOpen = 0;
	private int countUpdate = 0;
	private int countOthers = 0;
	private int countNotification = 0;

	public BgpConsumerThread(BgpConsumer consumer, Socket listen, DataInputStream inputStream, DataOutputStream outputStream, BgpProducer bgpProducer, String name){
		this.listen = listen;
		this.consumer = consumer;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.bgpProducer = bgpProducer;
		name = name + listen.getRemoteSocketAddress();
		this.setName("CT-"+name);
		this.kaTimer = new KeepAliveTimer(this.getName(), (new Date()).getTime(), this);this.kaTimer.start();
		this.connectRetryTimer = new ConnectRetryTimer("", (new Date()).getTime(), this);
		this.holdTimer = new HoldTimer("", (new Date()).getTime(), this);
		this.kaSender = new KeepAliveSender("KAS-"+this.getName(),inputStream, outputStream, log);
	}

	@Override
	public void run(){
		try{
			consumer.setFsmState(FSMState.ACTIVE);
			byte[] packRest = null;
			int ffByteCount = 0;
			while(this.isRunning()){
				if(!this.kaTimer.isRunning()){
					this.setRunning(false);
					listen.close();
				}
				byte bite = 0;
				bite = inputStream.readByte();
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
		}catch(EOFException eofe){
			try{
				timeUp();
			}catch(Exception w){
				log.error("Encountered EOF");
			}
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}

	public void processPacket(int packLen,int packType,byte[] packRest){
		log.info("Packet received, type "+packType+" length "+ packLen);
		switch(packType){
			case 4: log.info("KeepAlive Packet");
					countKA++;
					this.kaTimer.resetCounter();
					consumer.setFsmState(FSMState.ESTABLISHED);
					//TODO : Start rule handling
					break;
			case 1: log.info("Open Packet");
					consumer.setFsmState(FSMState.OPEN_CONFIRM);
					toSendOPEN(inputStream, outputStream, log, listen.getLocalAddress().toString());
					consumer.setFsmState(FSMState.OPEN_SENT);
					kaSender.start();
					this.kaTimer.resetCounter();
					countOpen++;
					break;
			case 2: log.info("Update Packet");
					toSendUpdate(inputStream, outputStream, log);
					countUpdate++;
					break;
			case 3: log.info("Notificaiton Packet");
					toSendNotification(inputStream, outputStream, log);
					countNotification++;
					break;
			default:log.info("Malformed Packet");
					countOthers++;
					break;
		}

	}

	/**
	 * @return the bgpProducer
	 */
	public BgpProducer getBgpProducer() {
		return bgpProducer;
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
	public int getCountOthers(){
		return this.countOthers;
	}
	public int getCountTotal(){
		return this.totalBgpPackets;
	}
	public int getCountMalformed(){
		return this.countOthers;
	}
	public boolean isRunning(){
		return this.running;
	}
	public void setRunning(boolean isRunning){
		this.running = isRunning;
	}
	@Override
	public void timeUp(){
		try {
			listen.close();
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
}

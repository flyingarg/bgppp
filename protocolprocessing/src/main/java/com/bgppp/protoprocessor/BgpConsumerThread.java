package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;

import org.apache.log4j.*;

import com.bgppp.protoprocessor.packet.*;
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

	private boolean isEstablished = false;

	public String nameOfRouterConnectedTo = "";
	public String destinationAddress = "";	
	public BgpConsumerThread(BgpConsumer consumer, Socket listen, DataInputStream inputStream, DataOutputStream outputStream, BgpProducer bgpProducer, String name){
		this.listen = listen;
		this.consumer = consumer;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.bgpProducer = bgpProducer;
		this.setName(consumer.getBgpConfig().getRouterName()+"C("+listen.getLocalAddress().toString()+")->" + listen.getRemoteSocketAddress().toString());
		this.kaTimer = new KeepAliveTimer(this.getName(), (new Date()).getTime(), this);this.kaTimer.start();
		this.connectRetryTimer = new ConnectRetryTimer("", (new Date()).getTime(), this);
		this.holdTimer = new HoldTimer("", (new Date()).getTime(), this);
		this.kaSender = new KeepAliveSender("KAS-"+this.getName());
		this.kaSender.setInputStream(inputStream);
		this.kaSender.setOutputStream(outputStream);
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
					if(getInt(packType) == 2 || getInt(packType) == 1 ){
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
		}catch(EOFException eofe){
			try{
				timeUp();
			}catch(Exception w){
				log.error("Encountered EOF");
				log.error("parsing packet : " + w.getMessage());
				w.printStackTrace();
			}
		}catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void processPacket(int packLen,int packType,byte[] packRest){
		String type="";
		if(packType == 4)type="KeepAlive";else if(packType == 1)type="Open";else if(packType == 2)type="Update";else if(packType == 3)type="Notification";
		log.info("Packet received, type "+type+" length "+ packRest.length);
		switch(packType){
			case 4: countKA++;
					this.kaTimer.resetCounter();
					if(!isEstablished){
						consumer.setFsmState(FSMState.ESTABLISHED);
						log.info("C("+listen.getLocalAddress().toString()+")  in ESTABLISHED state with " + listen.getRemoteSocketAddress().toString());
						this.isEstablished = true;
					}
					break;
			case 1: consumer.setFsmState(FSMState.OPEN_CONFIRM);
					String asNumber = consumer.getBgpConfig().getRouterName();
					destinationAddress = listen.getRemoteSocketAddress().toString().split(":")[0].replaceAll("\\/", "");
					toSendOPEN(inputStream, outputStream, log, destinationAddress, asNumber);
					consumer.setFsmState(FSMState.OPEN_SENT);
					BgpOpenPacket op = new BgpOpenPacket(packRest);
					this.nameOfRouterConnectedTo = ""+op.getAsNumber();
					kaSender.start();
					this.kaTimer.resetCounter();
					countOpen++;
					break;
			case 2: countUpdate++;
					BgpUpdatePacket packet = new BgpUpdatePacket(packRest);
					String bgpOperationName = this.nameOfRouterConnectedTo + "==" + this.getName();
					consumer.getBgpConfig().getRuleStore().addRule(packet.getRule(this.nameOfRouterConnectedTo, bgpOperationName));
					break;
			case 3: toSendNotification(inputStream, outputStream, log);
					countNotification++;
					break;
			default:countOthers++;
					break;
		}

	}
	
	public void toSendUpdate(BgpUpdatePacket updatePacket){
		super.toSendUpdate(updatePacket.prepareUpdateSegment(), this.inputStream, this.outputStream, BgpProducer.log);
	}

	/**
	 * @return the bgpProducer
	 */
	public BgpProducer getBgpProducer() {
		return bgpProducer;
	}

	/**
	 * @return the consumer
	 */
	public BgpConsumer getConsumer() {
		return consumer;
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
			consumer.setFsmState(FSMState.IDLE);
			this.kaTimer.setRunning(false);
			this.connectRetryTimer.setRunning(false);
			this.holdTimer.setRunning(false);
			this.kaSender.setRunning(false);
			setRunning(false);
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		setRunning(false);
	}
}

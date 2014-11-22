package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import org.apache.log4j.*;

public class BgpConsumerThread extends Thread implements KeepAliveListener{
	public static Logger log = Logger.getLogger(BgpConsumerThread.class.getName());
	public Socket listen = null;
	public DataInputStream inputStream = null;
	public DataOutputStream outputStream = null;
	public boolean running = true;
	public boolean alive = true;
	public Long keepAliveTimer = (new Date()).getTime();
	public KeepAliveCountdown countdown = null;
	
	private int totalBgpPackets = 0;
	private int countKA = 0;
	private int countOpen = 0;
	private int countUpdate = 0;
	private int countOthers = 0;
	private int countNotification = 0;
	private boolean open = false;

	public BgpConsumerThread(Socket listen, DataInputStream inputStream, DataOutputStream outputStream/*, KeepAliveCountdown countdown*/){
		this.listen = listen;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	@Override
	public void run(){
		//Is it safe to pass 'this' ??
		this.countdown = new KeepAliveCountdown("", (new Date()).getTime(), this);
		Date date = new Date();
		Long timer = date.getTime();
		try{
			byte[] packRest = null;
			int ffByteCount = 0;
			while(this.isRunning()){
				if((new Date().getTime()) > (timer)+60000){
					this.setRunning(false);
					log.info("Now " + (new Date().getTime()));
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
		}catch(EOFException eofe){
			log.error(eofe.getMessage());
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}

	public void processPacket(int packLen,int packType,byte[] packRest){
		log.info("Packet received, type "+packType+" length "+ packLen);
		switch(packType){
			case 4: log.info("KeepAlive Packet");
					countKA++;
					this.countdown.resetCounter();
					break;
			case 1: log.info("Open Packet");
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		setRunning(false);
	}
	public int getInt(byte[] bite){
		String strInt = "";
		for(int j=bite.length-1;j>-1;j--){
			if (bite[j]<0)
				strInt = (127 - bite[j]) + strInt;
			else
				strInt = bite[j] + strInt;
		}
		Integer r = new Integer(strInt);
		return r.intValue();
	}
}

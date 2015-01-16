package com.bgppp.protoprocessor.timers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.bgppp.protoprocessor.BgpOperations;
import com.bgppp.protoprocessor.utils.TimeOutUtils;

public class KeepAliveSender extends BgpOperations{
	private static Logger log = Logger.getLogger(KeepAliveSender.class.getName());

	DataInputStream inputStream = null;
	DataOutputStream outputStream = null;
	boolean isRunning = false;
	
	@SuppressWarnings("static-access")
	public KeepAliveSender(String name, DataInputStream inputStream, DataOutputStream outputStream, Logger log){
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.log = log;
		this.setName(name + UUID.randomUUID());
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void run(){
		log.info("Starting Keep Alive Sender at " + this.getName());
		isRunning = true;
		while(isRunning){
			try{
				log.info("Sending keepalive packet");
				toSendKEEPALIVE(inputStream, outputStream, log);
				KeepAliveSender.sleep(TimeOutUtils.SENDKEEPALIVE);
			}catch(InterruptedException e){
				log.error(e.getMessage());
			}
		}
	}
}

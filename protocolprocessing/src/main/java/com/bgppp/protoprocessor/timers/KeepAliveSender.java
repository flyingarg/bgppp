package com.bgppp.protoprocessor.timers;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.apache.log4j.Logger;

import com.bgppp.protoprocessor.BgpOperations;
import com.bgppp.protoprocessor.utils.TimeOutUtils;

import java.util.Date;

public class KeepAliveSender extends BgpOperations{
	private static Logger log = Logger.getLogger(KeepAliveSender.class.getName());

	DataInputStream inputStream = null;
	DataOutputStream outputStream = null;
	boolean isRunning = false;
	
	public KeepAliveSender(String name){
		this.setName("KS-" + name);
	}

	/**
	 * @param inputStream the inputStream to set
	 */
	public void setInputStream(DataInputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * @param outputStream the outputStream to set
	 */
	public void setOutputStream(DataOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	public void setRunning(boolean isRunning) {
		log.info("KAS running set to " + isRunning);
		this.isRunning = isRunning;
	}

	public void run(){
		log.info("Starting Keep Alive Sender at " + this.getName());
		log.info(inputStream == null);
		log.info(outputStream == null);
		this.isRunning = true;
		while(this.isRunning){
			try{
				boolean response = toSendKEEPALIVE(inputStream, outputStream, log);
				if(response == false)
					setRunning(false);
				Long temp = (new Date()).getTime();
				KeepAliveSender.sleep(TimeOutUtils.SENDKEEPALIVE);
				log.info("After sending the KA packet, KAS slept for " + ((new Date()).getTime()-temp) + " milli seconds");
			}catch(InterruptedException e){
				log.error(e.getMessage());
			}
		}
	}
}

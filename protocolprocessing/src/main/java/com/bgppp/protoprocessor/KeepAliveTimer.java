package com.bgppp.protoprocessor;

import java.util.*;

import org.apache.log4j.Logger;

import com.bgppp.protoprocessor.utils.TimeOutUtils;

public class KeepAliveTimer extends Thread{
	public static Logger log = Logger.getLogger(KeepAliveTimer.class.getName());
	boolean isRunning = true;
	Long counter = null;
	TimerListener keepAliveListener = null;
	boolean touched = false;
	public KeepAliveTimer(String name, Long counter, TimerListener keepAliveListener){
		this.counter = counter;
		this.setName("KAT-"+name+UUID.randomUUID());
		this.keepAliveListener = keepAliveListener;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public synchronized void resetCounter(){
		Long temp =  counter;
		this.counter = (new Date()).getTime() + TimeOutUtils.KEEPALIVE;
		log.info("KeepAliveTimer reset after : " + (counter - temp) );
		touched = true;
	}

	public void run() {
		int i = 0;
		while (isRunning) {
			i = i + 1;
			log.info("KeepAliveTimer " + i);
			try{
				KeepAliveTimer.sleep(1000);
			}catch(Exception e){
				log.error(e.getMessage());
			}
			if(touched){
				break;
			}
		}
		runner();
	}

	private void runner(){
		while (isRunning) {
			try {
				log.info("KeepAliveTimer "+((this.counter) - (new Date()).getTime()));
				if(this.counter > (new Date()).getTime()){
					KeepAliveTimer.sleep(TimeOutUtils.KEEPALIVE);
				}else{
					log.info("No KEEPALIVE for too long");
					keepAliveListener.timeUp();
					setRunning(false);
				}
			} catch (InterruptedException e) {
				log.info("Failed to send keepalive message");
			}
		}

	}

}

package com.bgppp.protoprocessor.timers;

import java.util.*;

import org.apache.log4j.Logger;

import com.bgppp.protoprocessor.utils.TimeOutUtils;

public class KeepAliveTimer extends Thread{
	public static Logger log = Logger.getLogger(KeepAliveTimer.class.getName());
	boolean isRunning = true;
	Long counter = null;
	TimerListener keepAliveListener = null;
	boolean touched = false;
	String uuid = "KAT-"+UUID.randomUUID().toString();
	public KeepAliveTimer(String name, Long counter, TimerListener keepAliveListener){
		this.counter = counter;
		this.setName("KAT-"+name);
		this.keepAliveListener = keepAliveListener;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public synchronized void resetCounter(){
		log.info(uuid + " KeepAliveTimer, time left to rest : " + ((new Date()).getTime() - this.counter) );
		this.counter = (new Date()).getTime() + TimeOutUtils.KEEPALIVE;
		touched = true;
	}

	public void run() {
		int i = 0;
		while (isRunning) {
			i = i + 1;
			try{
				KeepAliveTimer.sleep(1000);
			}catch(Exception e){
				log.error(e.getMessage());
				e.printStackTrace();
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
				//log.info(uuid + " KeepAliveTimer "+((this.counter) - (new Date()).getTime()));
				if(this.counter > (new Date()).getTime()){
					//KeepAliveTimer.sleep(TimeOutUtils.KEEPALIVE);
					KeepAliveTimer.sleep(1000);
				}else{
					log.info(uuid + " No KEEPALIVE for too long");
					keepAliveListener.timeUp();
					setRunning(false);
				}
			} catch (InterruptedException e) {
				log.error(uuid + " Failed to send keepalive message");
			}
		}

	}
}

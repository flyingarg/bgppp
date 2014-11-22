package com.bgppp.protoprocessor;

import java.io.IOException;
import java.util.Date;

import com.bgppp.protoprocessor.graphs.TimeOutUtils;
import org.apache.log4j.*;

public class KeepAliveCountdown extends Thread{
	public static Logger log = Logger.getLogger(KeepAliveListener.class.getName());
	boolean isRunning = true;
	Long counter = null;
	KeepAliveListener keepAliveListener = null;
	String linkBetween = "";

	public KeepAliveCountdown(String linkBetween, Long counter, KeepAliveListener keepAliveListener){
		this.counter = counter;
		this.linkBetween = linkBetween;
		this.keepAliveListener = keepAliveListener;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public synchronized void resetCounter(){
		this.counter = (new Date()).getTime();
	}

	public void run() {
		while (isRunning) {
			try {
				if(this.counter + TimeOutUtils.KEEPALIVE > (new Date()).getTime()){
					this.sleep(TimeOutUtils.KEEPALIVE);
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

package com.bgppp.protoprocessor.timers;

import java.io.IOException;
import java.util.Date;

import com.bgppp.protoprocessor.utils.TimeOutUtils;

import org.apache.log4j.*;

@SuppressWarnings("unused")
public class HoldTimer extends Thread{
	public static Logger log = Logger.getLogger(HoldTimer.class.getName());
	boolean isRunning = true;
	Long counter = null;
	TimerListener timerListener = null;
	String linkBetween = "";

	public HoldTimer(String linkBetween, Long counter, TimerListener timerListener){
		this.counter = counter;
		this.linkBetween = linkBetween;
		this.timerListener = timerListener;
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

	@SuppressWarnings("static-access")
	public void run() {
		while (isRunning) {
			try {
				if(this.counter + TimeOutUtils.KEEPALIVE > (new Date()).getTime()){
					this.sleep(TimeOutUtils.KEEPALIVE);
				}else{
					log.info("No KEEPALIVE for too long");
					timerListener.timeUp();
					setRunning(false);
				}
			} catch (InterruptedException e) {
				log.info("Failed to send keepalive message");
			}
		}
	}

}

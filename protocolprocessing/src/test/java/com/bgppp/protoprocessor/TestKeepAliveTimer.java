package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;

import org.apache.log4j.Logger;

import com.bgppp.protoprocessor.timers.KeepAliveSender;
import com.bgppp.protoprocessor.timers.KeepAliveTimer;
import com.bgppp.protoprocessor.timers.TimerListener;
import com.bgppp.protoprocessor.utils.TimeOutUtils;

public class TestKeepAliveTimer extends Thread {

	public static KeepAliveTimer kaTimer = null;

	public static MockKeepAliveSender kaSender = null;
	
	public static void main(String args[]) {
		TimerListener timerListener = null;
		timerListener = new TimerListener() {
			public void timeUp() {
				System.out.println("timeUp");
			}
		};
		kaTimer = new KeepAliveTimer("Hello", (new Date()).getTime(), timerListener);
		kaTimer.start();
		Thread t = new TestKeepAliveTimer();
		t.start();
	}

	public void run() {
		System.out.println("Started");
		kaSender = new MockKeepAliveSender("Sender", new DataInputStream(null), new DataOutputStream(null), Logger.getLogger(TestKeepAliveTimer.class.getName()), kaTimer);
		kaSender.start();
	}
}

class MockKeepAliveSender extends KeepAliveSender{

	KeepAliveTimer kaTimer = null;
	
	public MockKeepAliveSender(String name, DataInputStream inputStream,
			DataOutputStream outputStream, Logger log, KeepAliveTimer kaTimer) {
		super(name, inputStream, outputStream, log);
		this.kaTimer = kaTimer;
	}
	
	public void run(){
		System.out.println("Starting Sender");
		setRunning(true);
		while(isRunning()){
			try{
				KeepAliveSender.sleep(TimeOutUtils.SENDKEEPALIVE);
				kaTimer.resetCounter();
			}catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		}
	}
}


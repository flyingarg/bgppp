package com.bgppp.protoprocessor.timers;

import java.util.*;

import org.apache.log4j.*;

import com.bgppp.protoprocessor.rules.*;
import com.bgppp.protoprocessor.packet.BgpUpdatePacket;
import com.bgppp.protoprocessor.utils.TimeOutUtils;
import com.bgppp.protoprocessor.*;

public class DelaySendingUpdate extends Thread{
	private static final Logger log = Logger.getLogger(DelaySendingUpdate.class);
	private List<Rule> rules = new ArrayList<Rule>();
	private BgpOperations operation = null;
	private boolean running;

	public DelaySendingUpdate(BgpOperations bgpOperation){
		rules = new ArrayList<Rule>();
		running = false;
		this.operation = bgpOperation;
	}

	@Override
	public void run(){
		try{
		while(isRunning()){
			Thread.sleep(TimeOutUtils.DELAY_TIME);
			for(Rule rule : this.rules){
				BgpUpdatePacket packet = new BgpUpdatePacket(rule);
				if(this.operation instanceof BgpProducer){
					BgpProducer instance = (BgpProducer) this.operation;
					instance.toSendUpdate(packet);
				}else if(this.operation instanceof BgpConsumerThread){
					BgpConsumerThread instance = (BgpConsumerThread) this.operation;
					instance.toSendUpdate(packet);
				}   
			}
			setRunning(false);
		}
		}catch(InterruptedException e){
			log.error(e.getMessage());
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void addRule(Rule rule){
		this.rules.add(rule);
	}

}

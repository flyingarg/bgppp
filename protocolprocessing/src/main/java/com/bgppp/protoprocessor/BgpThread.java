package com.bgppp.protoprocessor;

import org.apache.log4j.*;

import com.bgppp.protoprocessor.monitor.*;

public class BgpThread extends Thread {

	private BgpConfig config;
	public static Logger log = Logger.getLogger(BgpThread.class.getName());
	private boolean isRunning = true;
	public BgpThread(BgpConfig config) {
		this.config = config;
	}

	@Override
	public void run() {
		for (Link link : config.getLinks()) {
			BgpConsumer consumer = new BgpConsumer(config, link);
			ProducerConsumerStore.addBgpConsumer(consumer);
			consumer.start();
			while(!consumer.isRunning()){
				log.info("Consumer still not running, sleep some more");
				try {
					this.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			BgpProducer producer = new BgpProducer(config, link);
			ProducerConsumerStore.addBgpProducer(producer);
			producer.start();
			consumer.setBgpProducer(producer);
		}
	}

}

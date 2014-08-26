package com.bgppp.protoprocessor;

import java.util.logging.Logger;


public class BgpThread extends Thread {

	private BgpConfig config;
	public static Logger log = Logger.getLogger(BgpThread.class.getName());
	public static final int PORT = 179;
	private boolean isRunning = true;
	public BgpThread(BgpConfig config) {
		this.config = config;
	}

	@Override
	public void run() {
		for (Link link : config.getLinks()) {
			BGPConsumer consumer = new BGPConsumer(config, link);
			MonitorProducerConsumer.addBgpConsumer(consumer);
			consumer.start();
			while(!consumer.isRunning()){
				log.info("Consumer still not running, sleep some more");
				try {
					this.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			BGPProducer producer = new BGPProducer(config, link);
			MonitorProducerConsumer.addBgpProducer(producer);
			producer.start();
		}
	}

}

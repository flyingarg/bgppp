package com.bgppp.protoprocessor;

import org.apache.log4j.*;

import java.util.HashMap;

import com.bgppp.protoprocessor.remote.SshServerDaemon;

public class BgpThread extends Thread {

	private BgpConfig config;
	private HashMap<String, BgpConsumer> consumers = new HashMap<String, BgpConsumer>();
	private HashMap<String, BgpProducer> producers = new HashMap<String, BgpProducer>();
	public static Logger log = Logger.getLogger(BgpThread.class.getName());
	//private boolean isRunning = true;
	public BgpThread(BgpConfig config) {
		this.config = config;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		NodeStore.addNode(config);
		//Start the threads
		for (Link link : config.getLinks()) {
			NodeStore.addPath(link);
			BgpConsumer consumer = new BgpConsumer(config, link);
			//ProducerConsumerStore.addBgpConsumer(consumer);
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
			//ProducerConsumerStore.addBgpProducer(producer);
			producer.start();
			consumer.setBgpProducer(producer);
		}
		//Start one ssh server on each router!!
		SshServerDaemon server = new SshServerDaemon(config);
		server.start();
	}

	public HashMap<String, BgpConsumer> getConsumers() {
		return consumers;
	}
	public void addConsumers(String name ,BgpConsumer consumers) {
		this.consumers.put(name, consumers);
	}
	public void removeConsumers(String name) {
		this.consumers.remove(name);
	}

	
	public HashMap<String, BgpProducer> getProducers() {
		return producers;
	}
	public void addProducer(String name ,BgpProducer producer) {
		this.producers.put(name, producer);
	}
	public void removeProducer(String name) {
		this.producers.remove(name);
	}
	
}

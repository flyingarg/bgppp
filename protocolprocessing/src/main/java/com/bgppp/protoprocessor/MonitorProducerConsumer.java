package com.bgppp.protoprocessor;

import java.util.HashMap;

public class MonitorProducerConsumer {

	private static HashMap<String, BGPProducer> bgpProducer = new HashMap<String, BGPProducer>();
	private static HashMap<String, BGPConsumer> bgpConsumer = new HashMap<String, BGPConsumer>();
	
	private MonitorProducerConsumer(){
		
	}

	public static synchronized BGPProducer getBgpProducerByName(String name){
		return MonitorProducerConsumer.bgpProducer.get(name);
	}
	public static synchronized void addBgpProducer(BGPProducer bgpProducer){
		MonitorProducerConsumer.bgpProducer.put(bgpProducer.getName(), bgpProducer);
	}
	public static synchronized BGPConsumer getBgpConsumerByName(String name){
		return MonitorProducerConsumer.bgpConsumer.get(name);
	}
	public static synchronized void addBgpConsumer(BGPConsumer bgpConsumer){
		MonitorProducerConsumer.bgpConsumer.put(bgpConsumer.getName(), bgpConsumer);
	}
	
	
	public static synchronized HashMap<String, BGPProducer> getBgpProducer() {
		return bgpProducer;
	}

	public static synchronized void setBgpProducer(HashMap<String, BGPProducer> bgpProducer) {
		MonitorProducerConsumer.bgpProducer = bgpProducer;
	}

	public static synchronized HashMap<String, BGPConsumer> getBgpConsumer() {
		return bgpConsumer;
	}

	public static synchronized void setBgpConsumer(HashMap<String, BGPConsumer> bgpConsumer) {
		MonitorProducerConsumer.bgpConsumer = bgpConsumer;
	}
	
}

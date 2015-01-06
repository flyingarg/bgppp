package com.bgppp.protoprocessor;

import java.util.HashMap;

public class ProducerConsumerStore {

	private static HashMap<String, BgpProducer> bgpProducersMap = null;
	private static HashMap<String, BgpConsumer> bgpConsumersMap = null;
	private static boolean hasNewUpdates = false;

	private ProducerConsumerStore(){
		ProducerConsumerStore.bgpProducersMap = new HashMap<String, BgpProducer>();
		ProducerConsumerStore.bgpConsumersMap = new HashMap<String, BgpConsumer>();
	}

	// Producer 
	public static synchronized BgpProducer getBgpProducerByName(String name){
		return ProducerConsumerStore.getBgpProducersMap().get(name);
	}
	public static synchronized void addBgpProducer(BgpProducer bgpProducer){
		ProducerConsumerStore.getBgpProducersMap().put(bgpProducer.getName(), bgpProducer);
		ProducerConsumerStore.hasNewUpdates = true;
	}
	public static synchronized void removeBgpProducer(BgpProducer bgpProducer){
		ProducerConsumerStore.getBgpProducersMap().remove(bgpProducer.getName());
		ProducerConsumerStore.hasNewUpdates = true;
	}
	
	//Consumer
	public static synchronized BgpConsumer getBgpConsumerByName(String name){
		return ProducerConsumerStore.getBgpConsumersMap().get(name);
	}
	public static synchronized void addBgpConsumer(BgpConsumer bgpConsumer){
		ProducerConsumerStore.getBgpConsumersMap().put(bgpConsumer.getName(), bgpConsumer);
		ProducerConsumerStore.hasNewUpdates = true;
	}
	public static synchronized void removeBgpConsumer(BgpConsumer bgpConsumer){
		ProducerConsumerStore.getBgpConsumersMap().remove(bgpConsumer.getName());
		ProducerConsumerStore.hasNewUpdates = true;
	}
	

	/*
	 * TODO : Remove setters as ProducerConsumerStore is a singleton, change the store variable name, is confusing !!
	 */
	public static synchronized HashMap<String, BgpProducer> getBgpProducersMap() {
		if (ProducerConsumerStore.bgpProducersMap == null)
			ProducerConsumerStore.bgpProducersMap = new HashMap<String, BgpProducer>();
		return ProducerConsumerStore.bgpProducersMap;
	}

	/*public static synchronized void setBgpProducer(HashMap<String, BgpProducer> bgpProducer) {
		ProducerConsumerStore.bgpProducer = bgpProducer;
	}*/

	public static synchronized HashMap<String, BgpConsumer> getBgpConsumersMap() {
		if(ProducerConsumerStore.bgpConsumersMap == null)
			ProducerConsumerStore.bgpConsumersMap = new HashMap<String, BgpConsumer>();
		return ProducerConsumerStore.bgpConsumersMap;
	}

	/*public static synchronized void setBgpConsumer(HashMap<String, BgpConsumer> bgpConsumer) {
		ProducerConsumerStore.bgpConsumer = bgpConsumer;
	}*/
	
	public static boolean hasNewUpdates(){
		return ProducerConsumerStore.hasNewUpdates;
	}
}

package com.bgppp.protoprocessor;

import java.util.HashMap;

import org.apache.log4j.*;

public class ProducerConsumerStore extends NodeStore{
	public static final Logger log = Logger.getLogger(ProducerConsumerStore.class);

	private static HashMap<String, BgpConfig> bgpConfigMap = null;
	private static boolean hasNewUpdates = false;
	public ProducerConsumerStore(){
		ProducerConsumerStore.bgpConfigMap = new HashMap<String, BgpConfig>();
	}
	
	public static boolean hasNewUpdates(){
		return ProducerConsumerStore.hasNewUpdates;
	}
	public static HashMap<String, BgpConfig> getBgpConfigMap() {
		if(bgpConfigMap == null){
			ProducerConsumerStore.bgpConfigMap = new HashMap<String, BgpConfig>();
		}
		return bgpConfigMap;
	}

	/**
	 * Get a BgpConfig(Router) by router name.
	 */
	public static BgpConfig getBgpConfigByName(String name) {
		if(bgpConfigMap == null){
			ProducerConsumerStore.bgpConfigMap = new HashMap<String, BgpConfig>();
		}
		return ProducerConsumerStore.bgpConfigMap.get(name);
	}
	/**
	 * Add a BgpConfig(Router) to the ProducerConsumerStore
	 */
	public static void addBgpConfig(String name, BgpConfig bgpConfig) {
		if(bgpConfigMap == null){
			ProducerConsumerStore.bgpConfigMap = new HashMap<String, BgpConfig>();
		}
		ProducerConsumerStore.bgpConfigMap.put(name, bgpConfig);
		ProducerConsumerStore.hasNewUpdates = true;
	}
	/**
	 * Remove a BgpConfig(Router) to the ProducerConsumerStore
	 */
	public static void removeBgpConfig(String name) {
		if(bgpConfigMap == null){
			ProducerConsumerStore.bgpConfigMap = new HashMap<String, BgpConfig>();
		}
		ProducerConsumerStore.bgpConfigMap.remove(name);
		ProducerConsumerStore.hasNewUpdates = true;
	}

	/**
	 * Router(BgpConfig) creates consumers and those are stored here, We can now get a consumer based on the link object.
	 */
	public static BgpConsumer getConsumerOfLink(Link link){
		String routerName = link.getPathName().split("-")[0].trim();
		String consumerOfThisLink = routerName + "_consumer_" + link.getSourceAddress();
		return ProducerConsumerStore.getBgpConfigByName(routerName).getConsumers().get(consumerOfThisLink);
	}
	/**
	 * Router(BgpConfig) creates producers and those are stored here, We can now get a consumer based on the link object.
	 */
	public static BgpProducer getProducerOfLink(Link link){
		String routerName = link.getPathName().split("-")[0].trim();
		String producerOfThisLink = routerName + "_producer_" + link.getSourceAddress();
		return ProducerConsumerStore.getBgpConfigByName(routerName).getProducers().get(producerOfThisLink);
	}
}

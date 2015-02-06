package com.bgppp.protoprocessor;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.*;
import com.bgppp.protoprocessor.graphs.GraphNode;
import com.bgppp.protoprocessor.utils.AddressAndMask;
import com.bgppp.protoprocessor.remote.SshServerDaemon;

public class BgpConfig extends GraphNode{
	public static Logger log = Logger.getLogger(BgpConfig.class.getName());
	private HashMap<String, BgpConsumer> consumers = new HashMap<String, BgpConsumer>();
	private HashMap<String, BgpProducer> producers = new HashMap<String, BgpProducer>();
	private String routerName;
	private List<Link> links = new ArrayList<Link>();
	private HashMap<String, AddressAndMask> addressAndMasks = new HashMap<String, AddressAndMask>();
	public BgpConfig(String name) {
		super(name);
		this.routerName = name;
	}

	public String getRouterName() {
		return routerName;
	}
	public void setRouterName(String routerName) {
		super.setNodeName(routerName);
		this.routerName = routerName;
	}

	public HashMap<String, AddressAndMask> getAddressAndMasks() {
		return addressAndMasks;
	}
	public boolean addAddressAndMask(AddressAndMask newAddressAndMask) {
		addressAndMasks.put(newAddressAndMask.getName(), newAddressAndMask);
		return true;
	}
	public boolean removeAddressAndMask(AddressAndMask newAddressAndMask) {
		addressAndMasks.remove(newAddressAndMask.getName());
		return true;
	}
	
	public HashMap<String, BgpConsumer> getConsumers() {
		return consumers;
	}
	public void addConsumer(BgpConsumer consumer) {
		this.consumers.put(consumer.getName(), consumer);
	}
	public void removeConsumer(String name) {
		this.consumers.remove(name);
	}


	public HashMap<String, BgpProducer> getProducers() {
		return producers;
	}
	public void addProducer(BgpProducer producer) {
		this.producers.put(producer.getName(), producer);
	}
	public void removeProducer(String name) {
		this.producers.remove(name);
	}

	public List<Link> getLinks() {
		return links;
	}
	public Link addLink(String localAddressName, InetAddress remoteAddress) {
		if (addressAndMasks.get(localAddressName) == null) {
			log.info("Link not created, Local Address " + localAddressName + " Does not exist.");
			return null;
		} else {
			for (Link link : this.links) {
				if (link.getSourceAddressName().equals(localAddressName)) {
					if (link.getDestinationAddress().equals(remoteAddress)) {
						log.info("Link not created, Local Address and Remote address pair "	+ localAddressName	+ "-"+ remoteAddress.toString()	+ " already exists.");
						return null;
					}
				}
			}
		}
		InetAddress source = getAddressAndMaskByName(localAddressName).getAddress();
		Link link = new Link(super.getNodeName()+"-"+localAddressName+"-"+remoteAddress.toString().substring(1),""+(links.size()+1)	,source	,remoteAddress);
		link.setSourceAddressName(localAddressName);
		this.links.add(link);
		log.info("Added Link" + link);
		return link;
	}
	
	public AddressAndMask getAddressAndMaskByName(String name){
		return addressAndMasks.get(name);
	}
	
	@Override
	public String toString(){
		String response = "";
		response += "routerName:"+this.routerName;
		response += ",address:"+this.addressAndMasks;
		response += ",link:"+this.links;
		return response;
	}

	public void execute() {
		ProducerConsumerStore.addNode(this);//Adding Node to ProducerConsumerStore
		//Start the threads
		for (Link link : this.getLinks()) {
			spwanProducersConsumers(link);
		}
		//Start one ssh server on each router!!
		ProducerConsumerStore.addBgpConfig(this.routerName, this);
		SshServerDaemon server = new SshServerDaemon(this);
		server.start();
	}

	public void spwanProducersConsumers(Link link){
		//Start the threads
		ProducerConsumerStore.addPath(link);//Adding Link to ProducerConsumerStore
		BgpConsumer consumer = new BgpConsumer(this, link);
		addConsumer(consumer);//Storing consumer to config, so that config becomes single point where all router related information is.
		consumer.start();
		while(!consumer.isRunning()){
			log.info("Consumer still not running, sleep some more");
			try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		BgpProducer producer = new BgpProducer(this, link);
		addProducer(producer);//Storing producer to config, so that config becomes single point where all router related information is.
		producer.start();
		consumer.setBgpProducer(producer);
	}
/**
	 * Clean closure of all producers and consumers so that the bgpConfig and all ensuing threads can be cleanly garbage collected.
	 */
	public void destroy(){
		ProducerConsumerStore.removeNode(this);
		for (Link link : this.getLinks()) {
			ProducerConsumerStore.removePath(link);
		}
		for(String consumerName : getConsumers().keySet()){
			BgpConsumer consumer = getConsumers().get(consumerName);
			consumer.setRunning(false);
		}
		for(String producerName : getProducers().keySet()){
			BgpProducer producer = getProducers().get(producerName);
			producer.timeUp();
		}
	}
}

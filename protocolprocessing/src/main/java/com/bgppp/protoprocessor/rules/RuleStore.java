package com.bgppp.protoprocessor.rules;

import org.apache.log4j.*;

import java.util.*;

import com.bgppp.protoprocessor.*;
import com.bgppp.protoprocessor.packet.BgpUpdatePacket;

@SuppressWarnings("unused")
public class RuleStore{
	public static Logger log = Logger.getLogger(RuleStore.class);

	private HashMap<String ,Rule> adjRibIn;
	private HashMap<String ,Rule> localRib;
	private HashMap<String ,Rule> adjRibOut;

	private Hashtable<String, BgpOperations> peers;
	public boolean in = true;
	public boolean out = true;
	public boolean local = true;

	private BgpConfig config = null;
	
	public RuleStore(BgpConfig config){
		this.config = config;
		this.adjRibIn = new HashMap<String, Rule>();
		this.localRib = new HashMap<String, Rule>();
		this.adjRibOut = new HashMap<String, Rule>();
		this.peers = new Hashtable<String, BgpOperations>();
	}

	public RuleStore(){
	}

	public synchronized void addRule(Rule rule){
		String uuid = UUID.randomUUID().toString();
		String p = "";
		for(String k : peers.keySet()){
			p=p+k;
		}
		log.info("\nNew Rule : " + rule.toString()+"\nPeers : "+p);
		if(rule.getPeerHandlerName().equals("0.0.0.0")){
			addLocalRib(uuid, rule);
		} else if(doesPeerExist(rule.getPeerHandlerName()) != null){
			BgpOperations ops = doesPeerExist(rule.getPeerHandlerName());
			if(ops instanceof BgpConsumerThread){
				BgpConsumerThread instance = (BgpConsumerThread) ops;
				log.info("++++++++++ADDING PATH AND HOP TO RULE ++++++++");
				rule.getPath().addPath(config.getRouterName(),"");
				rule.getNextHop().setNextHop(instance.destinationAddress);
			}else if(ops instanceof BgpProducer){
				BgpProducer instance = (BgpProducer) ops;
				log.info("++++++++++ADDING PATH AND HOP TO RULE ++++++++");
				rule.getPath().addPath(config.getRouterName(),"");
				rule.getNextHop().setNextHop(instance.destinationAddress);
			}
			shareOneRuleWithAll(rule);
			addAdjRibIn(uuid, rule);
			addLocalRib(uuid, rule);
		} /*else if(!doesPeerExist(rule.getPeerHandlerName())){
			BgpOperations peer = config.getBgpOperationsByName(rule.getPeerHandlerName()); 
			if(peer == null)log.error("Could not get a peer with name : " + rule.getPeerHandlerName());
			shareAllKnownRulesWithOne(peer);
			shareOneRuleWithAll(rule);
			addAdjRibIn(uuid, rule);
			addLocalRib(uuid, rule);
			addPeers(rule.getPeerHandlerName(), peer);
		}*/
		log.info("In : " + adjRibIn.size()+" Local : " +localRib.size()+ " OUT : " + adjRibOut.size());
	}

	/**
	 * Send the rule specified to all know peers.
	 * @param rule Rule to be shared
	 */
	public void shareOneRuleWithAll(Rule rule){
		for(String peerName : peers.keySet()){
			String s = peerName.split("==")[0];
			if(!s.equals(rule.getPeerHandlerName())){
				log.info("Sending rule " + rule.toString() + " through " + peerName);
				sendUpdate(peers.get(peerName), rule);
			}
		}
	}

	/**
	 * @param peer A new peer needs to receive all the known routes.
	 */
	public void shareAllKnownRulesWithOne(BgpOperations peer){
		for(String k : localRib.keySet()){
			log.info("Sending rule " + localRib.get(k).toString() + " through " + peer.getName());
			sendUpdate(peer, localRib.get(k));
		}
	}
	
	private void sendUpdate(BgpOperations bgpOperations, Rule rule) {
		if(bgpOperations instanceof BgpProducer){
			BgpProducer instance = (BgpProducer)bgpOperations;
			instance.toSendUpdate(new BgpUpdatePacket(rule));
		}else if(bgpOperations instanceof BgpConsumerThread){
			BgpConsumerThread instance = (BgpConsumerThread)bgpOperations;
			instance.toSendUpdate(new BgpUpdatePacket(rule));
		}
	}

	public void removeRulesFrom(String rcvFrom){
		log.info("Removing all rules from : " + rcvFrom);
		Iterator<Map.Entry<String,Rule>> itr = adjRibIn.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<String, Rule> k = itr.next();
			if(rcvFrom.equals(k.getValue().getPeerHandlerName()))
				itr.remove();
		}
		itr = localRib.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<String, Rule> k = itr.next();
			if(rcvFrom.equals(k.getValue().getPeerHandlerName()))
				itr.remove();
		}
		itr = adjRibOut.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<String, Rule> k = itr.next();
			if(rcvFrom.equals(k.getValue().getPeerHandlerName()))
				itr.remove();
		}
	}
	//Setters and Getters of the RIB's
	private synchronized void addAdjRibIn(String key, Rule rule){
		adjRibIn.put(adjRibIn.size()+new String("1"), rule);
	}
	private synchronized void removeAdjRibIn(String ruleId){
		adjRibIn.remove(ruleId);
	}
	public synchronized HashMap<String ,Rule> getAdjRibIn(){
		return adjRibIn;
	}
	
	private synchronized void addAdjRibOut(String uuid, Rule rule){
		adjRibIn.put(uuid, rule);
	}
	private synchronized void removeAdjRibOut(String uuid){
		adjRibOut.remove(uuid);
	}
	public synchronized HashMap<String ,Rule> getAdjRibOut(){
		return adjRibOut;
	}

	public void addPeers(String bgpOperationName, BgpOperations bgpOperation) {
		log.info(config.getRouterName()+"Added a new peer : " + bgpOperationName);
		this.peers.put(bgpOperationName, bgpOperation);
		shareAllKnownRulesWithOne(peers.get(bgpOperationName));
	}

	public void removePeers(String bgpOperationName) {
		log.info(config.getRouterName()+"Removed a peer : " + bgpOperationName);
		this.peers.remove(bgpOperationName);
	}

	public BgpOperations doesPeerExist(String rcvFrom){
		for(String peer : this.peers.keySet()){
			String s = peer.split("==")[0];
			if(rcvFrom.equals(s)){
				log.info("FOUND A PEER AND RETURNING IT");
				return peers.get(peer);
			}
		}
		return null;
	}

	private synchronized void addLocalRib(String uuid, Rule rule){
		localRib.put(uuid, rule);
	}
	private synchronized void removeLocalRib(String uuid){
		localRib.remove(uuid);
	}
	public synchronized HashMap<String ,Rule> getLocalRib(){
		return localRib;
	}
}

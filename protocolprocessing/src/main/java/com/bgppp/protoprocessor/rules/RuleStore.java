package com.bgppp.protoprocessor.rules;

import org.apache.log4j.*;

import java.util.*;

import com.bgppp.protoprocessor.*;
import com.bgppp.protoprocessor.timers.DelaySendingUpdate;
import com.bgppp.protoprocessor.packet.BgpUpdatePacket;

/**
 * Shittiest peice of code ever written !!!
 */
@SuppressWarnings("unused")
public class RuleStore{
	public static Logger log = Logger.getLogger(RuleStore.class);

	private HashMap<String ,Rule> adjRibIn;
	private HashMap<String ,Rule> localRib;
	private HashMap<String ,Rule> adjRibOut;

	private Hashtable<String, BgpOperations> peers;
	private Hashtable<String, DelaySendingUpdate> delayTimers;
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
		this.delayTimers = new Hashtable<String, DelaySendingUpdate>();
	}

	public RuleStore(){
	}

	public synchronized void addRule(Rule rule){
		if(RuleType.ADDRULE == rule.getType()){
			log.info("Add rule received from " + rule.getRuleSenderName() + " to network " + rule.getNetwork() + " on handler " + rule.getBgpOperationName());
			String uuid = UUID.randomUUID().toString();
			if(rule.getRuleSenderName().equals("0.0.0.0")){
				addLocalRib(uuid, rule);
			} else if(getBgpOpHandlingRuleSender(rule.getRuleSenderName()) != null){
				BgpOperations ops = getBgpOpHandlingRuleSender(rule.getRuleSenderName());
				if(ops instanceof BgpConsumerThread){
					BgpConsumerThread instance = (BgpConsumerThread) ops;
					rule.getPath().addPath(config.getRouterName(),"");
					rule.getNextHop().setNextHop(instance.destinationAddress);
				}else if(ops instanceof BgpProducer){
					BgpProducer instance = (BgpProducer) ops;
					rule.getPath().addPath(config.getRouterName(),"");
					rule.getNextHop().setNextHop(instance.destinationAddress);
				}
				shareOneRuleWithAll(rule);
				addAdjRibIn(uuid, rule);
				addLocalRib(uuid, rule);
			}
		}else{
			log.info("Withdrawing rule received from " + rule.getRuleSenderName() + " to network " + rule.getNetwork());
			withdrawRule(rule);
		}
	}

	private void withdrawRule(Rule rule) {
		Iterator<Map.Entry<String,Rule>> itr = adjRibIn.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<String, Rule> k = itr.next();
			if(rule.getNetwork().equals(k.getValue().getNetwork())){
				itr.remove();
			}
		}
		itr = localRib.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<String, Rule> k = itr.next();
			if(rule.getNetwork().equals(k.getValue().getNetwork())){
				itr.remove();
			}
		}
		itr = adjRibOut.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<String, Rule> k = itr.next();
			if(rule.getNetwork().equals(k.getValue().getNetwork())){
				itr.remove();
			}
		}
		log.info("Removing Peer from store : " + rule.getBgpOperationName());
		sendWithdrawalRule(rule.getRuleSenderName(), rule);
	}

	/**
	 * Send the rule specified to all know peers.
	 * @param rule Rule to be shared
	 */
	public void shareOneRuleWithAll(Rule rule){
		for(String peerName : peers.keySet()){
			String s = peerName.split("==")[0];
			if(!s.equals(rule.getRuleSenderName())){
				log.info("Sending rule " + rule.toString() + " through " + peerName);
				sendUpdate(peerName, peers.get(peerName), rule);
			}
		}
	}

	/**
	 * @param peer A new peer needs to receive all the known routes.
	 */
	public void shareAllKnownRulesWithOne(String operationName, BgpOperations peer){
		for(String k : localRib.keySet()){
			log.info("Sending rule " + localRib.get(k).toString() + " through " + peer.getName());
			sendUpdate(operationName, peer, localRib.get(k));
		}
	}
	
	private void sendUpdate(String bgpOperationName, BgpOperations bgpOperations, Rule rule) {
		if(this.delayTimers.get(bgpOperationName) == null){
			log.error("God Damnit Timer !!! : " + bgpOperationName);
			for(String s : this.delayTimers.keySet()){
				log.error(s);
			}
			return;
		}
		if(!this.delayTimers.get(bgpOperationName).isRunning()){
			if(bgpOperations instanceof BgpProducer){
				BgpProducer instance = (BgpProducer)bgpOperations;
				instance.toSendUpdate(new BgpUpdatePacket(rule));
			}else if(bgpOperations instanceof BgpConsumerThread){
				BgpConsumerThread instance = (BgpConsumerThread)bgpOperations;
				instance.toSendUpdate(new BgpUpdatePacket(rule));
			}
		}else{
			this.delayTimers.get(bgpOperations).addRule(rule);
		}
	}

	public void addPeers(String bgpOperationName, BgpOperations bgpOperation) {
		log.info(config.getRouterName()+"Added a new peer : " + bgpOperationName);
		this.peers.put(bgpOperationName, bgpOperation);
		DelaySendingUpdate delaySendingUpdate = new DelaySendingUpdate(bgpOperation);
		delaySendingUpdate.start();
		this.delayTimers.put(bgpOperationName, delaySendingUpdate);
		shareAllKnownRulesWithOne(bgpOperationName, peers.get(bgpOperationName));
	}

	private void sendWithdrawalRule(String ruleSenderName, Rule rule){
		log.info("Sending route withdrawal requests for " + rule.getNetwork());
		for(String peer : this.peers.keySet()){
			if(!(peer.split("==")[0]).equals(ruleSenderName)){
				BgpUpdatePacket packet = new BgpUpdatePacket("",new ArrayList<Attribute>(), rule.getNetwork());
				sendUpdate(peer, this.peers.get(peer), packet.getRule(ruleSenderName, peer));
				log.info("Withdrawal rule sent to : " + peer.split("==")[0] + ".....");
			}
		}
	}

	public BgpOperations getBgpOpHandlingRuleSender(String ruleSenderName){
		for(String peer : this.peers.keySet()){
			String s = peer.split("==")[0];
			if(ruleSenderName.equals(s)){
				return peers.get(peer);
			}
		}
		return null;
	}

	public void removeRulesFrom(String bgpOperationName){
		log.info("Removing all rules from : " + bgpOperationName);
		Iterator<Map.Entry<String,Rule>> itr = adjRibIn.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<String, Rule> k = itr.next();
			if(k.getValue().getPath().getPathSegmentAsString().contains(bgpOperationName.split("==")[0])){
				itr.remove();
			}
		}
		itr = localRib.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<String, Rule> k = itr.next();
			if(k.getValue().getPath().getPathSegmentAsString().contains(bgpOperationName.split("==")[0])){
				sendWithdrawalRule(bgpOperationName.split("==")[0], k.getValue());
				itr.remove();
			}
		}
		itr = adjRibOut.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<String, Rule> k = itr.next();
			if(k.getValue().getPath().getPathSegmentAsString().contains(bgpOperationName.split("==")[0])){
				itr.remove();
			}
		}
	}

	private Rule processRule(Rule rule){
		return null;
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

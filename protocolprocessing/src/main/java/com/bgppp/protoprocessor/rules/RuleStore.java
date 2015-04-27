package com.bgppp.protoprocessor.rules;

import org.apache.log4j.*;

import java.util.*;
import java.util.Map.Entry;

import com.bgppp.protoprocessor.*;
import com.bgppp.protoprocessor.timers.DelaySendingUpdate;
import com.bgppp.protoprocessor.packet.BgpUpdatePacket;

/**
 * Shittiest peice of code ever written !!!
 */
@SuppressWarnings("unused")
public class RuleStore{
	public static Logger log = Logger.getLogger(RuleStore.class);

	private Map<String ,Rule> adjRibIn;
	private Map<String ,Rule> localRib;
	private Map<String ,Rule> adjRibOut;

	private Hashtable<String, BgpOperations> peers;
	private Hashtable<String, DelaySendingUpdate> delayTimers;
	public boolean in = true;
	public boolean out = true;
	public boolean local = true;

	private BgpConfig config = null;

	public RuleStore(BgpConfig config){
		this.config = config;
		this.adjRibIn = Collections.synchronizedMap(new HashMap<String, Rule>());
		this.localRib = Collections.synchronizedMap(new HashMap<String, Rule>());
		this.adjRibOut = Collections.synchronizedMap(new HashMap<String, Rule>());
		this.peers = new Hashtable<String, BgpOperations>();
		this.delayTimers = new Hashtable<String, DelaySendingUpdate>();
	}

	public RuleStore(){
	}

	public synchronized void addRule(Rule rule){
		if(RuleType.ADDRULE == rule.getType()){
			log.info("Add rule received from " + rule.getRuleSenderName() + " to network " + rule.getNetwork() + " on handler " + rule.getBgpOperationName());
			log.info(getBgpOpHandlingRuleSender(rule.getRuleSenderName()) != null);
			String uuid = UUID.randomUUID().toString();
			if(rule.getRuleSenderName().equals("0.0.0.0")){
				log.info("Matches 0.0.0.0");
				addLocalRib(uuid, rule);
			} else if(rule.getPath().getPathSegmentAsString().contains(config.getRouterName())){
				log.info("Rule : " + rule + " \n CONTAINS this routers name already in its path, so we ignore it to avoid loops");
				return;
			}else if(getBgpOpHandlingRuleSender(rule.getRuleSenderName()) != null){
				BgpOperations ops = getBgpOpHandlingRuleSender(rule.getRuleSenderName());
				//If the router name is already in the path, it must be kicked
				if(rule.getPath().getPathSegmentAsString().contains(config.getRouterName())){
					log.info("Routername already in the path");
					return;
				}
				if(ops instanceof BgpConsumerThread){
					BgpConsumerThread instance = (BgpConsumerThread) ops;
					rule.getPath().addPath(config.getRouterName(),"");
					rule.getNextHop().setNextHop(instance.destinationAddress);
				}else if(ops instanceof BgpProducer){
					BgpProducer instance = (BgpProducer) ops;
					rule.getPath().addPath(config.getRouterName(),"");
					rule.getNextHop().setNextHop(instance.destinationAddress);
				}
				boolean pr1 = processRule(uuid, rule);
				boolean pr2 = false;
				log.info("processRule's response : " + pr1);
				if(!pr1){
					pr2 = processRule2(uuid, rule);
					log.info("processRule2's response : " + pr2);
				}
			}
		}else{
			log.info("Withdrawing rule received from " + rule.getRuleSenderName() + " to network " + rule.getNetwork());
			withdrawRule(rule, true);
		}
	}

	private void withdrawRule(Rule rule, boolean publish) {
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
		log.info("Removing rule from store : " + rule.getNetwork());
		if(publish)
			sendWithdrawalRule(rule.getRuleSenderName(), rule);
	}

	/**
	 * Send the rule specified to all know peers.
	 * @param rule Rule to be shared
	 */
	public void shareOneRuleWithAll(Rule rule){
		log.info("sharing rule with all : " + rule.getNetwork());
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
				log.info("Withdrawal rule sent to : " + peer.split("==")[0] + "..... as ruleSenderName was " + ruleSenderName);
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

	public void removeRulesFrom(String bgpOperationName, boolean publish){
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
				if(publish)
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

	private synchronized boolean processRule(String uuid, Rule rule){
		log.info("processRule " + rule);
		List<String> removeList = new ArrayList<String>();
		boolean replaceNow = false;
		synchronized(this){
			Iterator<Map.Entry<String, Rule>> itr = localRib.entrySet().iterator();
			//handles conditions where the network of new rule already exists in the table,
			while(itr.hasNext()){
				Entry<String, Rule> entry = itr.next();
				Rule r = entry.getValue();
				if(rule.getNetwork().equals(r.getNetwork())){
					log.info("rule and r have same network");
					if(rule.getPath().getPathSegmentAsString().split("==").length < r.getPath().getPathSegmentAsString().split("==").length){
						log.info("new rule has longer hops than r");
						removeList.add(entry.getKey());
						replaceNow = true;
						break;
					}else if(Integer.parseInt(rule.getLocalPref().getLocalPref()) > Integer.parseInt(r.getLocalPref().getLocalPref())){
						log.info("rule.locaPref is > r.localPref");
						removeList.add(entry.getKey());
						replaceNow = true;
						break;
					}else{
						log.info("no operations will be performed");
						return true;
					}
				}
			}
		}
		if(replaceNow){
			log.info("local : " + localRib.size() + " : In" + adjRibIn.size());
			for(int i=0; i<removeList.size(); i++){
				removeLocalRib(removeList.get(i));removeAdjRibIn(removeList.get(i));
			}
			addLocalRib(uuid, rule);addAdjRibIn(uuid, rule);
			log.info("local : " + localRib.size() + " : In" + adjRibIn.size());
			return true;
		}else{
			return false;
		}
	}

	private synchronized boolean processRule2(String uuid, Rule rule){
		boolean prefixEncompasing = false;
		//handles cases where the network in the rule cover larger or smaller area than existing rules.
		List<String> removeList = new ArrayList<String>();
		synchronized(this){
			Iterator<Map.Entry<String, Rule>> itr2 = localRib.entrySet().iterator();
			while(itr2.hasNext()){
				Entry<String, Rule> entry = itr2.next();
				Rule r = entry.getValue();
				if(checkPrefix(rule.getNetwork(), r.getNetwork())){
					log.info("rule covers more area than r, will replace " +r.getNetwork() + " with  " + rule.getNetwork());
					//removeLocalRib(entry.getKey());removeAdjRibIn(entry.getKey());
					//addLocalRib(uuid, rule);addAdjRibIn(uuid, rule);
					removeList.add(entry.getKey());
					sendWithdrawalRule(r.getRuleSenderName(), r);
					shareOneRuleWithAll(rule);
					prefixEncompasing = true;
				}else if(checkPrefix(r.getNetwork(), rule.getNetwork())){
					log.info("old r covers more area than rule, so we ignore new rule");
					return true;
				}
			}
		}

		//If none of the two cases above are matched, we add the rule to the tables and publish them
		if(!prefixEncompasing){
			addLocalRib(uuid, rule);
			addAdjRibIn(uuid, rule);
			shareOneRuleWithAll(rule);
		}else{
			log.info("local : " + localRib.size() + " : In" + adjRibIn.size());
			for(int i=0; i<removeList.size(); i++){
				log.info("Removing rule : " + localRib.get(removeList.get(i)).getNetwork());
				removeLocalRib(removeList.get(i));removeAdjRibIn(removeList.get(i));
			}
			addLocalRib(uuid, rule);addAdjRibIn(uuid, rule);
			log.info("local : " + localRib.size() + " : In" + adjRibIn.size());
		}
		return true;
	}

	public synchronized boolean checkPrefix(String network1, String network2){
		log.info("Comparing " + network1 +" and " + network2);
		Float p1 = Float.parseFloat(network1.split("\\/")[1]);
		Float p2 = Float.parseFloat(network2.split("\\/")[1]);
		String n1[] = network1.split("\\/")[0].split("\\.");
		String n2[] = network2.split("\\/")[0].split("\\.");
		if(p1<p2){
			for(int i=0; i<Math.floor(p1/8); i++){
				if(n1[i].equals(n2[i])){
					continue;
				}else{
					return false;
				}
			}
			return true;
		}
		return false;
	}


	//Setters and Getters of the RIB's
	private synchronized void addAdjRibIn(String key, Rule rule){
		adjRibIn.put(adjRibIn.size()+new String("1"), rule);
	}
	private synchronized void removeAdjRibIn(String ruleId){
		adjRibIn.remove(ruleId);
	}
	public synchronized Map<String ,Rule> getAdjRibIn(){
		return adjRibIn;
	}
	private synchronized void addAdjRibOut(String uuid, Rule rule){
		adjRibIn.put(uuid, rule);
	}
	private synchronized void removeAdjRibOut(String uuid){
		adjRibOut.remove(uuid);
	}
	public synchronized Map<String ,Rule> getAdjRibOut(){
		return adjRibOut;
	}
	private synchronized void addLocalRib(String uuid, Rule rule){
		localRib.put(uuid, rule);
	}
	private synchronized void removeLocalRib(String uuid){
		localRib.remove(uuid);
	}
	public synchronized Map<String ,Rule> getLocalRib(){
		return localRib;
	}
}

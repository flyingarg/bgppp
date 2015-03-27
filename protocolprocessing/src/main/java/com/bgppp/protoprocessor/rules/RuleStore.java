package com.bgppp.protoprocessor.rules;

import org.apache.log4j.*;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.Calendar;
//import com.bgppp.protoprocessor.packet.*;

public class RuleStore{
	public static Logger log = Logger.getLogger(RuleStore.class);

	public HashMap<String ,Rule> adjRibIn = new HashMap<String, Rule>();// ==> Adj-RIBs-In
	public HashMap<String ,Rule> localRib = new HashMap<String, Rule>();
	public HashMap<String ,Rule> adjRibOut = new HashMap<String, Rule>();

	public boolean in = true;
	public boolean out = true;
	public boolean local = true;


	public synchronized void addRule(Rule rule){
		Calendar cal = Calendar.getInstance();
		String uuid = UUID.randomUUID() + "" + cal.getTime().getTime();
		if(rule.getType().equals("in")){
			//send the new rule to everyone.
			for(String key : localRib.keySet()){
				if(){
				}
			}
			//send all rules to this rule.
			addAdjRibIn(uuid, rule);
		}else if(rule.getType().equals("local")){
			addLocalRib(rule);
		}
	}


	//Setters and Getters of the RIB's
	private synchronized void addAdjRibIn(String key, Rule rule){
		adjRibIn.put(adjRibIn.size()+new String("1"), rule);
	}
	private synchronized void removeAdjRibIn(String ruleId){
		adjRibIn.remove(ruleId);
	}
	private synchronized HashMap<String ,Rule> getAdjRibIn(){
		return adjRibIn;
	}
	
	private synchronized void addAdjRibOut(Rule rule){
		adjRibIn.put(adjRibOut.size()+new String("1"), rule);
	}
	private synchronized void removeAdjRibOut(String ruleId){
		adjRibOut.remove(ruleId);
	}
	private synchronized HashMap<String ,Rule> getAdjRibOut(){
		return adjRibOut;
	}

	private synchronized void addLocalRib(Rule rule){
		localRib.put(localRib.size()+new String("1"), rule);
	}
	private synchronized void removeLocalRib(String ruleId){
		localRib.remove(ruleId);
	}
	private synchronized HashMap<String ,Rule> getLocalRib(){
		return localRib;
	}
}

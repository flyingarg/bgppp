package com.bgppp.protoprocessor.rules;

import org.apache.log4j.*;
import java.util.HashMap;

public class RuleStore{
	public static Logger log = Logger.getLogger(RuleStore.class);
	public static HashMap<Long ,Rule> adjRibIn = new HashMap<Long, Rule>();// ==> Adj-RIBs-In
	public static HashMap<Long ,Rule> localRib = new HashMap<Long, Rule>();
	public static HashMap<Long ,Rule> adjRibOut = new HashMap<Long, Rule>();

	public void addAdjRibIn(Rule rule){
		adjRibIn.put(adjRibIn.size()+new Long("1"), rule);
	}
	public void removeAdjRibIn(Long ruleId){
		adjRibIn.remove(ruleId);
	}
	public HashMap<Long ,Rule> getAdjRibIn(){
		return adjRibIn;
	}

	
	//The rest of the code that we will implement below must relate to locRib
	
	
	//The rest of the code that we will implement below must relate to adjRibOut

}

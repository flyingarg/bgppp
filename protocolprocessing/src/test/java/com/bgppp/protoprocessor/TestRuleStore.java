package com.bgppp.protoprocessor;

import com.bgppp.protoprocessor.rules.RuleStore;

import junit.framework.TestCase;

public class TestRuleStore extends TestCase{

	RuleStore ruleStore = null;

	@Override
	protected void setUp() throws Exception {
		ruleStore = new RuleStore(null);
		super.setUp();
	}
	
	public void testCheckPrefix(){
		ruleStore = new RuleStore(null);
		assertTrue(ruleStore.checkPrefix("10.100/16","10.100.1/24"));
		assertFalse(ruleStore.checkPrefix("10.100.1/24","10.100/16"));
		assertTrue(ruleStore.checkPrefix("10.100/18","10.100.1/24"));
	}
}

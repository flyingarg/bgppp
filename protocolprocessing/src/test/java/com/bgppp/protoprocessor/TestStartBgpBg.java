package com.bgppp.protoprocessor;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

public class TestStartBgpBg extends TestCase{

	
	//static HashMap<String,BgpConfig> routers = new HashMap<String,BgpConfig>();
//	static StartBgpBg
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testValidConfigChanges() throws Exception{
		StartBgpBg.routers = new HashMap<String,BgpConfig>();
		BgpConfig config1 = new BgpConfig();
		config1.setRouterName("router1");
		config1.addAddress(InetAddress.getByName("10.100.1.1"));
		config1.addAddress(InetAddress.getByName("10.101.1.1"));
		StartBgpBg.routers.put(config1.getRouterName(), config1);
		
		BgpConfig config2 = new BgpConfig();
		config2.setRouterName("router2");
		config2.addAddress(InetAddress.getByName("10.102.1.1"));
		config2.addAddress(InetAddress.getByName("10.103.1.1"));
		StartBgpBg.routers.put(config2.getRouterName(), config2);
		
		List<BgpConfig> testConfigs = new ArrayList<BgpConfig>();
		testConfigs.add(config1);
		BgpConfig config3 = new BgpConfig();
		config3.setRouterName("router3");
		config3.addAddress(InetAddress.getByName("10.104.1.1"));
		config3.addAddress(InetAddress.getByName("10.105.1.1"));
		testConfigs.add(config3);
		
		
		HashMap<String, BgpConfig[]> response = StartBgpBg.MainThread.validConfigChanges(testConfigs);
		for(String configs : response.keySet()){
			System.out.println(configs + response.get(configs)[0] + response.get(configs)[1]);
		}
		assertTrue(true);
		
	}

}

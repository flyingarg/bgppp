package com.bgppp.protoprocessor;

import java.net.InetAddress;
import java.util.HashMap;

import junit.framework.TestCase;

import com.bgppp.protoprocessor.utils.ConfigHelper;

public class TestStartBgpBg extends TestCase{

	public BgpConfig config1;
	public BgpConfig config2;
	public BgpConfig testConfigs;
	public BgpConfig config3;
	
	@Override
	protected void setUp() throws Exception {
		StartBgpBg.routers = new WrappedHash<String,BgpConfig>();
		config1 = new BgpConfig();
		config1.setRouterName("router1");
		config1.addAddress(InetAddress.getByName("10.100.1.1"));
		config1.addAddress(InetAddress.getByName("10.101.1.1"));
		StartBgpBg.routers.put(config1.getRouterName(), config1);
		
		config2 = new BgpConfig();
		config2.setRouterName("router2");
		config2.addAddress(InetAddress.getByName("10.102.1.1"));
		config2.addAddress(InetAddress.getByName("10.103.1.1"));
		StartBgpBg.routers.put(config2.getRouterName(), config2);
		
		super.setUp();
	}
	
	public void testValidConfigChanges() throws Exception{
		HashMap<String, BgpConfig> testConfigs = new HashMap<String, BgpConfig>();
		testConfigs.put(config1.getRouterName(),config1);
		config3 = new BgpConfig();
		config3.setRouterName("router3");
		config3.addAddress(InetAddress.getByName("10.104.1.1"));
		config3.addAddress(InetAddress.getByName("10.105.1.1"));
		testConfigs.put(config3.getRouterName(), config3);

		HashMap<String, BgpConfig[]> response = ConfigHelper.validConfigChanges(testConfigs,StartBgpBg.routers);
		for(String key : response.keySet()){
			if(key.equals("router1")){
				assertNotNull(response.get(key)[1]);
				assertNotNull(response.get(key)[0]);
			}else if(key.equals("router2")){
				assertNull(response.get(key)[1]);
			}if(key.equals("router3")){
				assertNull(response.get(key)[0]);
			}
		}
	}
}

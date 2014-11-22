package com.bgppp.protoprocessor;

import java.lang.ProcessBuilder.Redirect;

import junit.framework.TestCase;

public class TestProcessBuilder extends TestCase {

	@Override
	protected void setUp() throws Exception {
		ProcessBuilder processBuilder = new ProcessBuilder("ifconfig", "eth0:Router1r101","10.100.1.1" ,"netmask" ,"255.255.0.0");
		processBuilder.redirectOutput(Redirect.INHERIT);
		Process process = processBuilder.start();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNothing() {
		assertTrue(true);
	}

}

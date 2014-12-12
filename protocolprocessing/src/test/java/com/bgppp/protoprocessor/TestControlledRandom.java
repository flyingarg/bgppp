package com.bgppp.protoprocessor;
import com.bgppp.protoprocessor.utils.ControlledRandom;

import junit.framework.*;

public class TestControlledRandom extends TestCase{

	public void testGetRandomInt(){
		int i = 0;
		while(i<10){
			assertTrue(ControlledRandom.getRandomInt(9)<10);
			i++;
		}
	}

	public void testNextInt(){
		int i = 0;
		while(i<10){
			ControlledRandom.nextInt(2,10);
			i++;
		}
		assertTrue(true);
	}
}

package com.bgppp.protoprocessor.utils;

import java.util.*;

import org.apache.log4j.*;

public class ControlledRandom{
	public static Logger log = Logger.getLogger(ControlledRandom.class.getName());
	public static int lastRandom = 0;
	public synchronized static int nextInt(int gap, int limit){
		int randomInt = getRandomInt(limit);
		while(!(randomInt-lastRandom < -gap || randomInt-lastRandom > gap)){
			log.debug("lastRandom : " + lastRandom + " newRandom : " + randomInt);
			randomInt = getRandomInt(limit);
		}
		lastRandom = randomInt;
		log.debug("Finally settling on lastRandom : " + lastRandom);
		return randomInt;
	}
	public synchronized static int getRandomInt(int a){
		Random rand = new Random();
		int randomInt = rand.nextInt(a);
		return randomInt;
	}
}

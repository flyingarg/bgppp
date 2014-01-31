package com.bgppp.protoprocessor;

public class BgpThread implements Runnable{

	@Override
	public void run() {
		System.out.println("Initiating and Starting Router : " + Thread.currentThread().getName());
	}

}

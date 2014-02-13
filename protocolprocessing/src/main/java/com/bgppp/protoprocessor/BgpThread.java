package com.bgppp.protoprocessor;

public class BgpThread implements Runnable{

	
	
	public BgpThread(BgpConfig config){
		
	}
	
	@Override
	public void run() {
		startListening(179);
	}

	private void startListening(Integer port){

	}
}

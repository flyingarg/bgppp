package com.bgppp.protoprocessor.packet;

public class BgpUpdatePacket {

	public BgpHeader bgpHeader = new BgpHeader();

	//TODO : List all needed parameters and generate getters and setters.
	boolean isAddition = false;
	String nodeName;
	
	//TODO : Used when preparing our packets
	public Byte[] prepareUpdateSegment(boolean isAddition){
		if(isAddition){
			//TODO : Code to add node, recalculate
		}else{
			//TODO : Code to remove node, recalculate
		}
		Byte[] packet = new Byte[0];
		Byte[] response = bgpHeader.addHeader(2, packet);
		return response;
	}

	//TODO : Once its determined that its a update packet, we need to fill that info onto the update packet
	public boolean isUpdateMessage(){
		return true;
	}
	
}

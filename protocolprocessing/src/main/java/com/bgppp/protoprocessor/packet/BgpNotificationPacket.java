package com.bgppp.protoprocessor.packet;

public class BgpNotificationPacket {

	public BgpHeader bgpHeader = new BgpHeader();

	//TODO : List all needed parameters and generate getters and setters.
	String nodeName;
	
	//TODO : Used when preparing our packets
	public Byte[] prepareNotificationSegment(boolean isAddition){
		Byte[] packet = new Byte[0];
		Byte[] response = bgpHeader.addHeader(3, packet);
		return response;
	}

	//TODO : Once its determined that its a update packet, we need to fill that info onto the update packet
	public boolean isNotificationMessage(){
		return true;
	}
	
}

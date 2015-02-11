package com.bgppp.protoprocessor.packet;

import java.util.Arrays;

public class BgpNotificationPacket {

	public BgpHeader bgpHeader = new BgpHeader();

	//TODO : List all needed parameters and generate getters and setters.
	BgpError error;
	byte[] data;
	String nodeName;
	
	public BgpNotificationPacket(final BgpError error) {
		this(error, null);
	}
	
	public BgpNotificationPacket(final BgpError error, final byte[] data) {
		super();
		this.error = error;
		this.data = data;
	}

	public BgpError getError() {
		return this.error;
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BGP Notification:");
		builder.append(this.error);
		builder.append(", data=");
		builder.append(Arrays.toString(this.data));
		return builder.toString();
	}
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

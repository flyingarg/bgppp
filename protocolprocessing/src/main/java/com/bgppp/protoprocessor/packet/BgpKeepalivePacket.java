package com.bgppp.protoprocessor.packet;

public class BgpKeepalivePacket extends BgpHeader{

	public byte[] prepareKeepAliveSegment(){
		byte[] response = addHeader(4, new byte[]{});
		return response;
	}
	
	public boolean isKeepAliveMessage(){
		return true;
	}
}

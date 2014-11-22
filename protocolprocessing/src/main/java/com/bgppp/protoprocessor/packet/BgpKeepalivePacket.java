package com.bgppp.protoprocessor.packet;

public class BgpKeepalivePacket extends BgpHeader{

	//TODO : Just the header part of a BGP packet.
	public byte[] prepareKeepAliveSegment(){
		Byte[] response = addHeader(4, new Byte[]{});
		return getbyteFromByte(response);
	}
	
	public boolean isKeepAliveMessage(){
		return true;
	}
}

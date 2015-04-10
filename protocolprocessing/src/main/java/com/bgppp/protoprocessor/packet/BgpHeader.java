package com.bgppp.protoprocessor.packet;

import com.bgppp.protoprocessor.utils.ByteOperations;

import java.nio.ByteBuffer;

public class BgpHeader extends ByteOperations{

	/**
	 * Adds 16octet header, 2octet length then 1 octet type.
	 * @param typeVal - 1 - OPEN 2 - UPDATE 3 - NOTIFICATION 4 - KEEPALIVE
	 * @param packet - The rest of the packet.
	 * @return
	 */
	public synchronized byte[] addHeader(int typeVal, byte[] packet){
		ByteBuffer b = ByteBuffer.allocate(19+packet.length);
		byte[] marker = new byte[16];
		for(int i=0;i<marker.length;i++){
			int temp = 255;
			marker[i] = (byte)temp;
		}
		b.put(marker);
		
		int lengthVal = packet.length + 19;//16 for marker, 2 for length and 1 for type.
		byte[] length = getByteArrayForInteger(lengthVal, 2);
		b.put(length);
		byte[] type	  = new byte[]{(byte)typeVal};
		b.put(type);
		b.put(packet);
		return b.array();
	}
	
	public boolean isHeaderPresent(){
		return true;
	}

}

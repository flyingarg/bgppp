package com.bgppp.protoprocessor.packet;

import com.bgppp.protoprocessor.utils.ByteOperations;

public class BgpHeader extends ByteOperations{

	/**
	 * Adds 16octet header, 2octet length then 1 octet type.
	 * @param typeVal - 1 - OPEN 2 - UPDATE 3 - NOTIFICATION 4 - KEEPALIVE
	 * @param packet - The rest of the packet.
	 * @return
	 */
	public synchronized Byte[] addHeader(int typeVal, Byte[] packet){
		Byte[] marker = new Byte[16];
		for(int i=0;i<marker.length;i++){
			int temp = 255;
			marker[i] = (byte)temp;
		}
		Byte[] type	  = new Byte[]{(byte)typeVal};
		
		int lengthVal = packet.length + 19;//16 for marker, 2 for length and 1 for type.
		Byte[] length = new Byte[]{Byte.parseByte("0",10),Byte.parseByte(""+lengthVal,10)};//Just make sure that it is not greater than 255 :-(

		return conc(conc(conc(marker,length),type),packet);
	}
	
	public boolean isHeaderPresent(){
		return true;
	}

}

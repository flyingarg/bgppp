package com.bgppp.protoprocessor.packet;

import java.nio.ByteBuffer;
public class BgpNotificationPacket extends BgpHeader{

	int data;
	String nodeName;
	int errorCodeInt ;
	int errorSubCodeInt;
		
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BGP Notification:");
		builder.append(this.errorCodeInt);
		builder.append(", data=");
		builder.append(this.data);
		return builder.toString();
	}
	/**
	 * Notice we added new arguments here. This is because this packet has 3 important segments, errorCode, errorSubcode and data.
	 * Now when someone wants to send a Notification packet. All he needs to do is invoke 
	 * <code>prepareNotificationSegment(int errorCodeInt ,int errorSubCodeInt, int dataInt)</code>
	 */
	public byte[] prepareNotificationSegment(int errorCodeInt ,int errorSubCodeInt, int dataInt){
		ByteBuffer packet = ByteBuffer.allocate(4);
		//This is 4 byte packet
		//The first Byte has the error code, it can be implemented in the following manner
		byte[] errorCode = new byte[1];
		errorCode[0] = getByteArrayForInteger(errorCodeInt, 1)[0]; // The two arguments are: first one is the error code number. The second one is the number of bytes we want to return, as this is one byte so 1. 

		//The second byte is the error subcode
		byte[] errorSubCode = new byte[1];
		errorSubCode[0] = getByteArrayForInteger(errorSubCodeInt, 1)[0]; // The two arguments are: first one is the error sub code number. The second one is the number of bytes we want to return, as this is one byte so 1. 

		//The third and 4th byte form the data field. So this might of the form
		byte[] data = new byte[2];
		data[0] = getByteArrayForInteger(dataInt, 2)[0]; // The two arguments are: first one is the data. The second one is the number of bytes we want to return, as this is two byte so its 2. 
		data[1] = getByteArrayForInteger(dataInt, 2)[1];

		//We then combine these top 4 in the same order
		packet.put(errorCode);
		packet.put(errorSubCode);
		packet.put(data);

		//We then add the mandatory BGP Header, to the above packet.
		return addHeader(3, packet.array()); 

	}

	public boolean isNotificationMessage(){
		return true;
	}
	
}

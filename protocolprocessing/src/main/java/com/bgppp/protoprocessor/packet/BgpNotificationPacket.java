package com.bgppp.protoprocessor.packet;

import java.util.Arrays;

public class BgpNotificationPacket extends BgpHeader{

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
	/**
	 * TODO : Used when preparing our packets
	 * Notice we added new arguments here. This is because this packet has 3 important segments, errorCode, errorSubcode and data.
	 * Now when someone wants to send a Notification packet. All he needs to do is invoke 
	 * <code>prepareNotificationSegment(int errorCodeInt ,int errorSubCodeInt, int dataInt)</code>
	 */
	public byte[] prepareNotificationSegment(int errorCodeInt ,int errorSubCodeInt, int dataInt){
		Byte[] packet = new Byte[0];
		//This is 4 byte packet
		//The first Byte has the error code, it can be implemented in the following manner
		Byte[] errorCode = new Byte[1];
		errorCode[0] = getByteArrayForInteger(errorCodeInt, 1)[0]; // The two arguments are: first one is the error code number. The second one is the number of bytes we want to return, as this is one byte so 1. 

		//The second byte is the error subcode
		Byte[] errorSubCode = new Byte[1];
		errorCode[0] = getByteArrayForInteger(errorSubCodeInt, 1)[0]; // The two arguments are: first one is the error sub code number. The second one is the number of bytes we want to return, as this is one byte so 1. 

		//The third and 4th byte form the data field. So this might of the form
		Byte[] data = new Byte[2];
		errorCode[0] = getByteArrayForInteger(dataInt, 1)[0]; // The two arguments are: first one is the data. The second one is the number of bytes we want to return, as this is two byte so its 2. 
		errorCode[1] = getByteArrayForInteger(dataInt, 1)[1];

		//We then combine these top 4 in the same order
		packet = conc(conc(errorCode, errorSubCode),data);

		//We then add the mandatory BGP Header, to the above packet.
		Byte[] temp = addHeader(1, packet); 

		//We then convert the Byte[] to byte[] and return it
		return getbyteFromByte(temp);
	}

	//TODO : Once its determined that its a update packet, we need to fill that info onto the update packet
	public boolean isNotificationMessage(){
		return true;
	}
	
}

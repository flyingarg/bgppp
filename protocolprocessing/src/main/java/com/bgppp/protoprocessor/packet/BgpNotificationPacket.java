package com.bgppp.protoprocessor.packet;

public class BgpNotificationPacket extends BgpHeader{

	//TODO : List all needed parameters and generate getters and setters.
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
		errorSubCode[0] = getByteArrayForInteger(errorSubCodeInt, 1)[0]; // The two arguments are: first one is the error sub code number. The second one is the number of bytes we want to return, as this is one byte so 1. 

		//The third and 4th byte form the data field. So this might of the form
		Byte[] data = new Byte[2];
		data[0] = getByteArrayForInteger(dataInt, 2)[0]; // The two arguments are: first one is the data. The second one is the number of bytes we want to return, as this is two byte so its 2. 
		data[1] = getByteArrayForInteger(dataInt, 2)[1];

		//We then combine these top 4 in the same order
		packet = conc(conc(errorCode, errorSubCode),data);

		//We then add the mandatory BGP Header, to the above packet.
		Byte[] temp = addHeader(3, packet); 

		//We then convert the Byte[] to byte[] and return it
		return getbyteFromByte(temp);
	}

	//TODO : Once its determined that its a update packet, we need to fill that info onto the update packet
	public boolean isNotificationMessage(){
		return true;
	}
	
}

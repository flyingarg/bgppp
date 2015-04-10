package com.bgppp.protoprocessor.packet;

import java.nio.ByteBuffer;

public class BgpOpenPacket extends BgpHeader{

	int version = 4;
	int asNumber;
	int holdTime;
	int[] bgpIdentifier = new int[4];

	public BgpOpenPacket(){
	}

	public BgpOpenPacket(byte[] bytes){
		this.asNumber = getIntegerFromBytes(new byte[]{bytes[20], bytes[21]});
		for(int i=0; i<bgpIdentifier.length; i++){
			this.bgpIdentifier[i] = getIntegerFromBytes(new byte[]{bytes[i+24]});
		}
	}

	public byte[] prepareOpenSegment() throws Exception{
        ByteBuffer packet = ByteBuffer.allocate(10);

        byte[] version = getByteArrayForInteger(4,1);
        byte[] autonomousSystem = getByteArrayForInteger(asNumber, 2);//2
        byte[] hTime = getByteArrayForInteger(holdTime, 2);//2
        byte[] identifier = new byte[4];
		identifier[0] = getByteArrayForInteger(bgpIdentifier[0],1)[0];//4
        identifier[1] = getByteArrayForInteger(bgpIdentifier[1],1)[0];//4
        identifier[2] = getByteArrayForInteger(bgpIdentifier[2],1)[0];//4
        identifier[3] = getByteArrayForInteger(bgpIdentifier[3],1)[0];//4
        byte[] optionalParameterLength = new byte[]{(byte)0};

		packet.put(version);
		packet.put(autonomousSystem);
		packet.put(hTime);
		packet.put(identifier);
		packet.put(optionalParameterLength);
		
		byte[] temp = addHeader(1, packet.array());
		return temp;
	}
	public boolean isOpenMessage(){
		return true;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @return the asNumber
	 */
	public int getAsNumber() {
		return asNumber;
	}

	/**
	 * @param asNumber the asNumber to set
	 */
	public void setAsNumber(int asNumber) {
		this.asNumber = asNumber;
	}

	/**
	 * @return the holdTime
	 */
	public int getHoldTime() {
		return holdTime;
	}

	/**
	 * @param holdTime the holdTime to set
	 */
	public void setHoldTime(int holdTime) {
		this.holdTime = holdTime;
	}

	/**
	 * @return the bgpIdentifier
	 */
	public String getBgpIdentifier() {
		String r = "";
		for(int i=0; i<this.bgpIdentifier.length; i++){
			r = "."+this.bgpIdentifier[i];
		}
		return r.replaceFirst("\\.","");
	}

	/**
	 * @param bgpIdentifier the bgpIdentifier to set
	 */
	public void setBgpIdentifier(String bgpIdentifier) {
		String[] str = bgpIdentifier.split("\\.");
		for(int i=0; i<str.length; i++){
			Long l = Long.parseLong(str[i]);
			this.bgpIdentifier[i] = l.intValue();
		}
	}
	
}

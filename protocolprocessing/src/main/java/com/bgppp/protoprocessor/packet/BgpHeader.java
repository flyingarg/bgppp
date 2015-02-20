package com.bgppp.protoprocessor.packet;

import java.util.*;

public class BgpHeader {

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
	
	public synchronized Byte[] conc(Byte[] a, Byte[] b) {
	    Byte[] result = new Byte[a.length + b.length]; 
	    System.arraycopy(a, 0, result, 0, a.length); 
		System.arraycopy(b, 0, result, a.length, b.length); 
		return result;
	} 

	public synchronized byte[] getbyteFromByte(Byte[] inputBytes){
		byte[] outputBytes = new byte[inputBytes.length];
		for(int i=0; i<inputBytes.length; i++){
			outputBytes[i] = inputBytes[i];
		}
		return outputBytes;
	}

	public boolean isHeaderPresent(){
		return true;
	}

	public boolean isBitSet(Byte b, int position){
		if(((b >> position) & 1) == 1)
			return true;
		else
			return false;
	}

	public byte[] getByteArrayForInteger(int nu, int arraySize){
		byte[] response = new byte[arraySize];
		Queue<Integer> st = new LinkedList<Integer>();
		if(nu>Math.pow(2,((8*arraySize)+1))){
			return null;
		}
		int tempNu = nu; 
		while(tempNu>1){
			int a = tempNu%2;
			st.add(Integer.parseInt(""+a));
			tempNu = tempNu/2;
			if(tempNu == 1){ 
				st.add(Integer.parseInt(""+1));
			}
		}
		int limit = arraySize - 1;
		String r = ""; 
		for(int i=limit; i>-1; i--){
			for(int j=0;j<8;j++){
				if(!st.isEmpty() && st.poll() == 1){ 
					r = "1"+r;
					response[i] = (byte)(response[i] | 1<<j);//Sets to 1
				}else{
					r = "0"+r;
					response[i] = (byte)(response[i] & (~(1<<j)));//Sets to 0
				}
			}
		}
		return response;
	}   
	public int getIntegerFromBytes(byte[] bytes){
		int response = 0;
		int limit = bytes.length-1;
		for(int i=limit; i>-1; i--){//Going from right to left.
			for(int j=0; j<8; j++){
				if(isBitSet(bytes[i], j)){//Going from righ to left.
					response = response + (int)Math.pow(2, (j + 8*(limit-i)));
				}
			}
		}
		return response;
	}

}

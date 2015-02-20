package com.bgppp.protoprocessor.rules;

import java.util.*;

public abstract class Attribute{
	public abstract String getType();
	public abstract Byte[] getAsBytes();

	boolean isOptional;
	boolean isTransitive;
	boolean isPartial;
	boolean isExtended;

	public Attribute(){
	}
	public Attribute(boolean isOptional, boolean isTransitive, boolean isPartial, boolean isExtended){
		this.isOptional = isOptional;
		this.isTransitive = isTransitive;
		this.isPartial = isPartial;
		this.isExtended = isExtended;
	}

	/**
	 * Returns a 1-octet (Byte) that has 1st 4 MSB bits representing optional, transitive, partial and extended. The rest of the 4 LSB set to zero.
	 */
	public Byte[] getFlagsAsBytes(){
		int flags[] = new int[]{isOptional?1:0, isTransitive?1:0, isPartial?1:0, isExtended?1:0, 0, 0, 0, 0};
		byte b = (byte)0;
		for(int i=0; i<flags.length; i++){
			if(flags[i] == 1)
				b = (byte)(b | 1<<(7-i));
			else
				b = (byte)(b & (~(1<<(7-i))));
		}
		return new Byte[]{b};
	}

	/**
	 * Concatonates two <code>Byte[]</code> to return a single <code>Byte[]</code>.
	 */
	public synchronized Byte[] conc(Byte[] a, Byte[] b) {
		Byte[] result = new Byte[a.length + b.length]; 
		System.arraycopy(a, 0, result, 0, a.length); 
		System.arraycopy(b, 0, result, a.length, b.length); 
		return result;
	}   

	/**
	 * Converts Byte[] to byte[]. Might be this whole business of using Byte[] is conuter-effetient, but i have written too much code already to change it. 
	 * But definately a todo.
	 */
	public synchronized byte[] getbyteFromByte(Byte[] inputBytes){
		byte[] outputBytes = new byte[inputBytes.length];
		for(int i=0; i<inputBytes.length; i++){
			outputBytes[i] = inputBytes[i];
		}
		return outputBytes;
	}   

	/**
	 * @param b is the byte which is being cheked for its individual bits.
	 * @param position the position of the bit under investigation. Positions go from right(0) to left(7).
	 * Checks if a bit is set in a <code>byte</code> at position <code>position</code>
	 */
	public boolean isBitSet(Byte b, int position){
		if(((b >> position) & 1) == 1)
			return true;
		else
			return false;
	}

	/**
	 * Java has signed byte. Bgp uses unsigned bytes. This accumulated with the fact that we might need to represt certain things(for example AS Id) in a feild of 2-octets(bytes)
	 * forces us to have the following method. One can enter a number as large a 65535 and have a 2-octet(byte) array returned using this method. Example:
	 * <code>getByteArrayForInteger(65535,2);</code>
	 *
	 * @param nu The number that will be converted into a byte
	 * @param arraySize The size of the return array
	 */
	public byte[] getByteArrayForInteger(int nu, int arraySize){
		byte[] response = new byte[arraySize];
		Queue<Integer> st = new LinkedList<Integer>();
		if(nu>Math.pow(2,((8*arraySize)+1))){
			return null;
		}
		//Factorizing and storing bits into a queue
		int tempNu = nu;
		if(nu == 1)
			st.add(Integer.parseInt(""+nu));
		while(tempNu>1){
			int a = tempNu%2;
			st.add(Integer.parseInt(""+a));
			tempNu = tempNu/2;
			if(tempNu == 1){
				st.add(Integer.parseInt(""+1));
			}
		}
		//Putting thes values in the queue to response array
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

	/**
	 * Plain typecasting of a byte(which is signed in java) would only give us numbers from -128 to 127. As we are encoding byte/Byte as unsigned. We need to consider the same 
	 * factors while trying to retrive numbers from bytes.
	 * @param bytes Enter a byte array and retrive the corresponding integer. Note: This returns byte Integer value for the whole array. 
	 * If <code>bytes = {(byte)1,(byte)1}</code>. Then <code>getIntegerFromBytes(bytes)</code> would return 257 as that is the value of (0000 0001, 0000 0001).
	 * If <code>bytes = {(byte)1}</code>. Then <code>getIntegerFromBytes(bytes)</code> would return 1 as that is the value of (0000 0001).
	 */
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

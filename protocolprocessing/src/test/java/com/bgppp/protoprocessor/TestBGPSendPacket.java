package com.bgppp.protoprocessor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

public class TestBGPSendPacket extends TestCase {

	
	public void testSomethig(){
		Byte[] marker = new Byte[16];
		for(int i=0;i<marker.length;i++){
			int temp = 255;
			marker[i] = (byte)temp;
		}
		int int_type = 1;
		Byte[] type	  = new Byte[]{(byte)1};		
		Byte[] length = new Byte[]{Byte.parseByte("0",10),Byte.parseByte("29",10)};

		Byte[] version = new Byte[]{Byte.parseByte("4",10)};
		Byte[] autonomousSystem = new Byte[]{Byte.parseByte("44",10),Byte.parseByte("66",10)};
		Byte[] holdTime = new Byte[]{Byte.parseByte("0",10),Byte.parseByte("5",10)};
		Byte[] bgpIdentifier = new Byte[]{Byte.parseByte("78",10),Byte.parseByte("56",10),Byte.parseByte("34",10),Byte.parseByte("12",10),};
		Byte[] optionalParameterLength = new Byte[]{Byte.parseByte("0",10)};

		Byte[] response = conc(conc(conc(conc(conc(conc(conc(marker,length),type),version),autonomousSystem),holdTime),bgpIdentifier),optionalParameterLength);
		
		InetSocketAddress inetSocketAddress;
		try {
			inetSocketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 179);
			Socket socket = new Socket();
			socket.connect(inetSocketAddress);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			byte[] resp = new byte[29];
			for(int i=0;i<response.length;i++)
				resp[i] = response[i];
			dos.write(resp, 0, 29);
			dos.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	Byte[] conc(Byte[] a, Byte[] b) {
	    Byte[] result = new Byte[a.length + b.length]; 
	    System.arraycopy(a, 0, result, 0, a.length); 
	    System.arraycopy(b, 0, result, a.length, b.length); 
	    return result;
	} 
	
	public void testTwoOctets(){
		ByteBuffer b = ByteBuffer.allocate(16);
		b.putInt(1);
		byte[] result = b.array();
		System.out.println(result.length);
	}
}

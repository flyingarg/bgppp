package com.bgppp.protoprocessor;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class ClientForRobustServer {

	public static void main(String args[]) throws Exception{
		Socket socket = null;
		HashMap<Integer, DataOutputStream> stored = new HashMap<Integer, DataOutputStream>(); 
		//Was for just creating connections and nothing else.
		/*for(int i=0;i<6;i++){
			InetSocketAddress inetSocketAddress;
			inetSocketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 8000);
			socket = new Socket();
			socket.setSoTimeout(5000);
			socket.connect(inetSocketAddress);
		}*/
		
		for(int i=0;i<4;i++){
			InetSocketAddress inetSocketAddress;
			inetSocketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 8000);
			socket = new Socket();
			socket.setSoTimeout(5000);
			socket.connect(inetSocketAddress);
			System.out.println("i="+i+" on port " + socket.getLocalPort());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			stored.put(i, dos);
			
		}
		
		System.out.println("No of clients created : " + stored.size());
		Random rand = new Random();
		Random rand2 = new Random();
		byte[] bytes = new byte[]{'H','E','L','L','O','W','D','F','G','X','B','Y','\n'};
		int k=0;
		while(k<20){
			int i = rand.nextInt(3);
			int j = rand2.nextInt(bytes.length);
			DataOutputStream dos = stored.get(i);
			byte b = bytes[j];
			System.out.println((char)b + " to " + i);
			dos.writeByte(b);
			/*if(b == '\n')
				dos.close();*/
			k++;
		}
		for(int i=0;i<4;i++){
			stored.get(i).write('\n');
			stored.get(i).close();
		}
	}
	
}

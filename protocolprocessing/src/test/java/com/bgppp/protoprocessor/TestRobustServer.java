package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.junit.Test;
import org.junit.Ignore;

public class TestRobustServer {

	@Test
	@Ignore
	public void testServerConnectionLimits() throws Exception{
		MyServer server = new MyServer();
		server.setMaxConnections(4);
		server.setName("MyServer");
		server.setPort(179);
		server.setAddress(InetAddress.getByName("127.0.0.1"));
		server.startServer();
		System.out.println("exit");
	}
	
	class MyServer{
		String name;
		int port;
		InetAddress address;
		int maxConnections;
		HashMap<String, Socket> acceptedConnections = new HashMap<String, Socket>();
		public void setName(String name) {
			this.name = name;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public void setAddress(InetAddress address) {
			this.address = address;
		}
		public void setMaxConnections(int maxConnections) {
			this.maxConnections = maxConnections;
		}
		public void startServer() throws Exception{
			InetSocketAddress inetSocketAddress = new InetSocketAddress(address, port);
			ServerSocket serverSocket = new ServerSocket();
			serverSocket.bind(inetSocketAddress);
			System.out.println("Let the connetions come *rolling up sleves*");
			while(true /*&& acceptedConnections.size()<4*/){
				System.out.println("Waiting for connection");
				Socket listen = serverSocket.accept();
				System.out.println("Accepted Connection from port : " + listen.getPort());
				acceptedConnections.put(""+listen.getPort(), listen);
				StreamListener streamListener = new StreamListener(""+listen.getPort(), listen);
				streamListener.run(); // what the fuck is the diff between run and start ?
				//System.out.println("exit");
				serverSocket.close();
			}
			//As soon as the connections are created this thing exits i guess, so only half the requests are handled.
		}
		public void areRunning(){
			
		}
		public void stopServer(){
			
		}
	}
	
	class StreamListener extends Thread{
		Socket listen;
		String listenerName;
		public StreamListener(String listenerName, Socket listen){
			this.listen = listen;
			this.listenerName = listenerName;
		}
		public void setListen(Socket listen) {
			this.listen = listen;
		}

		@Override
		public void run() {
			try{
				DataInputStream dis = new DataInputStream(listen.getInputStream());
				byte temp = 0;
				while((temp = dis.readByte()) != '\n'){
					System.out.print(listen.getPort());System.out.println((char)temp);
				}
				/*while(true){
					System.out.println(" - " + listen.getPort());
					temp = dis.readByte();//In the quest to forever read data !!
					System.out.println((char)temp);
					if(temp == '\n'){
						System.out.println(listenerName+":"+line);
						line = "";
					}else{
						line = line + new String(new byte[]{temp});
					}
				}*/
			}catch(Exception exception){
				System.out.println("Some error with the listener for : " + listenerName + " " + exception.getMessage());
				exception.printStackTrace();
			}
			System.out.println("exit");
			super.run();
		}
	}
}

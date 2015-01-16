package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class MyServerSocket {
	public static void main(String args[]) throws InterruptedException {
		Server server = new Server();
		server.start();
		SocketClientInitiator sci = new SocketClientInitiator();
		DataOutputStream dis = sci.getDataOutputStream(); 
		Client client = new Client("client1",dis);
		Client client2 = new Client("client2",dis);
		Client client3 = new Client("client3",dis);
		client.run();
		client2.run();
		client3.run();
	}
}

/**
 * An Ignored Test cases so does not matter, but can be run as -> Test Case.  
 * Notice the warnings in the test. We are not closing these on purpose
 * @author rajumoh
 *
 */
class Server extends Thread {

	@Override
	public void run() {
		try {
			DataInputStream ois = null;
			InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 179);
			ServerSocket serverSocket = new ServerSocket();
			serverSocket.bind(inetSocketAddress);
			//serverSocket.setSoTimeout(4000);
			Socket listen = serverSocket.accept();
			ois = new DataInputStream(listen.getInputStream());
			int turn = 1;
			while (true && turn<10) {
				System.out.println("Sleeping : " + (turn*1000));
				Thread.sleep(turn*1000);
				//byte[] bytes = new byte[10];
				String response = "";
				byte temp = 0;
				/*while((temp = ois.readByte()) != '\n'){
					response = response + new String(new byte[]{temp});
				}*/
				//while(true){
					byte[] resp = new byte[29];
					ois.readFully(resp);
					for(int k=0;k<29;k++){
					System.out.println(resp[k]);
					}
				//}
				//System.out.println("reading : " + response);
				//turn+=2;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}

class Client implements Runnable {

	@SuppressWarnings("unused")
	private boolean running = true;
	private int trys = 3;
	private String name;
	private DataOutputStream dis;
	public void terminate() {
		running = false;
	}
	public Client(String name, DataOutputStream dis){
		this.name = name;
		this.dis = dis;
	}

	@Override
	public void run() {
		try {
			fuck();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch(SocketTimeoutException exception){
			System.out.println("No more waiting for message");
		} catch(ConnectException exception){
			trys--;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Running nth time "+trys);
			if(trys>=0)
				run();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println("Exiting client");
	}
	
	private void fuck() throws UnknownHostException, SocketTimeoutException, ConnectException, IOException{
		//while (running) {
			//this.sleep(2000);
			dis.writeBytes("Hello");
			dis.writeByte('\n');
			System.out.println("Sending["+this.name+"] : " + 10);
		/*}
		if(running == false){
			dis.close();
		}*/
	}
}

class SocketClientInitiator{
	private DataOutputStream dis = null;
	private Socket socket = null;
	public SocketClientInitiator(){
		InetSocketAddress inetSocketAddress;
		try {
			inetSocketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 179);
			socket = new Socket();
			socket.setSoTimeout(5000);
			socket.connect(inetSocketAddress);
			this.dis = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public DataOutputStream getDataOutputStream(){
		return this.dis;
	}
	public void closeSocket(){
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
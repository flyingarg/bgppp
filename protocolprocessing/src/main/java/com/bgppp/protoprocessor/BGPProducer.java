package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.bgppp.protoprocessor.graphs.TimeOutUtils;

public class BGPProducer extends Thread{
	public static Logger log = Logger.getLogger(BGPProducer.class.getName());
	private BgpConfig bgpConfig = null;
	private Link link = null;
	private DataInputStream is = null;
	private PrintStream os = null;
	private int PORT = 179;
	private boolean isRunning = true;
	
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	public BGPProducer(BgpConfig bgpConfig, Link link){
		this.bgpConfig = bgpConfig;
		this.link = link;
		this.setName(bgpConfig.getRouterName()+"_producer"+"_"+link.getDestinationRouterName());
	}

	@Override
	public void run(){
		log.info("Starting Producer : " + bgpConfig.getRouterName() + ":" +link);
		InetSocketAddress inetSocketAddress = new InetSocketAddress(link.getDestinationAddress(), PORT);
		Socket socket = new Socket();
		DataOutputStream outStream = null;
		DataInputStream inStream = null;
		boolean isConnected = false;
		boolean bgpOpenCommandProcessed = false;
		while(isRunning()){
			log.info(isConnected+"/"+bgpOpenCommandProcessed+"/"+link.isAlive());
			try {
				if(!isConnected){
					//socket.setSoTimeout(TimeOutUtils.READ_SOMTIMEOUT);
					socket.connect(inetSocketAddress);
					outStream = new DataOutputStream(socket.getOutputStream());
					inStream = new DataInputStream(socket.getInputStream());
					isConnected = true;
				}
			}catch (UnknownHostException e) {
				//log.severe(e.getMessage());
				e.printStackTrace();
			} catch(SocketTimeoutException exception){
				log.info("No response from " + link.getDestinationRouterName());
				link.setAlive(false);
			} catch(ConnectException exception){
				try {
					log.info("Could not connect to " + link.getDestinationRouterName());
					isConnected = false;
					link.setAlive(false);
					Thread.sleep(TimeOutUtils.RECONNECT_TIME);
				} catch (InterruptedException e) {
					//log.severe(e.getMessage());
					e.printStackTrace();
				}
			} catch (IOException e) {
				//log.severe(e.getMessage());
				e.printStackTrace();
			}
			if(isConnected && !bgpOpenCommandProcessed){
				bgpOpenCommandProcessed = toSendOPEN(inStream, outStream, link.getDestinationAddress());
			}
			if(isConnected && bgpOpenCommandProcessed){
				String response = readResponse(inStream);
				if(response.startsWith("keepalive")){
					link.setAlive(true);
				}else{
					log.info("UNRECOGNIZABLE RESPONSE : " + response);
				}
			}
		}
		if(!isRunning()){
			try {
				if(socket!=null)socket.close();
				if(inStream!=null)inStream.close();
				if(outStream!=null)outStream.close();
			} catch (IOException e) {
				log.severe("Error closing connections" + e.getMessage());
			}
		}
	}
	/**
	 * Sends a output stream and then waits for a input stream, this input stream is usually the ACK stream that the OPEN Connection was accepted.
	 * @param inStream
	 * @param outStream
	 * @return
	 */
	private boolean toSendOPEN(DataInputStream inStream, DataOutputStream outStream, InetAddress address){
		try {
			this.sleep(3000);
			log.info("Writing to address:"+address);
			outStream.writeBytes("open:someautonomoussys:5:"+bgpConfig.getRouterName()+":param1:param2:param2\n");
			String response = "";
			byte temp = 0;
			while((temp = inStream.readByte()) != '\n'){
				response = response + new String(new byte[]{temp});
			}
			log.info("Received message " + response);
			if(response.contains("keepalive")){
				link.setAlive(true);
				return true;
			}
		} /*catch(SocketTimeoutException exception){
			log.info("Waited for keepalive response till "+TimeOutUtils.READ_SOMTIMEOUT+" milli-seconds, nothing happened.");
			return false;
		}*/ catch (IOException e) {
			//log.severe(e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
/*	private void toSendKEEPALIVE(DataInputStream inStream, DataOutputStream outStream){
		try {
			
			outStream.writeBytes("KEEPALIVE");
		} catch(SocketTimeoutException exception){
			System.out.println("Waited for ACK response till 3 seconds, nothing happened.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	private void toSendUPDATE(){
		
	}
	private void toSendNOTIFICATION(){
		
	}
	
	private String readResponse(DataInputStream inStream){
		String response = "";
		try {
			response = inStream.readLine();
			return response;
		} catch(SocketTimeoutException exception){
			log.info("Waited for SOME response till "+TimeOutUtils.READ_SOMTIMEOUT+" seconds, nothing happened.");
			link.setAlive(false);
			setRunning(false);
		} catch (IOException e) {
			log.info(e.getMessage());
		}
		return response;
	}
	
}

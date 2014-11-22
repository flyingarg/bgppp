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
import org.apache.log4j.*;
import com.bgppp.protoprocessor.graphs.TimeOutUtils;

import com.bgppp.protoprocessor.utils.*;
import com.bgppp.protoprocessor.packet.*;

public class BgpProducer extends Thread{
	public static Logger log = Logger.getLogger(BgpProducer.class.getName());
	private BgpConfig bgpConfig = null;
	private Link link = null;
	private DataInputStream is = null;
	private PrintStream os = null;
	private int PORT = GetProperties.bgpPort;
	private boolean isRunning = true;
	//private String producerName = "";
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	public BgpProducer(BgpConfig bgpConfig, Link link){
		this.bgpConfig = bgpConfig;
		this.link = link;
		this.setName(bgpConfig.getRouterName()+"_producer_"+link.getSourceAddressName());
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
					socket.setSoTimeout(TimeOutUtils.READ_SOMTIMEOUT);
					log.info("Starting connection");
					socket.connect(inetSocketAddress);
					outStream = new DataOutputStream(socket.getOutputStream());
					inStream = new DataInputStream(socket.getInputStream());
					isConnected = true;
					log.info("Connected");
				}
			}catch (UnknownHostException e) {
				log.error("Unknown Host "+e.getMessage());
				e.printStackTrace();
			} catch(SocketTimeoutException exception){
				log.error("No response from " + link.getDestinationRouterName());
				link.setAlive(false);
			} catch(ConnectException exception){
				try {
					log.error("Could not connect to " + link.getDestinationRouterName());
					isConnected = false;
					link.setAlive(false);
					Thread.sleep(TimeOutUtils.RECONNECT_TIME);
				} catch (InterruptedException e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}
			} catch (IOException e) {
				log.error(e.getMessage());
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
				log.error("Error closing connections" + e.getMessage());
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
			Thread.sleep(3000);
			log.info("Writing to address:"+address);
			BgpOpenPacket bgpOpenPacket = new BgpOpenPacket();
			bgpOpenPacket.setVersion(new Byte[]{Byte.parseByte("4",10)});
			bgpOpenPacket.setAutonomousSystem(new Byte[]{Byte.parseByte("44",10),Byte.parseByte("66",10)});
			bgpOpenPacket.setHoldTime(new Byte[]{Byte.parseByte("0",10),Byte.parseByte("5",10)});
			bgpOpenPacket.setBgpIdentifier(new Byte[]{Byte.parseByte("-2",10),Byte.parseByte("56",10),Byte.parseByte("34",10),Byte.parseByte("12",10),});
			byte[] openPacket = bgpOpenPacket.prepareOpenSegment();
			outStream.write(openPacket, 0, openPacket.length);
		} catch(SocketTimeoutException exception){
			log.info("Waited for keepalive response till "+TimeOutUtils.READ_SOMTIMEOUT+" milli-seconds, nothing happened.");
			return false;
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		} catch(Exception e){
			log.error(e.getMessage());
		}
		return true;
	}
	private void toSendKEEPALIVE(DataInputStream inStream, DataOutputStream outStream){
		try {
			BgpKeepalivePacket bgpKAPacket = new BgpKeepalivePacket();
			byte[] kaPacket = bgpKAPacket.prepareKeepAliveSegment();
			outStream.write(kaPacket, 0, kaPacket.length);
		} catch(SocketTimeoutException exception){
			log.info("Waited for ACK response till 3 seconds, nothing happened.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
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

	public BgpConfig getBgpConfig(){
		return this.bgpConfig;
	}   
	public boolean isProducerAlive(){
		return this.link.isAlive();
	}   
	/*public void setProducerName(String name){
		this.producerName = name;
	}   
	public String getProducerName(){
		return this.producerName;
	} */  

}

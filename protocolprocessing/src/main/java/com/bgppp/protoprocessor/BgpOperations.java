package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;

import com.bgppp.protoprocessor.graphs.TimeOutUtils;
import com.bgppp.protoprocessor.packet.BgpKeepalivePacket;
import com.bgppp.protoprocessor.packet.BgpOpenPacket;

public class BgpOperations extends Thread{
	//private static Logger log = Logger.getLogger(BgpOperations.class.getName());

	
	/**
	 *Sends a output stream and then waits for a input stream, this input stream is usually the ACK stream that the OPEN Connection was accepted.
	 *@param inStream
	 *@param outStream
	 *@return 
	 */
	protected boolean toSendOPEN(DataInputStream inStream, DataOutputStream outStream, Logger log, String bgpIdentifier){
		try {
			log.info("toSendOPEN invoked");
			Thread.sleep(3000);
			BgpOpenPacket bgpOpenPacket = new BgpOpenPacket();
			bgpOpenPacket.setVersion(new Byte[]{Byte.parseByte("4",10)});
			bgpOpenPacket.setAutonomousSystem(new Byte[]{Byte.parseByte("44",10),Byte.parseByte("66",10)});
			bgpOpenPacket.setHoldTime(new Byte[]{Byte.parseByte("0",10),Byte.parseByte("5",10)});
			String[] i = bgpIdentifier.substring(1).trim().split("\\.");
			log.info(bgpIdentifier);
			bgpOpenPacket.setBgpIdentifier(new Byte[]{Byte.parseByte(i[0],10),Byte.parseByte(i[1],10),Byte.parseByte(i[2],10),Byte.parseByte(i[3],10),});
			byte[] openPacket = bgpOpenPacket.prepareOpenSegment();
			outStream.write(openPacket, 0, openPacket.length);
		} catch(SocketTimeoutException exception){
			log.info("Waited for keepalive response till "+TimeOutUtils.READ_SOMTIMEOUT+" milli-seconds, nothing happened.");
			return false;
		} catch (IOException e) {
			log.error("IOException: "+e.getMessage());
			return false;
		} catch (InterruptedException e) {
			log.error("InterruptedException: "+e.getMessage());
			return false;
		} catch(Exception e){ 
			log.error("Exception:" + e.getMessage());
			return false;
		}
		return true;
	}

	protected void toSendKEEPALIVE(DataInputStream inStream, DataOutputStream outStream, Logger log){
		try {
			BgpKeepalivePacket bgpKAPacket = new BgpKeepalivePacket();
			byte[] kaPacket = bgpKAPacket.prepareKeepAliveSegment();
			outStream.write(kaPacket, 0, kaPacket.length);
		} catch(SocketTimeoutException exception){
			log.error("Waited for ACK response till 3 seconds, nothing happened.");
		} catch (IOException e) {
			log.error("IOException " + e.getMessage());
		}
	}   
	public int getInt(byte[] bite){
		String strInt = "";
		for(int j=bite.length-1;j>-1;j--){
			if (bite[j]<0)
				strInt = (127 - bite[j]) + strInt;
			else
				strInt = bite[j] + strInt;
		}
		Integer r = new Integer(strInt);
		return r.intValue();
	}

}

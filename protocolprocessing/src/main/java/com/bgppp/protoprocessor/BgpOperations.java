package com.bgppp.protoprocessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;

import com.bgppp.protoprocessor.utils.TimeOutUtils;
import com.bgppp.protoprocessor.packet.BgpKeepalivePacket;
import com.bgppp.protoprocessor.packet.BgpOpenPacket;

public class BgpOperations extends Thread{
	
	/**
	 *Sends a output stream and then waits for a input stream, this input stream is usually the ACK stream that the OPEN Connection was accepted.
	 *@param inStream
	 *@param outStream
	 *@return 
	 */
	protected boolean toSendOPEN(DataInputStream inStream, DataOutputStream outStream, Logger log, String bgpIdentifier, String asNumber){
		try {
			Thread.sleep(3000);
			BgpOpenPacket bgpOpenPacket = new BgpOpenPacket();
			Long l = Long.parseLong(asNumber);
			bgpOpenPacket.setAsNumber(l.intValue());
			bgpOpenPacket.setHoldTime(5);
			bgpOpenPacket.setBgpIdentifier(bgpIdentifier);
			byte[] openPacket = bgpOpenPacket.prepareOpenSegment();
			outStream.write(openPacket, 0, openPacket.length);
		} catch(SocketTimeoutException exception){
			log.info("Waited for keepalive response till "+TimeOutUtils.READ_SO_TIMEOUT+" milli-seconds, nothing happened.");
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

	protected boolean toSendKEEPALIVE(DataInputStream inStream, DataOutputStream outStream, Logger log){
		try {
			BgpKeepalivePacket bgpKAPacket = new BgpKeepalivePacket();
			byte[] kaPacket = bgpKAPacket.prepareKeepAliveSegment();
			outStream.write(kaPacket, 0, kaPacket.length);
		} catch(SocketTimeoutException exception){
			log.error("Waited for ACK response till 3 seconds, nothing happened.");
			return false;
		} catch (IOException e) {
			log.error("IOException " + e.getMessage());
			return false;
		}
		return true;
	}

	protected boolean toSendUpdate(byte[] updateBytes, DataInputStream inStream, DataOutputStream outStream, Logger log){
		try{
			outStream.write(updateBytes, 0, updateBytes.length);
		} catch(SocketTimeoutException exception){
			log.error("Waited for ACK response till 3 seconds, nothing happened.");
			return false;
		} catch (IOException e) {
			log.error("IOException " + e.getMessage());
			return false;
		}
		return true;
	}

	protected boolean toSendNotification(DataInputStream inStream, DataOutputStream outStream, Logger log){
		return false;
	}

	public synchronized int getInt(byte[] bite){
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
	
	public synchronized byte[] getMarker(){
		byte[] marker = new byte[16];
		for(int i=0;i<marker.length;i++){
			int temp = 255;
			marker[i] = (byte)temp;
		}
		return marker;
	}   

}

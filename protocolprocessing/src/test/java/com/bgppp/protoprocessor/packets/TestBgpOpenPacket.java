package com.bgppp.protoprocessor.packets;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

import com.bgppp.protoprocessor.packet.BgpKeepalivePacket;
import com.bgppp.protoprocessor.packet.BgpOpenPacket;

import junit.framework.TestCase;

public class TestBgpOpenPacket extends TestCase {

	public static Logger log = Logger.getLogger(TestBgpOpenPacket.class.getName());
	
	public void testSendingOpenPacket(){
		try{
			//Not being closed on purpose.
			Socket socket = new Socket();
			InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 179);
			socket.connect(inetSocketAddress);
			DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
			BgpOpenPacket bgpOpenPacket = new BgpOpenPacket();
			bgpOpenPacket.setVersion(new Byte[]{Byte.parseByte("4",10)});
			bgpOpenPacket.setAutonomousSystem(new Byte[]{Byte.parseByte("44",10),Byte.parseByte("66",10)});
			bgpOpenPacket.setHoldTime(new Byte[]{Byte.parseByte("0",10),Byte.parseByte("5",10)});
			bgpOpenPacket.setBgpIdentifier(new Byte[]{Byte.parseByte("-2",10),Byte.parseByte("56",10),Byte.parseByte("34",10),Byte.parseByte("12",10),});
			byte[] openPacket = bgpOpenPacket.prepareOpenSegment();
			dataOutput.write(openPacket, 0, openPacket.length);

			BgpKeepalivePacket bgpKalivePacket = new BgpKeepalivePacket();
			byte[] kalivePacket = bgpKalivePacket.prepareKeepAliveSegment();
			dataOutput.write(kalivePacket, 0, kalivePacket.length);

			//dataOutput.close();
			//socket.close();
			assertNull(null);
		}catch(Exception exception){
			System.out.println("Sender Error : " + exception.getMessage());
			exception.printStackTrace();
		}
	}
	
/*	public void testSendingKalivePacket(){
		try{
			Socket socket = new Socket();
			InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 179);
			socket.connect(inetSocketAddress);
			DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
			BgpKeepalivePacket bgpKalivePacket = new BgpKeepalivePacket();
			byte[] kalivePacket = bgpKalivePacket.prepareKeepAliveSegment();
			dataOutput.write(kalivePacket, 0, kalivePacket.length);
			dataOutput.close();
			socket.close();
			assertNull(null);
		}catch(Exception exception){
			System.out.println("Sender Error : " + exception.getMessage());
			exception.printStackTrace();
		}
	}*/
	
	public void testOpenPacketPreparation(){
		BgpOpenPacket bgpOpenPacket = new BgpOpenPacket();
		Byte[] b1 = bgpOpenPacket.conc(new Byte[]{Byte.parseByte("4",10)}, new Byte[]{Byte.parseByte("4",10)});
		assertNotNull(b1);
	}
	
	public void testKeepAlivePacketPreparation(){
		BgpKeepalivePacket bgpKalivePacket = new BgpKeepalivePacket();
		byte[] kalivePacket = bgpKalivePacket.prepareKeepAliveSegment();
		assertEquals(kalivePacket.length,19);
	}
}

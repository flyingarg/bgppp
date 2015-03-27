package com.bgppp.protoprocessor.packets;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

import com.bgppp.protoprocessor.packet.BgpKeepalivePacket;
import com.bgppp.protoprocessor.packet.BgpNotificationPacket;
import com.bgppp.protoprocessor.packet.BgpOpenPacket;
import com.bgppp.protoprocessor.packet.BgpUpdatePacket;
import com.bgppp.protoprocessor.rules.*;

import java.util.*;

public class BgpPacketSender extends Thread {

	public static Logger log = Logger.getLogger(BgpPacketSender.class.getName());
	
	public void run(){
		try{
			//Make this sleep for 2 seconds so that the listener on the other end gets a chance to start listening.
			Thread.sleep(2000);
			
			//Create a simple socket, note that this is not ServerSocket.
			Socket socket = new Socket();
			
			//Create a socket address, they have a port and an ip-address. 
			//Just as computers are identified by ip-address, services are identified by port and ip address
			InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 179);
			
			//We then try to connect to the service, which was identified by ip-address and port above.
			socket.connect(inetSocketAddress);
			
			//If the above is successful, we will have a "socket" variable to which we can write data to or read data from.
			//We then open a data output stream, this will allow us to write data to this socket.
			DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());

			//We then create a BGP open packet.
			BgpOpenPacket bgpOpenPacket = new BgpOpenPacket();
			bgpOpenPacket.setVersion(new Byte[]{Byte.parseByte("4",10)});
			bgpOpenPacket.setAutonomousSystem(new Byte[]{Byte.parseByte("44",10),Byte.parseByte("66",10)});
			bgpOpenPacket.setHoldTime(new Byte[]{Byte.parseByte("0",10),Byte.parseByte("5",10)});
			bgpOpenPacket.setBgpIdentifier(new Byte[]{Byte.parseByte("-2",10),Byte.parseByte("56",10),Byte.parseByte("34",10),Byte.parseByte("12",10),});
			byte[] openPacket = bgpOpenPacket.prepareOpenSegment();
			dataOutput.write(openPacket, 0, openPacket.length);//We then write the data to the socket.

			//We then create a BGP keepalive packet.
			BgpKeepalivePacket bgpKalivePacket = new BgpKeepalivePacket();
			byte[] kalivePacket = bgpKalivePacket.prepareKeepAliveSegment();
			dataOutput.write(kalivePacket, 0, kalivePacket.length);//We then write the data to the socket.

			BgpUpdatePacket upPac = createUpdatePacket();
			byte[] updatePacket = upPac.prepareUpdateSegment();
			dataOutput.write(updatePacket, 0, updatePacket.length);

			BgpNotificationPacket no = new BgpNotificationPacket();
			byte[] noPacket = no.prepareNotificationSegment(2,2,1244);
			dataOutput.write(noPacket, 0, noPacket.length);
			//We then close the data stream and socket.
			dataOutput.close();
			socket.close();
			//assertNull(null);
		}catch(Exception exception){
			System.out.println("Sender Error : " + exception.getMessage());
			exception.printStackTrace();
		}
	}

	private BgpUpdatePacket createUpdatePacket(){
		OriginAttributeType oat = new OriginAttributeType(false, true, false, false, "1", "0");
		List<String> ases = new ArrayList<String>();
		ases.add("65522");
		ases.add("65511");
		AsPathAttributeType asat = new AsPathAttributeType(false, true, false, false, "3", "2", ases);
		NextHopAttributeType nhat = new NextHopAttributeType(false, true, false, false, "10.1.12.1");
		MultiExitDiscAttributeType medat = new MultiExitDiscAttributeType(true, false, false, false, "0");
		LocalPrefAttributeType lfa = new LocalPrefAttributeType(false, true, false, false, "100"); 
		/*MultiExitDiscAttributeType medat = new MultiExitDiscAttributeType(true, false, false, false, "1111111");
		AtomicAggregateAttributeType aaat = new AtomicAggregateAttributeType(false, true, false, false);
		AggregatorAttributeType aat = new AggregatorAttributeType(true, true, false, false, "33333", "20.30.40.50");*/
		List<Attribute> attribute = new ArrayList<Attribute>();
		attribute.add(oat);
		attribute.add(asat);
		attribute.add(nhat);
		attribute.add(medat);
		attribute.add(lfa);
//		attribute.add(aaat);
//		attribute.add(aat);
		String wrPrefixes = "";
		String nlri = "2/8";
		BgpUpdatePacket p = new BgpUpdatePacket(nlri, attribute, wrPrefixes);
		return p;
	}
}

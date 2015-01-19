package com.bgppp.protoprocessor.packets;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.TestCase;

import com.bgppp.protoprocessor.packet.BgpKeepalivePacket;
import com.bgppp.protoprocessor.packet.BgpOpenPacket;

/**
 * Run this case with the wireshark running and you should be able to see packets.
 * Just to assist in analyzing all the packets being created. 
 * NOT really a TEST case.
 */
public class TestPacketReceiver extends TestCase{

	public int packetsConnected = 0;
	private boolean running = true;
	
	public void testIncomingPackets(){
		ServerSocket socket = null;
		Socket listen = null;
		try{
			//We create a ServerSocket. Server sockets help us to create TCP services. A TCP service is identified using an ip-address and port.
			socket = new ServerSocket();
			
			//We create an ip-address.
			InetAddress address = InetAddress.getByName("127.0.0.1");
			
			//We then bind the above ip-address and port to a socket. If there is no error till here, then the socket is ready.
			socket.bind(new InetSocketAddress(address, 179));
		}catch(Exception exception){
			System.out.println("Socket Error : "+exception.getMessage());
			exception.printStackTrace();
		}
		
		//We start a thread that will become a client and start sending BGP messages to this test case.
		BgpPacketSender packetSender = new BgpPacketSender();
		packetSender.run();
		
		while(running){
			try{
				// This is where the socket becomes active. 
				// The code will run till this section and then not execute further till it receives a connection. Once it receives a connection,
				// we can start getting input and outputstreams from the returned socket. 
				// As we just need to read data in our case we are getting input streams.
				listen = socket.accept();
				
				// We then will get the input stream from the listen socket.
				DataInputStream dataInput = new DataInputStream(listen.getInputStream());
				String response = null;
				boolean isReading = true;
				int ffByteCount = 0;
				while(isReading){
					byte bite = 0;

					// Read the data coming into the listen socket byte by byte.
					bite = dataInput.readByte();
					System.out.println(bite);

					// We are counting if each byte that we receive is 'ff', if there are 16 continuous 'ff', it means the packet is a bgp packet.
					// Refer http://www.ietf.org/rfc/rfc4271.txt section 4.1
					// BTW, 1 bite = 1 octate
					if(bite == (byte)255){
						ffByteCount+=1;
					}else{
						ffByteCount = 0;
					}
					
					// As you can see we are checking the size 16 here.
					if(ffByteCount == 16){
						packetsConnected += 1;
						byte[] packLen = new byte[2];
						dataInput.read(packLen);
						byte[] packType = new byte[1];
						dataInput.read(packType);
						byte[] packRest = null;
						if(getInt(packLen) > 19){
							packRest = new byte[getInt(packLen)-19];
							dataInput.read(packRest);
						}
						ffByteCount = 0;
						processPacket(getInt(packLen),getInt(packType),packRest);
					}
				}
				System.out.println("-"+response);
				listen.close();
				assertNotNull(null);
			// The two catches below are the clean up code that will make sure that the listen and socket variables are closed properly in case there are exceptions
			// EOF exceptions occur when we try to read data from a closed socket. 
			}catch(EOFException exception){
				System.out.println("Stream closed, closing connection: " + exception.getMessage());
				running = false;
				try {
					if(listen!=null)listen.close();
					if(socket!=null)socket.close();
				} catch (IOException e) {
					System.out.println("Residual Exception : " + e.getMessage());
				}
			// Any other exceptions are caught here.
			}catch(Exception exception){
				running = false;
				try {
					if(listen!=null)listen.close();
					if(socket!=null)socket.close();
				} catch (IOException e) {
					System.out.println("Residual Exception : " + e.getMessage());
				}
				System.out.println("Socket Error : "+exception.getMessage());
				exception.printStackTrace();
			}
		}
	}

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

	public void processPacket(int packLen,int packType,byte[] packRest){
		System.out.println("Packet received, type "+packType+" length "+ packLen);
		return;
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

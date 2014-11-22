package com.bgppp.protoprocessor.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import junit.framework.TestCase;

public class TestPacketReceiver extends TestCase{

	//public static Logger log = Logger.getLogger(TestPacketReceiver.class.getName());
	public int packetsConnected = 0;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Run this case with the wireshark running and you should be able to see packets.
	 * Just to assist in analyzing all the packets being create. 
	 * NOT really a TEST case.
	 */
	public void testIncomingPackets(){
		ServerSocket socket = null;
		Socket listen = null;
		try{
			socket = new ServerSocket();
			InetAddress address = InetAddress.getByName("127.0.0.1");
			socket.bind(new InetSocketAddress(address, 179));
		}catch(Exception exception){
			System.out.println("Socket Error : "+exception.getMessage());
			exception.printStackTrace();
		}
		Date date = new Date();
		Long timer = date.getTime();
		System.out.println("Started at " + timer);
		while(true){
			System.out.println("-------");
			try{
				listen = socket.accept();
				DataInputStream dataInput = new DataInputStream(listen.getInputStream());
				DataOutputStream dataOutput = new DataOutputStream(listen.getOutputStream());
				//byte bite = 0;
				String response = null;
				boolean isReading = true;
				int ffByteCount = 0;
				while(isReading){
					if((new Date().getTime()) > (timer)+60000){
						isReading = false;
						System.out.println("Now " + (new Date().getTime()));
						listen.close();
					}
	                byte bite = 0;
	                //if(dataInput.available()>0){
	                    bite = dataInput.readByte();
						System.out.println(bite);
	                //}
	                if(bite == (byte)255){
	                    ffByteCount+=1;
	                }else{
	                    ffByteCount = 0;
	                }
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
				assertNotNull(response);
			}catch(Exception exception){
				try {
					listen.close();
				} catch (IOException e) {

				}
				System.out.println("Socket Error : "+exception.getMessage());
				exception.printStackTrace();
			}
		}
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

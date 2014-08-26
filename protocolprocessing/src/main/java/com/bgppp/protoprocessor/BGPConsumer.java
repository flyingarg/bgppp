package com.bgppp.protoprocessor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import com.bgppp.protoprocessor.graphs.TimeOutUtils;

public class BGPConsumer extends Thread {
	public static Logger log = Logger.getLogger(BGPConsumer.class.getName());
	private BgpConfig bgpConfig = null;
	private Link link = null;
	private DataInputStream inStream = null;
	private DataOutputStream outStream = null;
	private int PORT = 179;
	private boolean isRunning = false;

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public BGPConsumer(BgpConfig bgpConfig, Link link) {
		this.bgpConfig = bgpConfig;
		this.link = link;
		this.setName(bgpConfig.getRouterName() + "_consumer");
	}

	@Override
	public void run() {
		log.info("Starting Consumer : " + bgpConfig.getRouterName() + ":" +link);
		ServerSocket serverSocket = null;
		try {
			InetAddress address = bgpConfig.getAddressAndMaskByName(link.getSourceAddressName()).getAddress();
			if (address == null) {
				log.severe("Address is null, what did you do ? ");
			}

			//TODO : How do we allow multiple accepts on listen sockets ? try to implement so that if a link already has 
			//overtaken a server then others have to find the over bearer and then use that to further create usable accept
			//SOLVED : Check test cases.
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(address, PORT));
			isRunning = true;
			Socket listen = serverSocket.accept();

			inStream = new DataInputStream(listen.getInputStream());
			outStream = new DataOutputStream(listen.getOutputStream());
			//BufferedReader inStream = new BufferedReader(new InputStreamReader(listen.getInputStream()));
			log.info("BGPConsumer : starting to read" + address);
			while (this.isRunning) {
				String line = "";
				byte temp = 0;
				while((temp = inStream.readByte()) != '\n'){
					line = line + new String(new byte[]{temp});
				}
				log.info("BGPThread[" + bgpConfig.getRouterName() + "] : "+ line);
				if (line.trim().startsWith("open")) {
					onRecevOPEN(outStream, line, bgpConfig);
				} else if (line.trim().startsWith("update")) {
					onRecvUPDATE(outStream, line, bgpConfig);
				} else if (line.trim().startsWith("notification")) {
					onRecvNOTIFICATION(outStream, line, bgpConfig);
				} else if (line.trim().startsWith("keepalive")) {
					onRecvKEEPALIVE(outStream, line, bgpConfig);
				} else {

				}
			}
			if (!isRunning()) {
				try {
					if (serverSocket != null)
						serverSocket.close();
					if (inStream != null)
						inStream.close();
					if (outStream != null)
						outStream.close();
				} catch (IOException e) {
					log.severe("Error closing connections" + e.getMessage());
				}
			}
		} catch (IOException exception) {
			log.severe("IO Stream Read error : " + exception.getMessage());
			if (serverSocket != null)
				try {
					if (serverSocket != null)
						serverSocket.close();
					if (inStream != null)
						inStream.close();
					if (outStream != null)
						outStream.close();
				} catch (IOException e) {
					log.severe("Error closing resources after exception occured."+ e.getMessage());
				}
			}
	}

	// RECV Ops
	private void onRecvNOTIFICATION(DataOutputStream os, String line,
			BgpConfig config) {
		String response[] = line.split(":");
	}

	private void onRecvUPDATE(DataOutputStream os, String line, BgpConfig config) {
		/*
		 * TODO : Is it path ? Then combo of two nodes, if its a single node then it must be pretty easy. Let us assume that it is a list of paths
		 * 1. List of paths to remove -  
		 * 2. List of paths to add - 
		 */
		String response[] = line.split("|");//left side paths to be added //right side paths to be removed
		String pathsToRemove[] = response[1].split(";");
		String pathsToAdd[] = response[0].split(";");
		
		
	}

	private void onRecevOPEN(DataOutputStream os, String line, BgpConfig config) {
		String response[] = line.split(":");
		String autonomousSystem = response[1];
		String holdTime = response[2];
		String bgpIdentifier = response[3];
		String optParam1 = response[4];
		String optParam2 = response[5];
		String optParam3 = response[6];

		// If holdTime is not mentioned then the hold time is set to 3 secs.
		// This is the number of minutes to wait between each "keelalive"
		// message.
		if ((new Long(holdTime)) < 3) {
			holdTime = "3";
		}

		// Send the "keepalive" message in a for loop.
		Thread keepaliveThread = new KeepaliveTimer(os, "keepalive\n");
		keepaliveThread.start();
	}

	private void onRecvKEEPALIVE(DataOutputStream os, String line,
			BgpConfig config) {
		String response[] = line.split(":");
	}

	class KeepaliveTimer extends Thread {
		DataOutputStream outStream = null;
		String message = null;
		boolean isRunning = true;

		public boolean isRunning() {
			return isRunning;
		}

		public void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}

		public KeepaliveTimer(DataOutputStream outStream, String message) {
			this.outStream = outStream;
			this.message = message;
		}

		public void run() {
			while (isRunning) {
				try {
					this.sleep(TimeOutUtils.KEEPALIVE);
					log.info("Sending keepalive message");
					outStream.writeBytes(message);
				} catch (InterruptedException e) {
					log.severe("Failed to send keepalive message");
				} catch (IOException e) {
					log.severe(e.getMessage());
				}
			}
		}
	}
}

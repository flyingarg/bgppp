package com.bgppp.protoprocessor.remote;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import org.apache.log4j.*;

public class SshServerDaemon extends Thread{

	public static Logger log  = Logger.getLogger(SshServerDaemon.class.getName());

	public void run(){
		SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setPort(22);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		log.info("Mohit Raju");
	}

}

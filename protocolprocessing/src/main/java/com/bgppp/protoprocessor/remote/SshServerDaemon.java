package com.bgppp.protoprocessor.remote;

import org.apache.sshd.server.keyprovider.*;
import org.apache.sshd.*;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.*;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.Command;

import org.apache.log4j.*;

import com.bgppp.protoprocessor.*;

public class SshServerDaemon extends Thread{

	public static Logger log  = Logger.getLogger(SshServerDaemon.class.getName());
	private BgpConfig config = null;
	
	public SshServerDaemon(BgpConfig config){
			this.config = config;
			setName(config.getRouterName());
	}   

	public void run(){
		try{
			SshServer sshd = SshServer.setUpDefaultServer();
			sshd.setPort(2222);
			sshd.setHost(config.getLinks().get(0).getSourceAddress().toString());
			sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("/repo/hostkey.ser"));
			sshd.setShellFactory(new EchoShellFactory());
			sshd.setCommandFactory(new ScpCommandFactory(new CommandFactory() {
				public Command createCommand(String command) {
					return new ProcessShellFactory(command.split("<SPAN class=\"code-quote\">\" ")).create();
				}
			}));
			sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
				public boolean authenticate(String username, String password, ServerSession session) {
					return "admin".equals(username) && "admin".equals(password);
				}
			});
			sshd.start();
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}
	
	public BgpConfig getConfig(){
		return this.config;
	}

}

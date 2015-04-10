package com.bgppp.protoprocessor.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

import com.bgppp.protoprocessor.BgpConfig;

import org.apache.log4j.*;
/*
 * Copied from <a href="https://svn.apache.org/repos/asf/mina/sshd/trunk/sshd-core/src/test/java/org/apache/sshd/util/EchoShellFactory.java">Apache MINA SSHD Project</a>
 * They did not include these in the latest releases, considering these are only util files.
 *
 * Modifying it so that we can use custom commands
 */
public class EchoShellFactory implements Factory<Command> {
	public static final Logger log = 	Logger.getLogger(EchoShellFactory.class);
	private BgpConfig config = null;

	public EchoShellFactory(BgpConfig config){
		this.config = config;
	}

	public Command create() {
		return new EchoShell(config);
	}

	public static class EchoShell implements Command, Runnable {

		private InputStream in;
		private OutputStream out;
		private OutputStream err;
		private ExitCallback callback;
		private Environment environment;
		private Thread thread;
		private BgpConfig config = null;


		public EchoShell(BgpConfig config){
			this.config = config;
		}

		public InputStream getIn() {
			return in;
		}

		public OutputStream getOut() {
			return out;
		}

		public OutputStream getErr() {
			return err;
		}

		public Environment getEnvironment() {
			return environment;
		}

		public void setInputStream(InputStream in) {
			this.in = in;
		}

		public void setOutputStream(OutputStream out) {
			this.out = out;
		}

		public void setErrorStream(OutputStream err) {
			this.err = err;
		}

		public void setExitCallback(ExitCallback callback) {
			this.callback = callback;
		}

		public void start(Environment env) throws IOException {
			environment = env;
			thread = new Thread(this, "EchoShell");
			thread.start();
		}

		public void destroy() {
			thread.interrupt();
		}

		public void run() {
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			try {
				for (;;) {
					String s = r.readLine();
					if (s == null) {
						return;
					}
					if ("exit".equals(s)) {
						return;
					}
					s = processInput(s);
					out.write((s + "\n%").getBytes());
					out.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				callback.onExit(0);
			}
		}

		public String processInput(String s){
			SSHCommands commands = new SSHCommands();
			if(s.trim().equals("stats")){
				return commands.stats(config);
			}if(s.trim().startsWith("linkadd")){
				return	commands.linkadd(s, config);
			}if(s.trim().startsWith("linkls")){
				return commands.linkls(s,config);
			}if(s.trim().startsWith("ifadd")){
				return commands.ifadd(s, config);
			}if(s.trim().startsWith("ifdel")){
				return commands.ifdel(s, config);
			}if(s.trim().startsWith("ifls")){
				return commands.ifls(s, config);
			}if(s.trim().startsWith("printrule")){
				return commands.getRule(s, config);
			}if(s.trim().equals("help")){
				return "Available Commands : ifadd, ifls, ifdel, linkadd, linkls, help, stats\n";
			}else{
				return "Type help to get available commands\n";
			}
		}
	}
}

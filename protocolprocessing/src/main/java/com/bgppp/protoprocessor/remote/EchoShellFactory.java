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
import com.bgppp.protoprocessor.ProducerConsumerStore;
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
					out.write((s + "\n").getBytes());
					out.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				callback.onExit(0);
			}
		}

		public String processInput(String s){
			String response = "";
			if(ProducerConsumerStore.hasNewUpdates()){
				for(String key : ProducerConsumerStore.getBgpProducersMap().keySet()){
					if(!key.contains(config.getRouterName()))
						continue;
					String name = ProducerConsumerStore.getBgpProducersMap().get(key).getName();
					boolean alive = ProducerConsumerStore.getBgpProducersMap().get(key).isAlive();
					boolean running = ProducerConsumerStore.getBgpProducersMap().get(key).isRunning();
					int nuOfOpenRcvd = ProducerConsumerStore.getBgpProducersMap().get(key).getCountOpen();
					int nuOfKASentRcvd = ProducerConsumerStore.getBgpProducersMap().get(key).getCountKA();
					int nuOfUpdateRcvd = ProducerConsumerStore.getBgpProducersMap().get(key).getCountUpdate();
					int nuOfNotificationsRcvd = ProducerConsumerStore.getBgpProducersMap().get(key).getCountNotification();
					int nuOfMalformedRcvd = ProducerConsumerStore.getBgpProducersMap().get(key).getCountMalformed();
					response += "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
					response += "PRODUCER-"+name+"\n";
					response += "-------------------------------------------------------------------\n";
					response += "|Alive: " + alive + "|Running: " + running + "|MalformedCount: " + nuOfMalformedRcvd ;
					response += "|KACount: " + nuOfKASentRcvd + "|OpenCount: " + nuOfOpenRcvd + "|UpdateCount: " + nuOfUpdateRcvd + "|NotificationCount: " + nuOfNotificationsRcvd +"|\n"; 
					response += "-------------------------------------------------------------------\n";
					response += "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n\n";
				}
				for(String key : ProducerConsumerStore.getBgpConsumersMap().keySet()){
					if(!key.contains(config.getRouterName()))
						continue;
					for(String k : ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().keySet()){
						String name = k;
						boolean alive = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).isAlive();
						boolean running = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).isRunning();
						int nuOfKASentRcvd = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).getCountKA();
						int nuOfOpenRcvd = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).getCountOpen();
						int nuOfUpdateRcvd = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).getCountUpdate();
						int nuOfNotificationsRcvd = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).getCountNotification();
						int nuOfMalformedRcvd = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).getCountMalformed();
						response += "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
						response += "CONSUMER-"+name+"\n";
						response += "-------------------------------------------------------------------\n";
						response += "|Alive: " + alive + "|Running: " + running + "|MalformedCount: " + nuOfMalformedRcvd ;
						response += "|KACount: " + nuOfKASentRcvd + "|OpenCount: " + nuOfOpenRcvd + "|UpdateCount: " + nuOfUpdateRcvd + "|NotificationCount: " + nuOfNotificationsRcvd +"|\n"; 
						response += "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
					}
				}
			}
			if(s.trim().equals("stats")){
				return response;
			}else{
				return s;
			}
		}
	}
}

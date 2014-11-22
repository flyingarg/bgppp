package com.bgppp.protoprocessor.monitor;

import java.util.*;

import com.bgppp.protoprocessor.*;

import org.apache.log4j.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import javax.servlet.*;
import com.bgppp.protoprocessor.web.*;

public class MonitorProducerConsumer extends Thread{
	public static Logger log = Logger.getLogger(MonitorProducerConsumer.class.getName());
	boolean running = false;
	private List<ProducerStats> producerStatsList= new ArrayList<ProducerStats>();
	private List<ConsumerStats> consumerStatsList= new ArrayList<ConsumerStats>();
	/*
	 * 1. Get producers and consumers hash from the StoreProducerConsumer.
	 * 2. Iterate through the each producer and consumer.
	 * 3. Get and print the information.
	 */
	public void run(){
	//TODO : Telnet session - print data on requests 
	//TODO : Live view session
	//TODO : provide rest api's
		
		/*
		 * Jersey initiation
		 */
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		Server jettyServer = new Server(8787);
		jettyServer.setHandler(context);
		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",Stats.class.getCanonicalName());
		try {
			jettyServer.start();
			jettyServer.join();
		} catch(Exception e){
			//log.error(e.getMessagte());
		} finally {
			jettyServer.destroy();
		}
		/*
		 * Command line stats
		 */
		while(true){
			try{
				Thread.sleep(10000);
			}catch(Exception e){
				log.info("Error : "+ e.getMessage());
			}
			if(ProducerConsumerStore.hasNewUpdates()){
				producerStatsList = new ArrayList<ProducerStats>();
				log.info("Producer Stats : ");
				for(String key : ProducerConsumerStore.getBgpProducersMap().keySet()){
					ProducerStats proStat = new ProducerStats();
					proStat.name = ProducerConsumerStore.getBgpProducersMap().get(key).getName();
					proStat.alive = ProducerConsumerStore.getBgpProducersMap().get(key).isAlive();
					proStat.running = ProducerConsumerStore.getBgpProducersMap().get(key).isRunning();
					
					/*proStat.open = ProducerConsumerStore.getBgpProducer().get(key).getBgpConfig().
					proStat.nuOfOpenRcvd = ProducerConsumerStore.getBgpProducer().get(key).getBgpConfig().
					proStat.nuOfKASentRcvd = ProducerConsumerStore.getBgpProducer().get(key).getBgpConfig().
					proStat.nuOfUpdateRcvd = ProducerConsumerStore.getBgpProducer().get(key).getBgpConfig().
					proStat.nuOfNotificationsRcvd = ProducerConsumerStore.getBgpProducer().get(key).getBgpConfig().*/
					
					producerStatsList.add(proStat);
					log.info("Name: " + proStat.name + "|Alive: " + proStat.alive + "|Running: " + proStat.running + "|");
				}
				consumerStatsList = new ArrayList<ConsumerStats>();
				log.info("Consumer Stats : ");
				for(String key : ProducerConsumerStore.getBgpConsumersMap().keySet()){
					for(String k : ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().keySet()){
						ConsumerStats conStat = new ConsumerStats();
						conStat.name = k;
						conStat.nuOfKASentRcvd = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).getCountKA();
						conStat.nuOfOpenRcvd = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).getCountOpen();
						conStat.nuOfUpdateRcvd = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).getCountUpdate();
						conStat.nuOfNotificationsRcvd = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).getCountNotification();
						conStat.alive = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).isAlive();
						conStat.running = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).isRunning();
						conStat.nuOfMalformedRcvd = ProducerConsumerStore.getBgpConsumersMap().get(key).getConnsFromPeers().get(k).getCountMalformed();
						consumerStatsList.add(conStat);
						log.info("|Name: " + conStat.name + "|Alive: " + conStat.alive + "|Running: " + conStat.running + "|MalformedCount: " + conStat.nuOfMalformedRcvd);
						log.info("|KACount: " + conStat.nuOfKASentRcvd + "|OpenCount: " + conStat.nuOfOpenRcvd + "|UpdateCount: " + conStat.nuOfUpdateRcvd + "|NotificationCount: " + conStat.nuOfNotificationsRcvd +"|");
					}
				}
			}
		}
	}

	public boolean isRunning(){
		return running;
	}
	public void setRunning(boolean running){
		this.running = running;
	}


	//TODO : Generate getters and setters, it NEEd rises AT ALL.
	class ConsumerStats{
		public String name;
		public boolean alive;//Has a live with its corresponding peer.
		public boolean running;//The thread is running.
		public boolean open;//Has opened connection with the peer.
		public int nuOfOpenRcvd;//Number of OPEN req rcvd
		public int nuOfKASentRcvd;//No of KEEPALIVE rcvd
		public int nuOfUpdateRcvd;
		public int nuOfNotificationsRcvd;
		public int nuOfMalformedRcvd;
	}
	class ProducerStats{
		public String name;
		public boolean running;//The thread is running.
		public boolean alive;//Has a live with its corresponding peer.
		public boolean open;//It has open connection to the peer.
		public int nuOfOpenSent;//Number of OPEN req sent
		public int nuOfKASent;//No of KEEPALIVE sent.
		public int nuOfUpdateSent;
		public int nuOfNotificationsSent;
	}
}

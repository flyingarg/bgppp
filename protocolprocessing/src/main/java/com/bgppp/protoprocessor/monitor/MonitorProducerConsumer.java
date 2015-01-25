package com.bgppp.protoprocessor.monitor;

import java.util.*;

import com.bgppp.protoprocessor.*;

import org.apache.log4j.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.Handler;

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
		Server jettyServer = new Server(8787);
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",Stats.class.getCanonicalName());
		
		WebAppContext jerseyPocClientWebApp = new WebAppContext();
		jerseyPocClientWebApp.setWar("../../gui/target/gui.war");
		jerseyPocClientWebApp.setContextPath("/gui");
	
		HandlerCollection handlerCollection = new HandlerCollection();
		handlerCollection.setHandlers(new Handler[]{jerseyPocClientWebApp,context});
		jettyServer.setHandler(handlerCollection);

		try {
			jettyServer.start();
			jettyServer.join();
		} catch(Exception e){
			log.error(e.getMessage());
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
				for(String configName : ProducerConsumerStore.getBgpConfigMap().keySet()){
					for(String key : ProducerConsumerStore.getBgpConfigByName(configName).getProducers().keySet()){
						ProducerStats proStat = new ProducerStats();
						proStat.name = ProducerConsumerStore.getBgpConfigByName(configName).getProducers().get(key).getName();
						proStat.alive = ProducerConsumerStore.getBgpConfigByName(configName).getProducers().get(key).isAlive();
						proStat.running = ProducerConsumerStore.getBgpConfigByName(configName).getProducers().get(key).isRunning();
						
						/*proStat.open = ProducerConsumerStore.getBgpProducer().get(key).getBgpConfig().
						proStat.nuOfOpenRcvd = ProducerConsumerStore.getBgpProducer().get(key).getBgpConfig().
						proStat.nuOfKASentRcvd = ProducerConsumerStore.getBgpProducer().get(key).getBgpConfig().
						proStat.nuOfUpdateRcvd = ProducerConsumerStore.getBgpProducer().get(key).getBgpConfig().
						proStat.nuOfNotificationsRcvd = ProducerConsumerStore.getBgpProducer().get(key).getBgpConfig().*/
						
						producerStatsList.add(proStat);
						log.info("Name: " + proStat.name + "|Alive: " + proStat.alive + "|Running: " + proStat.running + "|");
					}
					consumerStatsList = new ArrayList<ConsumerStats>();
					for(String key : ProducerConsumerStore.getBgpConfigMap().keySet()){
						for(String k : ProducerConsumerStore.getBgpConfigByName(configName).getConsumers().keySet()){
							ConsumerStats conStat = new ConsumerStats();
							conStat.name = k;
							conStat.nuOfKASentRcvd = ProducerConsumerStore.getBgpConfigByName(configName).getConsumers().get(key).getConnsFromPeers().get(k).getCountKA();
							conStat.nuOfOpenRcvd = ProducerConsumerStore.getBgpConfigByName(configName).getConsumers().get(key).getConnsFromPeers().get(k).getCountOpen();
							conStat.nuOfUpdateRcvd = ProducerConsumerStore.getBgpConfigByName(configName).getConsumers().get(key).getConnsFromPeers().get(k).getCountUpdate();
							conStat.nuOfNotificationsRcvd = ProducerConsumerStore.getBgpConfigByName(configName).getConsumers().get(key).getConnsFromPeers().get(k).getCountNotification();
							conStat.alive = ProducerConsumerStore.getBgpConfigByName(configName).getConsumers().get(key).getConnsFromPeers().get(k).isAlive();
							conStat.running = ProducerConsumerStore.getBgpConfigByName(configName).getConsumers().get(key).getConnsFromPeers().get(k).isRunning();
							conStat.nuOfMalformedRcvd = ProducerConsumerStore.getBgpConfigByName(configName).getConsumers().get(key).getConnsFromPeers().get(k).getCountMalformed();
							consumerStatsList.add(conStat);
							log.info("|Name: " + conStat.name + "|Alive: " + conStat.alive + "|Running: " + conStat.running + "|MalformedCount: " + conStat.nuOfMalformedRcvd);
							log.info("|KACount: " + conStat.nuOfKASentRcvd + "|OpenCount: " + conStat.nuOfOpenRcvd + "|UpdateCount: " + conStat.nuOfUpdateRcvd + "|NotificationCount: " + conStat.nuOfNotificationsRcvd +"|");
						}
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

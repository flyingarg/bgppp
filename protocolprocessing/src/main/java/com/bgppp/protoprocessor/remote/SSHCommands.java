package com.bgppp.protoprocessor.remote;

import org.apache.log4j.*;

import com.bgppp.protoprocessor.*;
import com.bgppp.protoprocessor.rules.Rule;
import com.bgppp.protoprocessor.utils.*;

import java.net.*;
import java.util.HashMap;

public class SSHCommands{
	private static final Logger log = Logger.getLogger(SSHCommands.class);

	public String stats(BgpConfig config){
		String response = "";
		System.out.println("No of Configurs : " + ProducerConsumerStore.getBgpConfigMap().size());
		if(ProducerConsumerStore.hasNewUpdates()){
			for(String key : ProducerConsumerStore.getBgpConfigByName(config.getRouterName()).getProducers().keySet()){
				String name = ProducerConsumerStore.getBgpConfigByName(config.getRouterName()).getProducers().get(key).getName();
				boolean alive = ProducerConsumerStore.getBgpConfigByName(config.getRouterName()).getProducers().get(key).isAlive();
				boolean running = ProducerConsumerStore.getBgpConfigByName(config.getRouterName()).getProducers().get(key).isRunning();
				int nuOfOpenRcvd = ProducerConsumerStore.getBgpConfigByName(config.getRouterName()).getProducers().get(key).getCountOpen();
				int nuOfKASentRcvd = ProducerConsumerStore.getBgpConfigByName(config.getRouterName()).getProducers().get(key).getCountKA();
				int nuOfUpdateRcvd = ProducerConsumerStore.getBgpConfigByName(config.getRouterName()).getProducers().get(key).getCountUpdate();
				int nuOfNotificationsRcvd = ProducerConsumerStore.getBgpConfigByName(config.getRouterName()).getProducers().get(key).getCountNotification();
				int nuOfMalformedRcvd = ProducerConsumerStore.getBgpConfigByName(config.getRouterName()).getProducers().get(key).getCountMalformed();
				response += "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				response += "PRODUCER-"+name+"\n";
				response += "-------------------------------------------------------------------\n";
				response += "|Alive: " + alive + "|Running: " + running + "|MalformedCount: " + nuOfMalformedRcvd ;
				response += "|KACount: " + nuOfKASentRcvd + "|OpenCount: " + nuOfOpenRcvd + "|UpdateCount: " + nuOfUpdateRcvd + "|NotificationCount: " + nuOfNotificationsRcvd +"|\n"; 
				response += "-------------------------------------------------------------------\n";
				response += "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n\n";
			}
			for(String key : ProducerConsumerStore.getBgpConfigByName(config.getRouterName()).getConsumers().keySet()){
				BgpConsumer bgpConsumer = ProducerConsumerStore.getBgpConfigByName(config.getRouterName()).getConsumers().get(key);
				for(String k : bgpConsumer.getConnsFromPeers().keySet()){
					String name = k;
					boolean alive = bgpConsumer.getConnsFromPeers().get(k).isAlive();
					boolean running = bgpConsumer.getConnsFromPeers().get(k).isRunning();
					int nuOfKASentRcvd = bgpConsumer.getConnsFromPeers().get(k).getCountKA();
					int nuOfOpenRcvd = bgpConsumer.getConnsFromPeers().get(k).getCountOpen();
					int nuOfUpdateRcvd = bgpConsumer.getConnsFromPeers().get(k).getCountUpdate();
					int nuOfNotificationsRcvd = bgpConsumer.getConnsFromPeers().get(k).getCountNotification();
					int nuOfMalformedRcvd = bgpConsumer.getConnsFromPeers().get(k).getCountMalformed();
					response += "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
					response += "CONSUMER-"+name+"\n";
					response += "-------------------------------------------------------------------\n";
					response += "|Alive: " + alive + "|Running: " + running + "|MalformedCount: " + nuOfMalformedRcvd ;
					response += "|KACount: " + nuOfKASentRcvd + "|OpenCount: " + nuOfOpenRcvd + "|UpdateCount: " + nuOfUpdateRcvd + "|NotificationCount: " + nuOfNotificationsRcvd +"|\n"; 
					response += "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
				}
			}
		}
		return response;
	}

	public String linkadd(String s, BgpConfig config){
		String split[] = s.trim().split(" ");
		Link link = null;
		if(split.length != 3)
			return "USAGE : %linkadd sourceaddress destinationaddress\n";
		try{
			String sourceInterface = split[1].trim();
			InetAddress destinationAddress = InetAddress.getByName(split[2]);
			link = config.addLink(sourceInterface, destinationAddress);
			if(link == null)
				return "ERROR : Link not created";
		}catch(UnknownHostException e){
			log.error("Unknown Host Exception : " + e.getMessage());
			return "ERROR : Error creating link";
		}
		config.spwanProducersConsumers(link);
		return "SUCCESS : Link added";
	}

	public String linkls(String s, BgpConfig config){
		String response = "";
		String split[] = s.trim().split(" ");
		if(split.length != 1)
			return "USAGE : linkls\n";
		response += "Link Name\tSource Address\tDestinationAddress\tStatus\n";
		for(Link link : config.getLinks()){
			response += link.getSourceAddressName() + "\t" + link.getSourceAddress() + "\t" + link.getDestinationAddress() + "\t" + link.isAlive() + "\n";
		}
		return response;

	}

	public String ifadd(String s, BgpConfig config){
		String split[] = s.trim().split(" ");
		if(split.length != 4)
			return "USAGE :  %ifadd ifname ifaddress ifmask\n";
		AddressAndMask anm = new AddressAndMask(split[1],split[2],split[3]);
		config.addAddressAndMask(anm);
		WrappedHash.addIfconfig(config, anm);
		return "SUCCESS : Added Address\n";

	}

	public String ifdel(String s, BgpConfig config){
		String split[] = s.trim().split(" ");
		if(split.length != 2)
			return "USAGE :  %ifdel ifname\n";
		WrappedHash.removeIfconfig(config, config.getAddressAndMaskByName(split[1]));
		return "SUCCESS : Removed the ifconfig";
	}

	public String ifls(String s, BgpConfig config){
		String response = "";
		String split[] = s.trim().split(" ");
		if(split.length != 1)
			return "USAGE : %ifls\n";
		response+="IfName\tIPAddress\tMask\n";
		for(String anmName : config.getAddressAndMasks().keySet()){
			response+=anmName+"\t"+config.getAddressAndMaskByName(anmName).getAddress()+"\t"+config.getAddressAndMaskByName(anmName).getMask()+"\n";
		}
		return response;

	}

	public String getRule(String s, BgpConfig config){
		String response = "";
		String split[] = s.trim().split(" ");
		if(split.length != 2)
			return "USAGE :  %printrule [local|in|out]\n";
		response+="NETWORK\tNEXT_HOP\tAS_PATH\tORIGIN\tLOCAL_PREF\tMUTI_EXIT_DISC\n";
		HashMap<String, Rule> rules = null;
		if("local".equals(split[1])){
			rules = config.getRuleStore().getLocalRib();
		}else if("in".equals(split[1])){
			rules = config.getRuleStore().getAdjRibIn();
		}else if("out".equals(split[1])){
			rules = config.getRuleStore().getAdjRibOut();
		}
		if(rules == null){
			return "Error: Could not process request.\n";
		}
		for(String key : rules.keySet()){
			Rule rule = rules.get(key);
			response+=rule.getNetwork()+"\t"+
				rule.getNextHop().getNextHop()+"\t"+
				rule.getPath().getPathSegmentAsString()+"\t"+
				rule.getOrigin().getAttrValue()+"\t"+
				rule.getLocalPref().getLocalPref()+"\t"+
				rule.getMetric().getMultiExitDiscriminator()+"\n";
		}
		return response;
	}
}

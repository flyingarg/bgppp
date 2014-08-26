package com.bgppp.protoprocessor.utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.bgppp.protoprocessor.BgpConfig;
import com.bgppp.protoprocessor.Link;
import com.bgppp.protoprocessor.WrappedHash;

public class ConfigHelper {

	public static Logger log = Logger.getLogger(ConfigHelper.class.getName());
	
	public static HashMap<String, BgpConfig> stringToBgpConfig(ArrayList<String> configsList) {
		HashMap<String, BgpConfig> responseBgpConfigs = new HashMap<String, BgpConfig>();
		for (String configAsString : configsList) {
			BgpConfig config = new BgpConfig("");
			log.info("Configuration - " + configAsString);
			String[] parameters = configAsString.split("\\^\\&");
			for (int i = 0; i < parameters.length; i++) {
				if (parameters[i].trim().startsWith("[")) {//Name
					String routerName = parameters[i].substring(parameters[i].indexOf("[") + 1,parameters[i].indexOf("]"));
					config.setRouterName(routerName);
				} else if (parameters[i].trim().matches("[0-9a-zA-Z]*[-][0-9]*.[0-9]*.[0-9]*.[0-9]*")) {//Links
					try {
						config.addLink(parameters[i].split("-")[0].trim(), InetAddress.getByName(parameters[i].split("-")[1].trim()));
					} catch (Exception exception) {
						log.severe(exception.getMessage());
					}
				} else if (parameters[i].trim().matches("[0-9a-zA-Z]*[/][0-9]*.[0-9]*.[0-9]*.[0-9]*[/][0-9]*.[0-9]*.[0-9]*.[0-9]*")) {//Ip Address
					try {
						config.addAddress(new AddressAndMask(parameters[i].trim().split("\\/")[0], parameters[i].trim().split("\\/")[1],parameters[i].trim().split("\\/")[2]));
					} catch (Exception exception) {
						log.severe(exception.getMessage());
					}
				}
			}
			responseBgpConfigs.put(config.getRouterName(), config);
		}
		return responseBgpConfigs;
	}
	
	public static HashMap<String, BgpConfig[]> validConfigChanges(HashMap<String, BgpConfig> newBgpConfigs, WrappedHash<String, BgpConfig> currentBgpConfigs) {
		WrappedHash<String, BgpConfig[]> routersWithNewConfigs = new WrappedHash<String, BgpConfig[]>();
		//Create Router config arrays [0] contains current config, [1] is new config
		//if [0] is empty and [1] is not, then we have a new config
		//if [0] is not empty and [1] is empty, then we have to delete a config
		//if both are not empty then we need to do deeper inspection to detect changes
		
		for(String newConfig : newBgpConfigs.keySet()){
			if(currentBgpConfigs.containsKey(newConfig)){
				BgpConfig configsPair[] = new BgpConfig[2];
				configsPair[0] = currentBgpConfigs.get(newConfig);
				configsPair[1] = newBgpConfigs.get(newConfig);
				routersWithNewConfigs.put(newConfig,configsPair);
			}
		}

		for(String newConfig : newBgpConfigs.keySet()){
			if(!currentBgpConfigs.containsKey(newConfig)){
				BgpConfig configsPair[] = new BgpConfig[2];
				configsPair[0] = null;
				configsPair[1] = newBgpConfigs.get(newConfig);
				routersWithNewConfigs.put(newConfig,configsPair);
			}
		}
		
		for(String currentConfig : currentBgpConfigs.keySet()){
			if(!newBgpConfigs.containsKey(currentConfig)){
				BgpConfig configsPair[] = new BgpConfig[2];
				configsPair[0] = currentBgpConfigs.get(currentConfig);
				configsPair[1] = null;
				routersWithNewConfigs.put(currentConfig,configsPair);
			}
		}
		
		for(String key : routersWithNewConfigs.keySet()){
			if(routersWithNewConfigs.get(key)[0] != null && routersWithNewConfigs.get(key)[1] !=null){
				log.info("Checking for changes in router with name : " + key);
				//check addresses
				isConfigNew(routersWithNewConfigs.get(key));
			}else if(routersWithNewConfigs.get(key)[0] == null){
				log.info("New Config found : " + key );
				currentBgpConfigs.put(key, routersWithNewConfigs.get(key)[1]);
			}else if(routersWithNewConfigs.get(key)[1] == null){
				log.info("Removing Config : " + key);
				currentBgpConfigs.remove(key);
			}
		}
		return routersWithNewConfigs;
	}

	public static boolean isConfigNew(BgpConfig[] configPairs){
		for(int i=0; i< 2; i++){//switch between 1 and zero.
			for(String addressAndMask : configPairs[i].getAddressAndMasks().keySet()){
				//if(!configPairs[(i+1)%2].getAddressAndMasks().contains(addressAndMask)){
				if(!configPairs[i].getAddressAndMasks().get(addressAndMask).existsIn(configPairs[(i+1)%2].getAddressAndMasks())){
					return true;
				}
			}
			//Check links
			//TODO : The boolean here is redundant.
			for(Link link1 : configPairs[i].getLinks()){
				boolean link1Exists = false;
				for(Link link2 : configPairs[(i+1)%2].getLinks()){
					if(link1.getDestinationAddress().equals(link2.getDestinationAddress()) && link1.getSourceAddressName().equals(link2.getSourceAddressName())){
						link1Exists = true;
					}
				}
				if(link1Exists == false){
					return true;
				}
			}
		}
		return false;
	}
}

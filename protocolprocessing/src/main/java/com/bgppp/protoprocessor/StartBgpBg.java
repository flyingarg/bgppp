package com.bgppp.protoprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sun.security.provider.ConfigSpiFile;

public class StartBgpBg {

	//static List<String> routerNames = new ArrayList<String>();
	static HashMap<String,BgpConfig> routers = new HashMap<String,BgpConfig>();

	public static void main(String args[]) {
		try {
			Thread thread = new MainThread();
			// thread.setDaemon(true);
			thread.start();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static boolean routerExists(String routerName) {
		if (StartBgpBg.routers.keySet().contains(routerName))
			return true;
		else {
			return false;
		}
	}

	static boolean routerAdd(String routerName, BgpConfig config) {
		routers.put(routerName,config);
		return true;
	}
	
	static boolean routerRemove(String routerName){
		routers.remove(routerName);
		return true;
	}
	
	static boolean routerModify(String routerName, BgpConfig config){
		routerRemove(routerName);
		routerAdd(routerName, config);
		return true;
	}

	static class MainThread extends Thread {

		Long lastChanged = new Long("0");
		Date dateTime = new Date();
		static File file;

		@Override
		public void run() {
			List<BgpConfig> bgpConfigs = getBgpConfigUpdates();
			//Thread thread = Thread.
			// TODO : Checking status of threads.

		}

		public List<BgpConfig> getBgpConfigUpdates() {
			List<BgpConfig> configs = new ArrayList<BgpConfig>();
			
			while (true) {
				// Checking if file exists, if not creating a sample file.
				file = new File("config-bgp");
				if (!file.exists()) {
					try {
						file.createNewFile();
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(file));
						writer.write("#Sample File\n[Router1]\n\n\n\n--\n[Router2]\n\n\n\n--\n\n\nEOF");
						writer.flush();
						writer.close();
						lastChanged = dateTime.getTime();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// Checking file modification and saving configuration
				if (lastChanged < file.lastModified()) {
					System.out.println("File Changed.");
					lastChanged = file.lastModified();
					file.setWritable(false);
					try {
						BufferedReader reader = new BufferedReader(
								new FileReader(file));
						String readLine = "";
						while ((readLine = reader.readLine()) != null) {
							System.out.println("Reading -----------------"
									+ readLine);
							if (readLine.startsWith("#")
									|| readLine.trim().equals("")) {
								System.out.println("Skipping" + readLine);
								continue;// ignore
							}
							if (readLine.startsWith("[")) {
								// Router configuration started, read till you
								// encounter -- and save the string in config
								String config = readLine;
								reader.mark(0);
								while (!(readLine = reader.readLine())
										.equals("--")
										&& !readLine.equals("EOF")
										&& readLine != null) {
									reader.mark(0);
									System.out.println("Reading " + readLine);
									config += "^&"+readLine;
								}
								configs.add(populateBgpConfigs(config));
								config = "";
								reader.reset();
							}
						}
						file.setWritable(true);
						reader.close();
						//validConfigChanges();
					} catch (FileNotFoundException exception) {
						exception.printStackTrace();
					} catch (IOException exception) {
						exception.printStackTrace();
					}
				}
			}
		}

		// TODO Will validate what configurations are new and then start stop threads accordingly.
		public static HashMap<String, BgpConfig[]> validConfigChanges(List<BgpConfig> bgpConfigs) {
			//If the router is new add the router.
			HashMap<String, BgpConfig[]> testMaps = new HashMap<String, BgpConfig[]>();
			List<BgpConfig> responseConfigs = new ArrayList<>();

			
			for(BgpConfig newConfig : bgpConfigs){
				for(String oldConfigRouterName : routers.keySet()){
					if(newConfig.getRouterName().equals(oldConfigRouterName)){
						BgpConfig configsPair[] = new BgpConfig[2];
						configsPair[0] = routers.get(oldConfigRouterName);
						configsPair[1] = newConfig;
						testMaps.put(oldConfigRouterName,configsPair);
						break;
					}
				}
				//If the router was newly added it would not get included in the above logic.
				if(!testMaps.containsKey(newConfig.getRouterName())){
					BgpConfig configsPair[] = new BgpConfig[2];
					configsPair[1] = newConfig;
					configsPair[0] = null;
					testMaps.put(newConfig.getRouterName(), configsPair);
				}
			}
			//If the router was removed,
			for(String namesFromOldList : routers.keySet()){
				if(!testMaps.containsKey(namesFromOldList)){
					BgpConfig configsPair[] = new BgpConfig[2];
					configsPair[1] = null;
					configsPair[0] = routers.get(namesFromOldList);
					testMaps.put(namesFromOldList, configsPair);
				}
			}
			return testMaps;
		}

		private BgpConfig populateBgpConfigs(String configAsString) {
			System.out.println("Configuration - " + configAsString);
			BgpConfig config = new BgpConfig();
			String[] parameters = configAsString.split("^&");
			for(int i=0; i<parameters.length ;i++){
				//Name
				if(parameters[i].trim().startsWith("[")){
					String routerName = parameters[i].substring(1, parameters[i].length()-1);
					config.setRouterName(routerName);
				}else if(parameters[i].trim().matches("[0-9]*.[0-9]*.[0-9]*.[0-9]*[-][0-9]*.[0-9]*.[0-9]*.[0-9]*")){//Links
					try{
					config.addLink(InetAddress.getByName(parameters[i].split("-")[0]), InetAddress.getByName(parameters[i].split("-")[1]));
					}catch(Exception exception){
						System.out.println(exception.getStackTrace());
					}
				}else if(parameters[i].trim().matches("[0-9]*.[0-9]*.[0-9]*.[0-9]*")){//Ip Address
					try{
						config.addAddress(InetAddress.getByName(parameters[i].trim()));
					}catch(Exception exception){
						System.out.println(exception.getStackTrace());	
					}
				}
			}
			return config;
		}
	}
	
	/*

			//If a router is missing
			for(int i=0; i<routers.size(); i++){
				if(!bgpConfigs.){
					responseConfigs.add(bgpConfigs.get(i));
				}
			}
			
			
			//If a router's config has changed, close the connection.
			for(BgpConfig config : bgpConfigs){
				if(routers.containsKey(config.getRouterName())){
					//Check addresses
					for(InetAddress address : routers.get(config.getRouterName()).getAddress()){
						if(config.getAddress().contains(address)){
							continue;
						}
					}
					//Check links
					for(Link link : config.getLinks()){
						if(link.getDestinationAddress().equals(o)){
							
						}
					}
				}else{
					routerAdd(config.getRouterName(), config);
				}
			}
	 */
}

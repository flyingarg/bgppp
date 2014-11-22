package com.bgppp.protoprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.*;

import com.bgppp.protoprocessor.monitor.MonitorProducerConsumer;
import com.bgppp.protoprocessor.utils.ConfigHelper;

public class StartBgpBg {

	public static Logger log = Logger.getLogger(StartBgpBg.class.getName());
	static WrappedHash<String,BgpConfig> routers = new WrappedHash<String,BgpConfig>();

	public static void main(String args[]) {
		try {
			Thread thread = new MainThread();
			BasicConfigurator.configure();
			// thread.setDaemon(true); Behaves really weird with daemon on. 
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
		static File file ;

		@Override
		public void run() {
			getBgpConfigUpdates();
		}

		public List<BgpConfig> getBgpConfigUpdates() {
			// Checking if file exists, if not creating a sample file.
			if(file == null || file.exists() == false)
				file = new File("config-bgp");
			if (!file.exists()) {
				try {
					file.createNewFile();
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					writer.write("#Sample File\n[Router1]\n\n\n\n--\n[Router2]\n\n\n\n--\n\n\nEOF");
					writer.flush();
					writer.close();
					lastChanged = dateTime.getTime();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			MonitorProducerConsumer monitor = new MonitorProducerConsumer();
			monitor.start();
			while (true) {
				// Checking file modification and saving configuration
				if (lastChanged < file.lastModified() && file.canWrite()) {
					ArrayList<String> configs = new ArrayList<String>();
					lastChanged = file.lastModified();
					file.setWritable(false);
					try {
						BufferedReader reader = new BufferedReader(new FileReader(file));
						String readLine = "";
						while ((readLine = reader.readLine()) != null) {
							if (readLine.startsWith("#") || readLine.trim().equals("")) {
								continue;// ignore
							}
							if (readLine.startsWith("[")) {
								// Router configuration started, read till you
								// encounter -- and save the string in config
								String config = readLine;
								reader.mark(0);
								while (!(readLine = reader.readLine()).equals("--")	&& !readLine.equals("EOF") && readLine != null) {
									reader.mark(0);
									config += "^&"+readLine;
								}
								configs.add(config);
								config = "";
								reader.reset();
							}
						}
						file.setWritable(true);
						reader.close();
					} catch (FileNotFoundException exception) {
						exception.printStackTrace();
					} catch (IOException exception) {
						exception.printStackTrace();
					}
					//Convert String configs to objects and then Validate these objects to find config changes
					ConfigHelper.validConfigChanges(ConfigHelper.stringToBgpConfig(configs), routers);
					configs.clear();
					//TODO : Create a thread that monitors the producers and consumers and gets data out of them
				}
			}
		}
	}
}

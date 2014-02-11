package com.bgppp.protoprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StartBgpBg {

	static List<String> routerNames  = new ArrayList<String>();
	
	public static void main(String args[]){
		try{
			Thread thread = new MainThread();
			//thread.setDaemon(true);
			thread.start();
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	
	static boolean routerExists(String routerName){
		if(StartBgpBg.routerNames.contains(routerName))
			return true;
		else{
			return false;
		}
	}
	
	static boolean routerAdd(String routerName){
		routerNames.add(routerName);
		return true;
	}
	
	static class MainThread extends Thread{

		Long lastChanged = new Long("0");
		Date dateTime = new Date();
		static File file;
		
		@Override
		public void run() {
			while(true){
				//Checking if file exists, if not creating a new file.
				file = new File("config-bgp");
				if(!file.exists()){
					try {
						file.createNewFile();
						lastChanged = dateTime.getTime();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				//Checking file modification.
				if(lastChanged < file.lastModified()){
					System.out.println("File Changed.");
					lastChanged = file.lastModified();
					file.setWritable(false);
					try{
						BufferedReader reader = new BufferedReader(new FileReader(file));
						String tmp;
						while( (tmp = reader.readLine()) != null){
							if(tmp.startsWith("[")){
								String tmpRouterName = tmp.substring(tmp.indexOf("[")+1, tmp.indexOf("]"));
								System.out.println("Checking Router : " + tmpRouterName);
								if(!routerExists(tmpRouterName)){
									System.out.println("Router " + tmpRouterName + " does not exist. Creating new Instance");
									Thread thread = new Thread(new BgpThread(), tmpRouterName);
									thread.start();
									routerAdd(tmpRouterName);
								}
							}
						}
						file.setWritable(true);
						reader.close();
					}catch(FileNotFoundException exception){
						exception.printStackTrace();
					}catch(IOException exception){
						exception.printStackTrace();
					}
				}
				
				//TODO : Checking status of threads.
				
			}
			
		}
		
	}
}

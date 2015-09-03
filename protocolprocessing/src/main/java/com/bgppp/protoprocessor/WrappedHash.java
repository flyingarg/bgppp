package com.bgppp.protoprocessor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.*;

import com.bgppp.protoprocessor.utils.AddressAndMask;

/**
 * This class is a wrapper over the HashMap class.
 * Allows printing of router configuration as soon as there are changes in the router list.
 * put and remove overridden.
 * @author rajumoh
 *
 * @param <K>
 * @param <V>
 */
@SuppressWarnings("serial")
public class WrappedHash<K, V> extends HashMap<K, V>{
	public static Logger log = Logger.getLogger(WrappedHash.class.getName());
	@Override
	public V put(K key, V value) {
		/*
		 * Router Init steps
		 * 1. Check and create missing virtual interfaces.
		 * 2. Use the BGP configuration and assign these interfaces to the java InetAddress. this will reserve these resources.
		 * 3. Start the discovery process.
		 * 4. All this must be part of one process and this process must be tracked 
		*/
		if(value instanceof BgpConfig){
			BgpConfig bgpConfig = (BgpConfig) value;
			HashMap<String, AddressAndMask> addresses = bgpConfig.getAddressAndMasks();
			//Creating interfaces
			for(String addressName : addresses.keySet()){
				addIfconfig(bgpConfig, addresses.get(addressName));
			}
			//Start discovery process
			bgpConfig.execute();
		}
		return super.put(key, value);
	}
	@Override
	public V remove(Object key) {
		log.info("Remove:Key->"+key.toString()+"Value"+super.get(key).toString());
		/*
		 * Get the name of the process associated with this key and stop/kill the process.
		*/
		Object value = super.get(key).toString();
		if(value instanceof BgpConfig){
			BgpConfig bgpConfig = (BgpConfig) value;
			HashMap<String, AddressAndMask> addresses = bgpConfig.getAddressAndMasks();
			//Creating interfaces
			for(String addressName : addresses.keySet()){
				removeIfconfig(bgpConfig, addresses.get(addressName));
			}
			bgpConfig.destroy();
			//Start discovery process
		}
		return super.remove(key);
	}

	public static synchronized void addIfconfig(BgpConfig bgpConfig, AddressAndMask address){
		String exitStatus = "";
		String ifName = bgpConfig.getRouterName() + address.getName();
		log.info("ifconfig " + "eth0:" + ifName + " " + address.getAddress().toString().replaceFirst("/","") + " netmask "+ address.getMask());
		ProcessBuilder processBuilder = new ProcessBuilder("ifconfig", "eth0:"+ifName, address.getAddress().toString().replaceFirst("/","") ,"netmask" ,address.getMask());
		Process process;
		BufferedReader br = null;
		try{
			process = processBuilder.start();
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String output = "";
			if((output=br.readLine())!=null){
				if(output.toLowerCase().contains("usage"))
					log.error("Could not create network interface");
				return;
			}
			process.waitFor();
			exitStatus = ""+process.exitValue();
			br.close();
		}catch(IOException exception){
			if(br!=null)
				try{
					br.close();
				}catch(IOException e){
					log.error("Error creating interface " + ifName + "/" + address.toString().substring(1) + ", Exit status: " + exitStatus);
				}
			log.error("Error creating interface " + ifName + "/" + address.toString().substring(1) + ", Exit status: " + exitStatus);
		}catch(InterruptedException e){
			log.error("Error creating interface " + ifName + "/" + address.toString().substring(1) + ", Exit status: " + exitStatus);
		}
	}

	public static synchronized void removeIfconfig(BgpConfig bgpConfig, AddressAndMask address){
		String exitStatus = "";
		String ifName = bgpConfig.getRouterName() + address.getName();
		log.info("ifconfig " + "eth0:" + ifName + " down");
		ProcessBuilder processBuilder = new ProcessBuilder("ifconfig", "eth0:"+ifName, "down");
		Process process;
		BufferedReader br = null;
		try{
			process = processBuilder.start();
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String output = "";
			if((output=br.readLine())!=null){
				if(output.toLowerCase().contains("usage"))
					log.error("Could not create network interface");
				return;
			}
			process.waitFor();
			exitStatus = ""+process.exitValue();
			br.close();
		}catch(IOException exception){
			if(br!=null)
				try{
					br.close();
				}catch(IOException e){
					log.error("Error deleting interface " + ifName + "/" + address.toString().substring(1) + ", Exit status: " + exitStatus);
				}
			log.error("Error deleting interface " + ifName + "/" + address.toString().substring(1) + ", Exit status: " + exitStatus);
		}catch(InterruptedException exception){
			log.error("Error deleting interface " + ifName+ " , Exit status: " + exitStatus);
		}
	}
}

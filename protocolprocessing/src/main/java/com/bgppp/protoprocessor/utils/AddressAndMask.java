package com.bgppp.protoprocessor.utils;

import java.net.InetAddress;
import java.util.HashMap;

import org.apache.log4j.*;

public class AddressAndMask {
	public static Logger log = Logger.getLogger(AddressAndMask.class.getName());

	private InetAddress address;
	private String mask;
	private String name;
	private String routerName;
	private String accessNetwork;
	/**
	 * Constructor
	 * 
	 * @param name
	 * @param address
	 * @param mask
	 */
	public AddressAndMask(String name, String address, String mask) {
		this.name = name;
		try {
			this.address = InetAddress.getByName(address);
		} catch (Exception exception) {
			log.error("Problem adding address in constructor.");
		}
		this.mask = mask;
	}

	// ~~ Getters and Setters/
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getRouterName() {
		return routerName;
	}
	public void setRouterName(String routerName) {
		this.routerName = routerName;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	/**
	 * @This will return a string in the form "*.*.*.*"
	 * @return
	 */
	public String getMask() {
		return mask;
	}

	/**
	 * Set this as "*.*.*.*"
	 * 
	 * @param mask
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}

	// ~~ Helper Classes
	/**
	 * Helper class to set address and mask from the string of form
	 * *.*.*.*\/*.*.*.*
	 * 
	 * @param String
	 *            addressAndMask
	 */
	public void setAddressAndMask(String addressAndMask) {
		String address = addressAndMask.split("\\/")[0];
		this.mask = addressAndMask.split("\\/")[1];
		try {
			this.address = InetAddress.getByName(address);
		} catch (Exception exception) {
			log.error("Problem assigning address from AddressAndMask");
		}
	}

	/**
	 * Returns true if ip and mask match, else returns false
	 * 
	 * @param addressAndMask
	 */
	public boolean compare(AddressAndMask addressAndMask) {
		if (addressAndMask.getAddress().equals(this.address)
				&& addressAndMask.getMask().equals(this.mask)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return this.address.toString() + this.mask;
	}

	/**
	 * Checks if this address exists in the list passed
	 * 
	 * @param addressAndMasks
	 * @return
	 */
	public boolean existsIn(HashMap<String, AddressAndMask> addressAndMasks) {
		for (String nameAddressAndMask : addressAndMasks.keySet()) {
			if (addressAndMasks.get(nameAddressAndMask).compare(this)) {
				return true;
			}
		}
		return false;
	}

	public String getAccessNetwork() {
		return accessNetwork;
	}

	public void setAccessNetwork(String accessNetwork) {
		this.accessNetwork = accessNetwork;	
	}

}

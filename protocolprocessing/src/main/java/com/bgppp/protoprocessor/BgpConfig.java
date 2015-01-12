package com.bgppp.protoprocessor;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.*;
import com.bgppp.protoprocessor.graphs.GraphNode;
import com.bgppp.protoprocessor.utils.AddressAndMask;

public class BgpConfig extends GraphNode{

	public static Logger log = Logger.getLogger(BgpConfig.class.getName());
	public BgpConfig(String name) {
		super(name);
		this.routerName = name;
	}

	// Basic router identifiers.
	private String routerName;

	// Array of InetAddresses available.
	private HashMap<String, AddressAndMask> addressAndMasks = new HashMap<String, AddressAndMask>();

	// Connection details - List of one-to-one outgoing connections.
	private List<Link> links = new ArrayList<Link>();

	public String getRouterName() {
		return routerName;
	}

	public void setRouterName(String routerName) {
		super.setNodeName(routerName);
		this.routerName = routerName;
	}

	public HashMap<String, AddressAndMask> getAddressAndMasks() {
		return addressAndMasks;
	}

	public void setAddressAndMasks(HashMap<String, AddressAndMask> addressAndMask) {
		this.addressAndMasks = addressAndMask;
	}

	public boolean addAddress(AddressAndMask newAddressAndMask) {
/*		if (this.addressAndMasks != null && this.addressAndMasks.size() != 0) {
			for (AddressAndMask address : this.addressAndMasks) {
				if (address.equals(newAddressAndMask)) {
					log.info("Address not added, Address "
							+ newAddressAndMask.toString() + " already exist.");
					return false;
				}
			}

		}*/
		addressAndMasks.put(newAddressAndMask.getName(), newAddressAndMask);
		return true;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public boolean addLink(String localAddressName, InetAddress remoteAddress) {
		if (addressAndMasks.get(localAddressName) == null) {
			log.info("Link not created, Local Address " + localAddressName + " Does not exist.");
			return false;
		} else {
			for (Link link : this.links) {
				if (link.getSourceAddressName().equals(localAddressName)) {
					if (link.getDestinationAddress().equals(remoteAddress)) {
						log.info("Link not created, Local Address and Remote address pair "
										+ localAddressName
										+ "-"
										+ remoteAddress.toString()
										+ " already exists.");
						return false;
					}
				}
			}

		}
		InetAddress source = getAddressAndMaskByName(localAddressName).getAddress();
		Link link = new Link(super.getNodeName()+"-"+localAddressName+"-"+remoteAddress.toString().substring(1)
				,""+(links.size()+1)
				,source
				,remoteAddress);
		link.setSourceAddressName(localAddressName);
		this.links.add(link);
		log.info("Added Link" + link);
		return true;
	}
	
	public AddressAndMask getAddressAndMaskByName(String name){
		return addressAndMasks.get(name);
	}
	
	@Override
	public String toString(){
		String response = "";
		response += "routerName:"+this.routerName;
		response += ",address:"+this.addressAndMasks;
		response += ",link:"+this.links;
		return response;
	}
}

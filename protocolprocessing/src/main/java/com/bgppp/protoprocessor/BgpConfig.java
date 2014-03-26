package com.bgppp.protoprocessor;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class BgpConfig {

	// Basic router identifiers.
	private String routerName;

	// Array of InetAddresses available.
	private List<InetAddress> address = new ArrayList<InetAddress>();

	// Connection details - List of one-to-one outgoing connections.
	private List<Link> links = new ArrayList<Link>();

	public String getRouterName() {
		return routerName;
	}

	public void setRouterName(String routerName) {
		this.routerName = routerName;
	}

	public List<InetAddress> getAddress() {
		return address;
	}

	public void setAddress(List<InetAddress> address) {
		this.address = address;
	}

	public boolean addAddress(InetAddress inetAddress) {
		if (this.address != null && this.address.size() != 0) {
			for (InetAddress address : this.address) {
				if (address.equals(inetAddress)) {
					System.out.println("Address not added, Address "
							+ inetAddress.toString() + " already exist.");
					return false;
				}
			}

		}
		address.add(inetAddress);
		return true;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public boolean addLink(InetAddress localAddress, InetAddress remoteAddress) {
		if (!address.contains(localAddress)) {
			System.out.println("Link not created, Local Address " + localAddress.toString() + " Does not exist.");
			return false;
		} else {
			for (Link link : this.links) {
				if (link.getSourceAddress().equals(localAddress)) {
					if (link.getDestinationAddress().equals(remoteAddress)) {
						System.out.println("Link not created, Local Address and Remote address pair "
										+ localAddress.toString()
										+ "-"
										+ remoteAddress.toString()
										+ " already exists.");
						return false;
					}
				}
			}

		}
		Link link = new Link();
		link.setSourceAddress(localAddress);
		link.setDestinationAddress(remoteAddress);
		this.links.add(link);
		return true;
	}
	
	@Override
	public String toString(){
		String response = "";
		response += "routerName:"+this.routerName;
		response += ",address:"+this.address;
		response += ",link:"+this.links;
		return response;
	}
}

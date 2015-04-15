package com.bgppp.protoprocessor;

import java.net.InetAddress;

import com.bgppp.protoprocessor.graphs.*;

public class Link extends GraphPath{
	public Link(String pathName, String pathId, InetAddress source, InetAddress destination, String routerName) {
		super(pathName, pathId, routerName, "", "#F00");
		this.sourceAddress = source;
		this.destinationAddress = destination;
		//this.destinationAddressName =  destination.toString().substring(1);
		this.sourceAddressName = source.toString().replace("\\/","");
	}
	private InetAddress sourceAddress;
	private String sourceAddressName;//self, but we need to determine which of the available interfaces is doing that.
	private InetAddress destinationAddress;//address of the peer bgp router.
	//private String destinationRouterName;//name of the bgp peer. on a second thought, it shoud not know remote bgp's name as the remote ip is the bgp identifier.
	//private Integer nativePort;//port being used to connect to the destination. destination port is always 179
	private boolean isAlive;
	public String getSourceAddressName() {
		return sourceAddressName;
	}
	public void setSourceAddressName(String sourceAddressName) {
		this.sourceAddressName = sourceAddressName;
	}
	public InetAddress getDestinationAddress() {
		return destinationAddress;
	}
	public void setDestinationAddress(InetAddress destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	/*public String getDestinationRouterName() {
		return destinationRouterName;
	}
	public void setDestinationRouterName(String destinationRouterName) {
		this.destinationRouterName = destinationRouterName;
	}
	public Integer getNativePort() {
		return nativePort;
	}
	public void setNativePort(Integer nativePort) {
		this.nativePort = nativePort;
	}*/
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public InetAddress getSourceAddress() {
		return sourceAddress;
	}
	public void setSourceAddress(InetAddress sourceAddress) {
		this.sourceAddress = sourceAddress;
	}
	@Override
	public String toString() {
		String response="";
		response += "sourceAddress:"+sourceAddress;
		response += ",sourceAddressName:"+sourceAddressName;
		response += ",destinationAddress:"+destinationAddress;
		return response;
	}
}

package com.bgppp.protoprocessor;

import java.net.InetAddress;

public class Link {

	private InetAddress sourceAddress;//self, but we need to determine which of the available interfaces is doing that.
	private InetAddress destinationAddress;//address of the peer bgp router.
	private String destinationRouterName;//name of the bgp peer.
	private Integer nativePort;//port being used to connect to the destination. destination port is always 179
	
	public InetAddress getSourceAddress() {
		return sourceAddress;
	}
	public void setSourceAddress(InetAddress sourceAddress) {
		this.sourceAddress = sourceAddress;
	}
	public InetAddress getDestinationAddress() {
		return destinationAddress;
	}
	public void setDestinationAddress(InetAddress destinationAddress) {
		this.destinationAddress = destinationAddress;
	}
	public String getDestinationRouterName() {
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
	}
}

package com.bgppp.protoprocessor.graphs;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {

	private String nodeName;
	private String id;
	private String size;
	private String color;
	private List<GraphNode> network = new ArrayList<GraphNode>();
	private List<GraphPath> networkPath = new ArrayList<GraphPath>();
	
	public GraphNode(String name, String size, String color){
		this.nodeName = name;
		this.id = name;
		this.size = size;
		this.color = color;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	
	
	public List<GraphPath> getNetworkPath() {
		return this.networkPath;
	}
	public void setNetworkPath(List<GraphPath> paths) {
		this.networkPath = paths;
	}
	public void addPath(GraphPath path){
		this.networkPath.add(path);
	}
	public void removePath(GraphPath path){
		this.networkPath.add(path);
	}
	
	public List<GraphNode> getNetwork() {
		return this.network;
	}
	public void setNetwork(List<GraphNode> network) {
		this.network = network;
	}
	public void addNetwork(GraphNode network){
		this.network.add(network);
	}
	public void removeNetwork(GraphNode network){
		this.network.add(network);
	}

	@Override
	public String toString(){
		return nodeName;
	}
}

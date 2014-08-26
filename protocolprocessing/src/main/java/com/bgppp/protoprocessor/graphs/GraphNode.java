package com.bgppp.protoprocessor.graphs;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {

	private String nodeName;
	private List<GraphPath> paths = new ArrayList<GraphPath>();
	public GraphNode(String name){
		this.nodeName = name;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public List<GraphPath> getPaths() {
		return paths;
	}
	public void setPaths(List<GraphPath> paths) {
		this.paths = paths;
	}
	
	
	//Helpers
	public void addPath(GraphPath path){
		paths.add(path);
	}
	public void removePath(GraphPath path){
		paths.add(path);
	}
	
	@Override
	public String toString(){
		return nodeName;
	}
}

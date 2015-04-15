package com.bgppp.protoprocessor.graphs;

import java.util.Stack;

public class GraphPath {

	private GraphNode node;
	private Long weight;

	private String pathName = "";
	private String pathId = "";
	private String destinationId = "";
	private String sourceId = "";
	private String color = "";

	public GraphPath(String pathName, String pathId, String source, String destination, String color){
		this.pathName = pathName;
		this.pathId = pathId;
		this.sourceId = source;
		this.destinationId = destination;
		this.color = color;
	}
	public GraphNode getNode() {
		return node;
	}
	public void setNode(GraphNode node) {
		this.node = node;
	}
	public Long getWeight() {
		return weight;
	}
	public void setWeight(Long weight) {
		this.weight = weight;
	}
	public String getPathName() {
		return pathName;
	}
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	public String getPathId() {
		return pathId;
	}
	public void setPathId(String pathId) {
		this.pathId = pathId;
	}
	public String getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public boolean isPresentInStack(Stack<GraphNode> stack){
		for(GraphNode graphNode : stack){
			if(graphNode.getNodeName().equals(node.getNodeName()))
				return true;
		}
		return false;
	}
}

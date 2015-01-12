package com.bgppp.protoprocessor.graphs;

import java.util.Stack;

public class GraphPath {

	private GraphNode node;
	private Long weight;

	private String id = "";
	private String pathName = "";
	private String pathId = "";
	private String destinationId = null;
	private String sourceId = null;


	public GraphPath(String pathName, String pathId, String source, String destination){
		//this.node = node;
		//this.weight = weight;
		this.pathName = pathName;
		this.pathId = pathId;
		this.sourceId = source;
		this.destinationId = destination;
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	/*@Override
	public String toString(){
		return node.getNodeName();
	}*/
	public boolean isPresentInStack(Stack<GraphNode> stack){
		for(GraphNode graphNode : stack){
			if(graphNode.getNodeName().equals(node.getNodeName()))
				return true;
		}
		return false;
	}
}

package com.bgppp.protoprocessor.graphs;

import java.util.Stack;

public class GraphPath {

	GraphNode node;
	Long weight;
	public GraphPath(GraphNode node, Long weight){
		this.node = node;
		this.weight = weight;
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
	@Override
	public String toString(){
		return node.getNodeName();
	}
	public boolean isPresentInStack(Stack<GraphNode> stack){
		for(GraphNode graphNode : stack){
			if(graphNode.getNodeName().equals(node.getNodeName()))
				return true;
		}
		return false;
	}
}

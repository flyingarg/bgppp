package com.bgppp.protoprocessor.graphs;

@SuppressWarnings("serial")
public class NoNodeException extends Exception {
	GraphNode node = null;
	public NoNodeException(GraphNode node){
		this.node = node;
	}
	@Override
	 public String getMessage() {
        return "There is no node in odeStore "+ node;
    }
}

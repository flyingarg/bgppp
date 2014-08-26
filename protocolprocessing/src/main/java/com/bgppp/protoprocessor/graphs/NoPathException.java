package com.bgppp.protoprocessor.graphs;

public class NoPathException extends Exception {
	GraphNode from = null;
	GraphNode to = null;
	public NoPathException(GraphNode from, GraphNode to){
		this.from = from;
		this.to = to;
	}
	@Override
	public String getMessage(){
		return "There is no path from "+from.getNodeName()+" to "+to.getNodeName();
	}
}

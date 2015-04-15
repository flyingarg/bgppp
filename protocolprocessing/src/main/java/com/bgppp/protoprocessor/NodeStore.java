package com.bgppp.protoprocessor;

import java.util.HashMap;

import com.bgppp.protoprocessor.graphs.*;

import org.apache.log4j.*;

public class NodeStore {

	public static Logger log = Logger.getLogger(NodeStore.class.getName());
	
	private static HashMap<String,GraphNode> hashStore = new HashMap<String, GraphNode>();
	private static HashMap<String, GraphPath> pathStore = new HashMap<String, GraphPath>();
	public static HashMap<String, GraphNode> getHashStore() {
		return hashStore;
	}
	public static void addNode(GraphNode node){
		NodeStore.hashStore.put(node.getNodeName(), node);
	}
	public static void removeNode(GraphNode node){
		NodeStore.hashStore.remove(node.getNodeName());
	}
	public static GraphNode getNodeByName(String name){
		return NodeStore.hashStore.get(name);
	}

	public static HashMap<String, GraphPath> getPathStore() {
		return pathStore;
	}
	public static void addPath(GraphPath path){
		NodeStore.pathStore.put(path.getPathName(), path);
	}
	public static void removePath(GraphPath path){
		NodeStore.pathStore.remove(path.getPathName());
	}
	public static GraphPath getPathByName(String name){
		return NodeStore.pathStore.get(name);
	}


	public Long getDistance(GraphNode from, GraphNode to) throws NoPathException, NoNodeException{
		Long distance = new Long("0");
		boolean pathfound = false;
		if(getNodeByName(from.getNodeName())==null)
			throw new NoNodeException(from);
		if(getNodeByName(to.getNodeName()) == null)
			throw new NoNodeException(to);
		for(GraphPath path : from.getNetworkPath()){
			if(path.getNode().getNodeName().equals(to.getNodeName())){
				distance+=path.getWeight();
				pathfound=true;
			}
		}
		if(pathfound == false)
			throw new NoPathException(from,to);
		return distance;
	}
	public Long getWeightOfPath(String path){
		Long response = new Long("0");
		String nodes[] = path.split("==>");
		for(int i=0; i<nodes.length-1; i++){
			try{
				response+=this.getDistance(NodeStore.getNodeByName(nodes[i]), NodeStore.getNodeByName(nodes[i+1]));
			}catch(NoPathException exception){
				log.error(exception.getMessage());
			}catch(NoNodeException exception){
				log.error(exception.getMessage());
			}
		}
		return response;
	}

}

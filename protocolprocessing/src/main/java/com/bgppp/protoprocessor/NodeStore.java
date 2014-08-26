package com.bgppp.protoprocessor;

import java.util.HashMap;

import com.bgppp.protoprocessor.graphs.*;

public class NodeStore {

	HashMap<String,GraphNode> hashStore = new HashMap<String, GraphNode>();
	public HashMap<String, GraphNode> getHashStore() {
		return hashStore;
	}
	public void setHashStore(HashMap<String, GraphNode> hashStore) {
		this.hashStore = hashStore;
	}
	public void addNode(GraphNode node){
		hashStore.put(node.getNodeName(), node);
	}
	public void removeNode(GraphNode node){
		hashStore.remove(node.getNodeName());
	}
	public GraphNode getNodeByName(String name){
		return hashStore.get(name);
	}
	public Long getDistance(GraphNode from, GraphNode to) throws NoPathException, NoNodeException{
		Long distance = new Long("0");
		boolean pathfound = false;
		if(getNodeByName(from.getNodeName())==null)
			throw new NoNodeException(from);
		if(getNodeByName(to.getNodeName()) == null)
			throw new NoNodeException(to);
		for(GraphPath path : from.getPaths()){
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
			System.out.println(i);
			try{
				response+=this.getDistance(this.getNodeByName(nodes[i]), this.getNodeByName(nodes[i+1]));
			}catch(NoPathException exception){
				System.out.println(exception.getMessage());
			}catch(NoNodeException exception){
				System.out.println(exception.getMessage());
			}
		}
		return response;
	}

}

package com.bgppp.protoprocessor.graphs;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.log4j.*;

public class GraphUtils {
	/**
	 * Shity Code to find all the possible paths from currentNode to destination.
	 * Go deeper if branch is 
	 		not destination(not at the destination yet), 
			not parent(so that i do not go back)
			not current(so that i am not stuck)
	 * @param parent
	 * @param currentNode
	 * @param destination
	 * @param pathAdder
	 */
	private ArrayList<GraphNode> paths = new ArrayList<GraphNode>();
	private List<String> listOfPaths = new ArrayList<String>();
	public static Logger log = Logger.getLogger(GraphUtils.class.getName());	
	public List<String> getPaths(){
		return listOfPaths;
	}
	

	/**
	 *Does DFS(Depth First Search) on a graph. the graph is stored in NodeStore. 
	 * @param nodesInPath : Nodes traversed are stored in stack variable
	 * @param currentNode : Each node contains path information, so as long as one node is provided, paths can be traversed.
	 */
/*	public void tracePath2(Stack<GraphNode> nodesInPath, GraphNode currentNode){
		log.info(nodesInPath.hashCode()+"NodesInPath=" + nodesInPath + "; currentNode=" + currentNode + ";Paths=" + currentNode.getPaths());
		for(GraphPath branch : currentNode.getPaths()){
			log.info("From " + currentNode + " to => " + branch.getNode() );
			if(branch.equals(currentNode)){
				log.info("Current");
			}else if(branch.isPresentInStack(nodesInPath)){
				log.info("Loop");
				printNodesInStack(nodesInPath);
			}else{
				//Cloning of object is required as each new branch of search must have its own record of paths traveled.
				Stack<GraphNode> newNodesInPath = (Stack<GraphNode>)nodesInPath.clone();
				newNodesInPath.push(branch.getNode());
				tracePath2(newNodesInPath, branch.getNode());
			}
		}
	}
*/
	
	public void tracePath2(Stack<GraphNode> nodesInPath, GraphNode currentNode){
		log.info(nodesInPath.hashCode()+"NodesInPath=" + nodesInPath + "; currentNode=" + currentNode + ";Paths=" + currentNode.getPaths());
		for(GraphPath branch : currentNode.getPaths()){
			log.info("From " + currentNode + " to => " + branch.getNode() );
			if(branch.equals(currentNode)){
				log.info("Current");
			}else if(branch.isPresentInStack(nodesInPath)){
				log.info("Loop");
				printNodesInStack(nodesInPath);
			}else{
				//Cloning of object is required as each new branch of search must have its own record of paths traveled.
				Stack<GraphNode> newNodesInPath = (Stack<GraphNode>)nodesInPath.clone();
				newNodesInPath.push(branch.getNode());
				tracePath2(newNodesInPath, branch.getNode());
			}
		}
	}

	private void printNodesInStack(Stack<GraphNode> nodesInPath) {
		String response = "";
		for(GraphNode node : nodesInPath){
			response += "==>" + node.getNodeName();
		}
		listOfPaths.add(response.substring(3));
		//log.info(response);
	}
	
}

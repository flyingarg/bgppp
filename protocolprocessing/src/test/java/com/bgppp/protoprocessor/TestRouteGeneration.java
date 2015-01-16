package com.bgppp.protoprocessor;

import java.util.List;
import java.util.Stack;

import org.junit.Test;
import org.junit.Ignore;

//import com.bgppp.protoprocessor.graphs.BgpConfig;
import com.bgppp.protoprocessor.graphs.GraphNode;
//import com.bgppp.protoprocessor.graphs.Link;
import com.bgppp.protoprocessor.graphs.GraphUtils;

public class TestRouteGeneration {
	BgpConfig node = new BgpConfig("node");
	BgpConfig node1 = new BgpConfig("node1");
	BgpConfig node2 = new BgpConfig("node2");
	BgpConfig node3 = new BgpConfig("node3");
	BgpConfig node4 = new BgpConfig("node4");
	BgpConfig node5 = new BgpConfig("node5");
	BgpConfig node6 = new BgpConfig("node6");
	BgpConfig node7 = new BgpConfig("node7");
	BgpConfig node8 = new BgpConfig("node8");
	BgpConfig node9 = new BgpConfig("node9");
	NodeStore nodeStore = new NodeStore();
	
	/*
	protected void setUp() throws Exception {
		node.addPath(new Link(node1,new Long("1")));
		node.addPath(new Link(node2,new Long("2")));
		node.addPath(new Link(node3,new Long("3")));
		node.addPath(new Link(node4,new Long("4")));
		
		node1.addPath(new Link(node,new Long("1")));
		node2.addPath(new Link(node,new Long("2")));
		node3.addPath(new Link(node,new Long("3")));
		
		//node4.addPath(new Link(node,new Long("4")));
		node4.addPath(new Link(node5,new Long("45")));
		node4.addPath(new Link(node6,new Long("46")));
		
		node5.addPath(new Link(node4,new Long("54")));
		node5.addPath(new Link(node6,new Long("56")));
		
		node6.addPath(new Link(node4,new Long("64")));
		
		node6.addPath(new Link(node7,new Long("64")));
		
		node7.addPath(new Link(node8,new Long("64")));
		
		node8.addPath(new Link(node9,new Long("64")));
		
		node9.addPath(new Link(node6,new Long("64")));
		
		nodeStore.addNode(node);nodeStore.addNode(node1);nodeStore.addNode(node2);nodeStore.addNode(node3);
		nodeStore.addNode(node4);nodeStore.addNode(node5);nodeStore.addNode(node6);nodeStore.addNode(node7);
		nodeStore.addNode(node8);nodeStore.addNode(node9);
	}
	@Override
	protected void tearDown() throws Exception {
	
	}
*/
	@Test
	@Ignore
	public void testTraveling(){
		Stack<GraphNode> nodes = new Stack<GraphNode>();
		nodes.push(node8);
		GraphUtils graphUtils = new GraphUtils();
		graphUtils.tracePath2(nodes,node8);
		List<String> response = graphUtils.getPaths();
		for(String paths : response){
			System.out.println(paths);
		}
		//assertTrue(true);
	}
	
	/*public void testGetWeightOfPath(){
		System.out.println(nodeStore.getWeightOfPath("node8==>node9==>node6==>node4==>node5"));
		assertTrue(true);
	}*/
}

package com.bgppp.protoprocessor.web;

import java.util.ArrayList;
import java.util.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.json.JSONArray;

import com.bgppp.protoprocessor.BgpConfig;
import com.bgppp.protoprocessor.BgpConsumer;
import com.bgppp.protoprocessor.FSMState;
import com.bgppp.protoprocessor.ProducerConsumerStore;
import com.bgppp.protoprocessor.Link;
import com.bgppp.protoprocessor.graphs.*;
import com.bgppp.protoprocessor.rules.Rule;

import org.apache.log4j.*;

@Path("/bgppp")
public class Stats{

	public static final Logger log = Logger.getLogger(Stats.class);
	@GET
	@Path("/stats/{routerName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String stats(@PathParam("routerName") String routerName){
		try{
			JSONObject jsonobject;
			JSONArray nodeArray = new JSONArray();
			JSONArray verticesArray = new JSONArray();
			for(String key : ProducerConsumerStore.getHashStore().keySet()){
				GraphNode node = ProducerConsumerStore.getHashStore().get(key);
				if(node.getNodeName().contains(routerName) || routerName.equals("all")){
					jsonobject = new JSONObject();
					jsonobject.put("id",node.getId());
					jsonobject.put("name",node.getNodeName());
					jsonobject.put("size", Integer.parseInt(node.getSize()));
					jsonobject.put("color", node.getColor());
					nodeArray.put(jsonobject);
					for(GraphNode n : node.getNetwork()){
						JSONObject jo = new JSONObject();
						jo.put("id",n.getId());
						jo.put("name",n.getNodeName());
						jo.put("size", Integer.parseInt(n.getSize()));
						jo.put("color", n.getColor());
						nodeArray.put(jo);
					}
					for(GraphPath p : node.getNetworkPath()){
						JSONObject po = new JSONObject();
						po.put("id",p.getPathId());
						po.put("name",p.getPathName());
						po.put("source",p.getSourceId());
						po.put("target",p.getDestinationId());
						po.put("color",p.getColor());
						verticesArray.put(po);
					}
				}
			}

			for(String key : ProducerConsumerStore.getPathStore().keySet()){
				GraphPath path = ProducerConsumerStore.getPathStore().get(key);
				if((path.getPathName()).contains(routerName) || routerName.equals("all")){
					//We check if these paths are infact instance of a link, which it should be and hence it makes it safe to typecast
					if(path instanceof Link){
						Link link = (Link)path;
						//We then get the consumer from the ProducerConsumerStore using the link and check if the consumer is in ESTABLISHED state. If yes, it means
						//that that particular link between two routers is ACTIVE and processing BGP messages.
						BgpConsumer consumer = ProducerConsumerStore.getConsumerOfLink(link);
						if(!consumer.getFsmState().equals(FSMState.ESTABLISHED))
							continue;
					}
					jsonobject = new JSONObject();
					jsonobject.put("id",path.getPathId());
					jsonobject.put("name",path.getPathName());
					jsonobject.put("source",path.getSourceId());
					jsonobject.put("target",path.getDestinationId());
					jsonobject.put("color",path.getColor());
					verticesArray.put(jsonobject);
				}
			}
			JSONObject response = new JSONObject();
			response.put("vertices", verticesArray);
			response.put("nodes", nodeArray);
			return  response.toString();
		}catch(Exception e){
			log.error("Exception in json stuff : " + e.getMessage());
			e.printStackTrace();
			return "--";
		}
	}
	
	@GET
	@Path("/graphs/{routerName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String graphs(@PathParam("routerName") String routerName){
		ArrayList<String> verticesId = new ArrayList<String>();
		ArrayList<String> nodesId = new ArrayList<String>();
		JSONArray nodeArray = new JSONArray();
		JSONArray verticesArray = new JSONArray();
		BgpConfig config = ProducerConsumerStore.getBgpConfigByName(routerName);
		if(config == null)
			return "ROUTER NOT FOUND";
		Map<String, Rule> rules = config.getRuleStore().getLocalRib();
		try{
			nodesId.add(routerName);
			nodeArray.put(getNode(routerName, 50, "red"));
			for(String key : rules.keySet()){
				Rule rule = rules.get(key);
				if(rule.getPath().getPathSegmentAsString().split("==").length == 1){
					verticesArray.put(getVertices(rule.getNetwork()+"=="+routerName, routerName, rule.getNetwork(), "yellow"));
					verticesId.add(rule.getNetwork()+"=="+routerName);
				}else{
					String neighbours[] = rule.getPath().getPathSegmentAsString().split("==");
					for(int i=0; i<neighbours.length-1; i++){
						if(!edgeExists(neighbours[i], neighbours[i+1], verticesId)){
							verticesArray.put(getVertices(neighbours[i]+"=="+neighbours[i+1], neighbours[i], neighbours[i+1], "black"));
							verticesId.add(neighbours[i]+"=="+neighbours[i+1]);
						}
					}
					if(!edgeExists(neighbours[neighbours.length-1], rule.getNetwork(), verticesId)){
						verticesArray.put(getVertices(neighbours[neighbours.length-1] + "==" +rule.getNetwork(), neighbours[neighbours.length-1], rule.getNetwork(), "yellow"));
						verticesId.add(neighbours[neighbours.length-1] + "==" +rule.getNetwork());
					}
				}
			}
			for(String str : verticesId){
				String[] s = str.split("==");
				for(int i=0;i<s.length;i++){
					if(!nodeExists(s[i], nodesId)){
						if(s[i].contains(".")){
							nodesId.add(s[i]);
							nodeArray.put((getNode(s[i], 25, "green")));
						}else{
							nodesId.add(s[i]);
							nodeArray.put((getNode(s[i], 50, "red")));
						}
					}
				}
			}
			JSONObject response = new JSONObject();
			response.put("vertices", verticesArray);
			response.put("nodes", nodeArray);
			return  response.toString();
		}catch(Exception e){
			log.error("Exception in json stuff : " + e.getMessage());
			e.printStackTrace();
			return "--";
		}
	}

	private boolean edgeExists(String n1, String n2, ArrayList<String> verticesId){
		if(verticesId.contains(n1+"=="+n2) || (verticesId.contains(n2+"=="+n1) ))
			return true;
		else  
			return false;
	}
	private boolean nodeExists(String n, ArrayList<String> nodesId){
		if(nodesId.contains(n))
			return true;
		else  
			return false;
	}
	private JSONObject getNode(String id, int size, String color) throws Exception{
		JSONObject jsonobject = new JSONObject();
		jsonobject.put("id", id);
		jsonobject.put("name", id);
		jsonobject.put("size", size);
		jsonobject.put("color", color);
		return jsonobject;
	}
	private JSONObject getVertices(String id, String source, String target, String color) throws Exception{
		JSONObject jsonobject = new JSONObject();
		jsonobject.put("id", id);
		jsonobject.put("name", id);
		jsonobject.put("target", target);
		jsonobject.put("source", source);
		jsonobject.put("color", color);
		return jsonobject;
	}
}

package com.bgppp.protoprocessor.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.json.JSONArray;

import com.bgppp.protoprocessor.FSMState;
import com.bgppp.protoprocessor.NodeStore;
import com.bgppp.protoprocessor.ProducerConsumerStore;
import com.bgppp.protoprocessor.Link;

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
			for(String key : NodeStore.getHashStore().keySet()){
				if(NodeStore.getHashStore().get(key).getNodeName().contains(routerName) || routerName.equals("all")){
					jsonobject = new JSONObject();
					jsonobject.put("id",NodeStore.getHashStore().get(key).getId());
					jsonobject.put("name",NodeStore.getHashStore().get(key).getNodeName());
					nodeArray.put(jsonobject);
				}
			}
			for(String key : NodeStore.getPathStore().keySet()){
				if((NodeStore.getPathStore().get(key).getPathName()).contains(routerName) || routerName.equals("all")){
					//We check if these paths are infact instance of a link, which it should be and hence it makes it safe to typecast
					if(NodeStore.getPathStore().get(key) instanceof Link){
						Link link = null;
						//typecasting
						link = (Link)NodeStore.getPathStore().get(key);
						log.info(ProducerConsumerStore.getBgpProducersMap().keySet());
						log.info(link.getPathName().split("-")[0]+"_producer_"+link.getSourceAddress());
						log.info(ProducerConsumerStore.getBgpConsumerByName(link.getPathName().split("-")[0]+"_consumer_" + link.getSourceAddress()).getFsmState());
						log.info(ProducerConsumerStore.getBgpProducerByName(link.getPathName().split("-")[0]+"_producer_"+link.getDestinationAddress()).getFsmState());
						FSMState consumerState = ProducerConsumerStore.getBgpConsumerByName(link.getPathName().split("-")[0]+"_consumer_" + link.getSourceAddress()).getFsmState();
						//If the consumer of a link id in ESTABLISHED state, then it is treated as the source of a connection between consumer and producer
						if(!consumerState.equals(FSMState.ESTABLISHED))
							continue;
					}
					jsonobject = new JSONObject();
					jsonobject.put("id",NodeStore.getPathStore().get(key).getId());
					jsonobject.put("source",NodeStore.getPathStore().get(key).getSourceId());
					jsonobject.put("target",NodeStore.getPathStore().get(key).getDestinationId());
					jsonobject.put("name",NodeStore.getPathStore().get(key).getPathName());
					verticesArray.put(jsonobject);
				}
			}
			JSONObject response = new JSONObject();
			response.put("vertices", verticesArray);
			response.put("nodes", nodeArray);
			return  response.toString();
		}catch(Exception e){
			System.out.println("Exception in json stuff : " + e.getMessage());
			e.printStackTrace();
			return "--";
		}
	}
}

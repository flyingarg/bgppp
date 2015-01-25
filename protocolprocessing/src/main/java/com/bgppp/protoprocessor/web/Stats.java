package com.bgppp.protoprocessor.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.json.JSONArray;

import com.bgppp.protoprocessor.BgpConsumer;
import com.bgppp.protoprocessor.FSMState;
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
			for(String key : ProducerConsumerStore.getHashStore().keySet()){
				if(ProducerConsumerStore.getHashStore().get(key).getNodeName().contains(routerName) || routerName.equals("all")){
					jsonobject = new JSONObject();
					jsonobject.put("id",ProducerConsumerStore.getHashStore().get(key).getId());
					jsonobject.put("name",ProducerConsumerStore.getHashStore().get(key).getNodeName());
					nodeArray.put(jsonobject);
				}
			}

			for(String key : ProducerConsumerStore.getPathStore().keySet()){
				log.info("path store key"+key);
			}
			for(String key : ProducerConsumerStore.getHashStore().keySet()){
				log.info("node store key"+key);
			}

			for(String key : ProducerConsumerStore.getPathStore().keySet()){
				if((ProducerConsumerStore.getPathStore().get(key).getPathName()).contains(routerName) || routerName.equals("all")){
					//We check if these paths are infact instance of a link, which it should be and hence it makes it safe to typecast
					if(ProducerConsumerStore.getPathStore().get(key) instanceof Link){
						Link link = (Link)ProducerConsumerStore.getPathStore().get(key);
						//We then get the consumer from the ProducerConsumerStore using the link and check if the consumer is in ESTABLISHED state. If yes, it means
						//that that particular link between two routers is ACTIVE and processing BGP messages.
						BgpConsumer consumer = ProducerConsumerStore.getConsumerOfLink(link);
						if(!consumer.getFsmState().equals(FSMState.ESTABLISHED))
							continue;
					}
					jsonobject = new JSONObject();
					jsonobject.put("id",ProducerConsumerStore.getPathStore().get(key).getId());
					jsonobject.put("source",ProducerConsumerStore.getPathStore().get(key).getSourceId());
					jsonobject.put("target",ProducerConsumerStore.getPathStore().get(key).getDestinationId());
					jsonobject.put("name",ProducerConsumerStore.getPathStore().get(key).getPathName());
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

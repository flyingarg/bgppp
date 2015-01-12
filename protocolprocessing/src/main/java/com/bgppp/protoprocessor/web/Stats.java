package com.bgppp.protoprocessor.web;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.json.JSONArray;

import com.bgppp.protoprocessor.NodeStore;

@Path("/bgppp")
public class Stats{
	
	@GET
	@Path("/stats/{routerName}")
	@Produces(MediaType.APPLICATION_JSON)
	public String stats(@PathParam("routerName") String routerName){
		try{
			//String routerName = "all";
			JSONObject jsonobject;
			JSONArray nodeArray = new JSONArray();
			JSONArray verticesArray = new JSONArray();
			for(String key : NodeStore.getHashStore().keySet()){
				if(NodeStore.getHashStore().get(key).getNodeName().contains(routerName) || routerName.equals("all")){
					jsonobject = new JSONObject();
					jsonobject.append("id",NodeStore.getHashStore().get(key).getId());
					jsonobject.append("name",NodeStore.getHashStore().get(key).getNodeName());
					nodeArray.put(jsonobject);
				}
			}
			for(String key : NodeStore.getPathStore().keySet()){
				if((NodeStore.getPathStore().get(key).getPathName()).contains(routerName) || routerName.equals("all")){
					jsonobject = new JSONObject();
					jsonobject.append("id",NodeStore.getPathStore().get(key).getId());
					jsonobject.append("source",NodeStore.getPathStore().get(key).getSourceId());
					jsonobject.append("target",NodeStore.getPathStore().get(key).getDestinationId());
					jsonobject.append("name",NodeStore.getPathStore().get(key).getPathName());
					verticesArray.put(jsonobject);
				}
			}
			JSONObject response = new JSONObject();
			response.append("vertices", verticesArray);
			response.append("nodes", nodeArray);
			return  response.toString();
		}catch(Exception e){
			System.out.println("Exception in json stuff : " + e.getMessage());
			e.printStackTrace();
			return "--";
		}
	}
}

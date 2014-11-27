package com.bgppp.protoprocessor.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

@Path("/bgppp")
public class Stats{
	
	@GET
	@Path("stats")
	@Produces("application/json")
	public String stats() throws Exception{
		return  "{\"id\":\"n2\",\"label\":\"Node 2\",\"status\":\"connecting\"}";
	}
}

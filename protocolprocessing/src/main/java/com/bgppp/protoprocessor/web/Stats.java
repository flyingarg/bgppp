package com.bgppp.protoprocessor.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/bgppp")
public class Stats{
	
	@GET
	@Path("stats")
	@Produces(MediaType.TEXT_PLAIN)
	public String stats(){
		return "Mohit";
	}
}

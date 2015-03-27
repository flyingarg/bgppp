package com.bgppp.protoprocessor.rules;

//import com.bgppp.protoprocessor.packet.BgpUpdatePacket;

public class Rule{
	private String type; //Withdrawn routes or path attributes.
	
	private String network;
	private String nextHop;
	private String metric;
	private String localPref;
	//private String weight;
	private String path;//Added locally by the admin, MAYBE
	private String origin;

    public static String MAX_LOCAL_PREF = "131071";

	public Rule(){
	}
	public Rule(String network, String nextHop, String path, String origin, /*String weight,*/ String localPref, String metric, String type){
		this.network = network;
		this.nextHop = nextHop;
		this.path = path;
		this.origin = origin;
		//this.weight = weight;
		this.localPref = localPref;
		this.metric = metric;
		this.type = type;
	}

	//Just getters and setters.
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getNextHop() {
		return nextHop;
	}
	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}
	public String getMetric() {
		return metric;
	}
	public void setMetric(String metric) {
		this.metric = metric;
	}
	public String getLocalPref() {
		return localPref;
	}
	public void setLocalPref(String localPref) {
		this.localPref = localPref;
	}
	/*public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getPath() {
		return path;
	}*/
	public void setPath(String path) {
		this.path = path;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String toString(){
		return "Origin:"+origin+
			"Path:"+path+
			//"Weight:"+weight+
			"Metric:"+metric+
			"LocalPref:"+localPref+
			"NextHop:"+nextHop+
			"Network:"+network;
	}
}

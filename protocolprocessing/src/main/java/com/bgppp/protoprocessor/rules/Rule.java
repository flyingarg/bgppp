package com.bgppp.protoprocessor.rules;

//import org.apache.log4j.Logger;


//import com.bgppp.protoprocessor.packet.BgpUpdatePacket;

public class Rule{
	//private static final Logger log = Logger.getLogger(Rule.class);

	private RuleType type; //Withdrawn routes or path attributes.
	
	private String network;
	private NextHopAttributeType nextHop;
	private MultiExitDiscAttributeType metric;
	private LocalPrefAttributeType localPref;
	//private String weight;
	private AsPathAttributeType path;//Added locally by the admin, MAYBE
	private OriginAttributeType origin;
	private String ruleSender;
	private String wrPrefix;
	private String bgpOperationName;
    public static String MAX_LOCAL_PREF = "131071";

	public Rule(){
	}
	public Rule(String network, NextHopAttributeType nextHop, AsPathAttributeType path, OriginAttributeType origin, 
			LocalPrefAttributeType localPref, MultiExitDiscAttributeType metric, String ruleSender, String bgpOperationName){
		this.network = network;
		this.nextHop = nextHop;
		this.path = path;
		this.origin = origin;
		this.localPref = localPref;
		this.metric = metric;
		//this.type = type;
		this.ruleSender = ruleSender;
		this.bgpOperationName = bgpOperationName;
	}

	public String toString(){
		String response = "";
			response+="Network:"+network;
			response+="\nNextHop:"+(nextHop!=null?nextHop.getNextHop():"");
			response+="\nLocalPref:"+(localPref!=null?localPref.getLocalPref():"");
			response+="\nOrigin:"+(origin!=null?origin.getAttrValue():"");
			response+="\nPath:"+(path!=null?path.getPathSegmentType():"");
			response+=(path!=null?path.getPathSegmentAsString():"");
			response+="\nMetric:"+(metric!=null?metric.getMultiExitDiscriminator():"");
			response+="\nReceivedFrom:"+(getRuleSenderName());
			//log.info(response);
			return response;
	}

	/**
	 * @return the type
	 */
	public RuleType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RuleType type) {
		this.type = type;
	}

	/**
	 * @return the network
	 */
	public String getNetwork() {
		return network;
	}

	/**
	 * @param network the network to set
	 */
	public void setNetwork(String network) {
		this.network = network;
	}

	/**
	 * @return the nextHop
	 */
	public NextHopAttributeType getNextHop() {
		return nextHop;
	}

	/**
	 * @param nextHop the nextHop to set
	 */
	public void setNextHop(NextHopAttributeType nextHop) {
		this.nextHop = nextHop;
	}

	/**
	 * @return the metric
	 */
	public MultiExitDiscAttributeType getMetric() {
		return metric;
	}

	/**
	 * @param metric the metric to set
	 */
	public void setMetric(MultiExitDiscAttributeType metric) {
		this.metric = metric;
	}

	/**
	 * @return the localPref
	 */
	public LocalPrefAttributeType getLocalPref() {
		return localPref;
	}

	/**
	 * @param localPref the localPref to set
	 */
	public void setLocalPref(LocalPrefAttributeType localPref) {
		this.localPref = localPref;
	}

	/**
	 * @return the path
	 */
	public AsPathAttributeType getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(AsPathAttributeType path) {
		this.path = path;
	}

	/**
	 * @return the origin
	 */
	public OriginAttributeType getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(OriginAttributeType origin) {
		this.origin = origin;
	}

	/**
	 * @return the rcvFromPeer
	 */
	public String getRuleSenderName() {
		return ruleSender;
	}

	/**
	 * @param ruleSender the rcvFromPeer to set
	 */
	public void setRuleSenderName(String ruleSender) {
		this.ruleSender = ruleSender;
	}

	/**
	 * @return the wrPrefix
	 */
	public String getWrPrefix() {
		return wrPrefix;
	}

	/**
	 * @param wrPrefix the wrPrefix to set
	 */
	public void setWrPrefix(String wrPrefix) {
		this.wrPrefix = wrPrefix;
	}

	/**
	 * @return the bgpOperationName
	 */
	public String getBgpOperationName() {
		return bgpOperationName;
	}

	/**
	 * @param bgpOperationName the bgpOperationName to set
	 */
	public void setBgpOperationName(String bgpOperationName) {
		this.bgpOperationName = bgpOperationName;
	}
}

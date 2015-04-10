package com.bgppp.protoprocessor.packet;

import java.util.*;
import java.nio.ByteBuffer;

import org.apache.log4j.*;

import com.bgppp.protoprocessor.rules.*;

public class BgpUpdatePacket extends BgpHeader{
	private static final Logger log =	Logger.getLogger(BgpUpdatePacket.class);

	boolean isAddition = false;
	String nodeName;
	private String paPrefixes;
	private String wrPrefixes;
	private List<Attribute> paAttributes;

	/**
	 * Suggesting not to use this constructor.
	 */
	public BgpUpdatePacket(){
	}

	/**
	 */
	public BgpUpdatePacket(Rule rule){
		this.paPrefixes = rule.getNetwork();
		List<Attribute> atr = new ArrayList<Attribute>();
		atr.add(rule.getNextHop());
		atr.add(rule.getMetric());
		atr.add(rule.getLocalPref());
		atr.add(rule.getOrigin());
		atr.add(rule.getPath());
		this.paAttributes = atr;
		this.wrPrefixes = rule.getWrPrefix();
	}
	/**
	 * Create a BgpUpdatePacket. This would create an object that can be used to generate the byte[] response using the <code>byte[] prepareUpdateSegment()</code> method.
	 * @param paPrefixes Enter paPrefix of the for <code>2/8</code> or <code>2.5/16</code>. These translate to prefixes <code>2.0.0.0/8 and 2.5.0.0/16</code>
	 * @param paAttributes Enter a list of attributes.
	 * @param wrPrefixes Enter wrPrefix of the for <code>2/8</code> or <code>2.5/16</code>. These translate to prefixes <code>2.0.0.0/8 and 2.5.0.0/16</code>
	 */
	public BgpUpdatePacket(String paPrefixes, List<Attribute> paAttributes, String wrPrefixes){
		this.paPrefixes = paPrefixes;
		this.wrPrefixes = wrPrefixes;
		this.paAttributes = paAttributes;
	}

	/**
	 * Create a BgpUpdatePacket from the bytes received. This translates a stream of bytes to an object.
	 * @param bytes
	 */
	public BgpUpdatePacket(byte[] bytes){
		this.paPrefixes = new String();
		this.wrPrefixes = new String();
		this.paAttributes = new ArrayList<Attribute>();
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		buffer.position(16);
		byte[] packetLength = new byte[2];buffer.get(packetLength);
		if(bytes.length != getIntegerFromBytes(packetLength)){
			log.error("Actual length and length specified in feild are not same." + bytes.length + ":" + getIntegerFromBytes(packetLength));
		}
		byte[] packetType = new byte[1]; buffer.get(packetType);
		if(2 != getIntegerFromBytes(packetType)){
			log.error("Not an update packet");
		}
		byte[] wrLength = new byte[2]; buffer.get(wrLength);
		int withdrawrouteLength = getIntegerFromBytes(wrLength);
		byte[] withdrawroute = new byte[withdrawrouteLength];buffer.get(withdrawroute);
		this.wrPrefixes = stringFromPrefix(withdrawroute);

		byte[] paLength = new byte[2]; buffer.get(paLength);
		int totalAttributesLength  = buffer.position() + getIntegerFromBytes(paLength);
		while(buffer.position() < totalAttributesLength){
			int currentPosition = buffer.position();
			int paAttrLength = 3 + getIntegerFromBytes(new byte[]{buffer.get(currentPosition+2)});
			buffer.position(currentPosition);
			byte[] tempAttrBuffer = new byte[paAttrLength];
			buffer.get(tempAttrBuffer);
			Attribute attr = constructAttributeFromByte(tempAttrBuffer);
			this.paAttributes.add(attr);
		}
		byte[] nlri = new byte[bytes.length - buffer.position()]; buffer.get(nlri);
		this.paPrefixes = stringFromPrefix(nlri);
	}

	/**
	 * Once can get this Object as a stream of bytes that can be directly sent as a BGP update packet over TCP.
	 */
	public byte[] prepareUpdateSegment(){
		Byte[] packet = new Byte[0];
		
		//Withdrwal routes length
		Byte[] wrLength = new Byte[2];
		Byte[] wrPacket = new Byte[0];
		int wrlen = this.wrPrefixes == null || this.wrPrefixes.equals("") ? 0 : 2;
		wrLength[0] = getByteArrayForInteger(wrlen,2)[0];
		wrLength[1] = getByteArrayForInteger(wrlen,2)[1];

		//Withdrawal routes
		if(wrlen != 0){
			Byte[] wrs = getPrefixPacket(this.wrPrefixes);
			wrPacket = conc(wrLength, wrs);
		}else{
			wrPacket = conc(wrPacket, wrLength);
		}
		
		//NLRI routes
		Byte[] nlri = getPrefixPacket(this.paPrefixes);

		int totalLengthAsInt = 0;
		Byte[] paas = new Byte[0];
		for(Attribute attribute : paAttributes){
			totalLengthAsInt = totalLengthAsInt + attribute.getAsBytes().length;
			paas = conc(paas,attribute.getAsBytes());
		}

		Byte[] totalPaLength = new Byte[2];
		totalPaLength = new Byte[]{getByteArrayForInteger(totalLengthAsInt,2)[0],getByteArrayForInteger(totalLengthAsInt,2)[1]};
		packet = conc(conc(conc(wrPacket,totalPaLength),paas),nlri);
		return addHeader(2, getbyteFromByte(packet));
		//return getbyteFromByte(finalPacket);
	}

	public boolean isUpdateMessage(){
		return true;
	}

	/**
	 * Converts from prefix to Byte[]. 
	 * @param prefixes of the form <code>2/8</code> or <code>2.5/16</code> are translated to byte arrays <code>byte[]{(byte)2,(byte)8} and byte[]{(byte)2,(byte)5,(byte)8}</code>
	 */
	public Byte[] getPrefixPacket(String prefixes){
		Byte[] p = new Byte[0];
		String prefix[] = prefixes.split("/")[0].trim().split("\\.");
		String length = prefixes.split("/")[1].trim();
		Byte[] temp = new Byte[1+prefix.length];
		temp[prefix.length] = getByteArrayForInteger(Integer.parseInt(length),1)[0];
		for(int k=0; k<prefix.length; k++){
			temp[k] = getByteArrayForInteger(Integer.parseInt(prefix[k]),1)[0];
		}
		p = conc(p, temp);
		return p;
	}

	/**
	 * Takes the <code>byte[]</code> section belonging to an Attribute, determines the attribute type and then creates a Attribute object from <code>byte[]</code>
	 */
	public Attribute constructAttributeFromByte(byte[] bytes){
		try{
			switch(bytes[1]){
				case 1: return new OriginAttributeType(bytes);
				case 2: return new AsPathAttributeType(bytes);
				case 3: return new NextHopAttributeType(bytes);
				case 4: return new MultiExitDiscAttributeType(bytes);
				case 5: return new LocalPrefAttributeType(bytes);
				case 6: return new AtomicAggregateAttributeType(bytes);
				case 7: return new AggregatorAttributeType(bytes);
			}
		}catch(Exception exception){
			exception.printStackTrace();
			log.error(exception.getMessage());
		}
		return null;
	}
	public String stringFromPrefix(byte[] prefix){
		if(prefix.length == 0)
			return "";
		int length = prefix.length;
		String response = "";
		for(int i=4; i>-1; i--){
			if(i == 4){
				response = "/"+getIntegerFromBytes(new byte[]{(byte)prefix[prefix.length-1]});
				length = length - 1;
			}else if(i>(length -1)){
				response = ".0"+response;
			}else if(i!=0){
				response = "."+getIntegerFromBytes(new byte[]{(byte)prefix[length-1]}) + response;
				length = length - 1;
			}else{
				response = ""+getIntegerFromBytes(new byte[]{(byte)prefix[length-1]}) + response;
			}
		}
		return response;
	}

	public Rule getRule(String rcvFrom){//TODO : might need too eliminate type alltogether.
		Rule rule = new Rule();
		rule.setNetwork(getPaPrefixes());
		//rule.setType(type);
		for(Attribute attr : getPaAttributes()){
			if(attr instanceof NextHopAttributeType){
				rule.setNextHop((NextHopAttributeType)attr);
			}else if(attr instanceof LocalPrefAttributeType){
				rule.setLocalPref((LocalPrefAttributeType)attr);
			}else if(attr instanceof OriginAttributeType){
				rule.setOrigin((OriginAttributeType)attr);
			}else if(attr instanceof AsPathAttributeType){
				rule.setPath((AsPathAttributeType)attr);
			}else if(attr instanceof MultiExitDiscAttributeType){
				rule.setMetric((MultiExitDiscAttributeType)attr);
			}
		}
		rule.setPeerHandlerName(rcvFrom);
		return rule;
	}

	//Getters for these three important attributes.
	public String getPaPrefixes() {
		return paPrefixes;
	}
	public String getWrPrefixes() {
		return wrPrefixes;
	}
	public List<Attribute> getPaAttributes() {
		return paAttributes;
	}
}

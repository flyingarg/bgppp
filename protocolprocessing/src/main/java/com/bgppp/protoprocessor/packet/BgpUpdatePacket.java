package com.bgppp.protoprocessor.packet;

import java.util.*;

import com.bgppp.protoprocessor.rules.*;

public class BgpUpdatePacket extends BgpHeader{

	boolean isAddition = false;
	String nodeName;
	private List<String> paPrefixes;
	private List<String> wrPrefixes;
	private List<Attribute> paAttributes;

	public BgpUpdatePacket(String paPrefixes, List<Attribute> paAttributes, String wrPrefixes){
		this.paPrefixes = new ArrayList<String>();
		if(paPrefixes != null && paPrefixes != "" && paPrefixes.contains("/")){
			for(String prefix : paPrefixes.split(":")){
				this.paPrefixes.add(prefix);
			}
		}
		this.wrPrefixes = new ArrayList<String>();
		if(paPrefixes != null && paPrefixes != "" && paPrefixes.contains("/")){
			for(String prefix : paPrefixes.split(":")){
				this.wrPrefixes.add(prefix);
			}
		}
		this.paAttributes = paAttributes;
	}
	
	public byte[] prepareUpdateSegment(){
		Byte[] packet = new Byte[0];

		//Withdrwal routes length
		Byte[] wrLength = new Byte[2];
		byte[] t = getByteArrayForInteger(this.wrPrefixes.size(),2);
		wrLength[0] = t[0]; wrLength[1] = t[1];

		//Withdrawal routes
		Byte[] wrs = new Byte[wrPrefixes.size()*5];
		int i = 0;
		if(this.wrPrefixes.size() != 0){
			for(String wr : this.wrPrefixes){
				String prefix = wr.split("/")[0].trim();
				String length = wr.split("/")[1].trim();
				wrs[i*5] = Byte.parseByte(length, 10);
				int j = 1;
				for(String sub : prefix.split(".")){
					wrs[(i*5)+j] = getByteArrayForInteger(Integer.parseInt(sub.trim()),1)[0];
					j = j + 1;
				}
			}
		}

		//Nlri routes		
		/*Byte[] nlri = new Byte[paPrefixes.size()*5];//number of prefixes x (1 byte for prefix length + 4 bytes for prefix)
		i = 0;
		for(String pap : this.paPrefixes){
			String prefix = pap.split("/")[0].trim();
			String length = pap.split("/")[1].trim();
			nlri[i*5] = Byte.parseByte(length,10);
			int j = 0;
			for(String sub : prefix.split("\\.")){
				nlri[(i*5)+j+1] = getByteArrayForInteger(Integer.parseInt(sub.trim()),1)[0];
				j = j+1;
			}
			i = i+1;
		}*/   
		Byte[] nlri = new Byte[2];
		nlri[0] = (byte)8;
		nlri[1] = (byte)2;
		//Then we fill the attributes
		int totalLengthAsInt = 0;
		Byte[] paas = new Byte[0];
		for(Attribute attribute : paAttributes){
			System.out.println(attribute.getAsBytes().length);
			totalLengthAsInt = totalLengthAsInt + attribute.getAsBytes().length;
			paas = conc(paas,attribute.getAsBytes());
		}

		Byte[] totalPaLength = new Byte[2];
		totalPaLength = new Byte[]{getByteArrayForInteger(totalLengthAsInt,2)[0],getByteArrayForInteger(totalLengthAsInt,2)[1]};
		packet = conc(conc(conc(wrLength,totalPaLength),paas),nlri);
		Byte[] finalPacket = addHeader(2, packet);
		return getbyteFromByte(finalPacket);
	}

	//TODO : Once its determined that its a update packet, we need to fill that info onto the update packet
	public boolean isUpdateMessage(){
		return true;
	}

	public PathAttributeRule getPathAttributeRule(){
		PathAttributeRule rule = new PathAttributeRule();
		rule.setPrefix(this.paPrefixes);
		rule.setAttributes(paAttributes);
		return rule;
	}

	public WithdrawnRoutesRule getWithdrawRoutesRule(){
		WithdrawnRoutesRule rule = new WithdrawnRoutesRule();
		rule.setPrefix(this.wrPrefixes);
		return rule;
	}
	
}

package com.bgppp.protoprocessor.rules;

public class NextHopAttributeType extends Attribute{

	private String nextHop = "";

	public NextHopAttributeType(Byte[] bytes) throws Exception{
		super();
		if(bytes[1].intValue() != 3){ 
			throw new AttributeTypeCreationException("Either the bytes are not of correct number or its not the correct attribute type");
		}
		this.isOptional = isBitSet(bytes[0], 7);
		this.isTransitive = isBitSet(bytes[0], 6);
		this.isPartial = isBitSet(bytes[0], 5);
		this.isExtended = isBitSet(bytes[0], 4);
		this.nextHop+= getIntegerFromBytes(new byte[]{bytes[3]})+".";
		this.nextHop+= getIntegerFromBytes(new byte[]{bytes[4]})+".";
		this.nextHop+= getIntegerFromBytes(new byte[]{bytes[5]})+".";
		this.nextHop+= getIntegerFromBytes(new byte[]{bytes[6]})+"";
	}

	/**
	 * First 4 fields are usually 0100
	 * @param isOptional Is the feild optional
	 * @param isTransitive Is the feild transitive
	 * @param isPartial Is the feild Partial
	 * @param isExtended Is the feild Extended
	 * @param nextHop This is 4-octet(byte) feild which specifies the next hop, hop length is always 4-octet
	 */
	public NextHopAttributeType(boolean isOptional, boolean isTransitive, boolean isPartial, boolean isExtended, String nextHop){
		super(isOptional, isTransitive, isPartial, isExtended);
		this.nextHop = nextHop;
	}
	
	@Override
	public String getType() {
		return "3";
	}

	@Override
	public Byte[] getAsBytes() {
		Byte[] flagsAsBytes = getFlagsAsBytes();
		Byte[] typeCode = new Byte[]{Byte.parseByte(getType(),10)};
		Byte[] hopLen = new Byte[]{Byte.parseByte("4",10)};
		Byte[] hop = new Byte[4];
		String s[] = nextHop.trim().split(".");
		hop = new Byte[]{
			getByteArrayForInteger(Integer.parseInt(s[0]), 1)[0],
			getByteArrayForInteger(Integer.parseInt(s[1]), 1)[0],
			getByteArrayForInteger(Integer.parseInt(s[2]), 1)[0],
			getByteArrayForInteger(Integer.parseInt(s[3]), 1)[0],
		};
		return conc(conc(conc(flagsAsBytes, typeCode),hopLen),hop);
	}

}

package com.bgppp.protoprocessor.rules;

public class LocalPrefAttributeType extends Attribute{

	String localPref;
	String localPrefLen;
	public LocalPrefAttributeType(Byte[] bytes) throws Exception{
		super();
		if(bytes[1].intValue() != 5){
			throw new AttributeTypeCreationException("Its not the correct attribute type");
		}
		this.isOptional = isBitSet(bytes[0], 7); 
		this.isTransitive = isBitSet(bytes[0], 6); 
		this.isPartial = isBitSet(bytes[0], 5); 
		this.isExtended = isBitSet(bytes[0], 4); 

		localPrefLen = bytes[2].intValue()+"";
		localPrefLen = getIntegerFromBytes(new byte[]{bytes[3],bytes[4],bytes[5],bytes[6]})+"";
	}

	/**
	 * Not mandatory attribute, but well known attribute. This is used by BGP speaker to advertise preferences of routes to local peers. This is NOT sent to external peers.
	 * Used in decision making of maket sending to local peers. First 4 flags are usually 0100
	 * @param isOptional optional
	 * @param isTransitive transitive
	 * @param isPartial partial
	 * @param isExtended extended
	 * @param localPref any number that can fit in 4 octates is a valid entry, again string though. 
	 */
	public LocalPrefAttributeType(boolean isOptional, boolean isTransitive, boolean isPartial, boolean isExtended, String localPref){
		super(isOptional, isTransitive, isPartial, isExtended);
		this.localPref = localPref;
		this.localPrefLen = "4";
	}


	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "5";
	}


	@Override
	public Byte[] getAsBytes() {
		Byte[] flagsAsBytes = getFlagsAsBytes();
		Byte[] typeCode = new Byte[]{Byte.parseByte(getType(),10)};
		Byte[] length = new Byte[]{Byte.parseByte(this.localPrefLen,10)};
		byte[] r = getByteArrayForInteger(Integer.parseInt(this.localPref), 4);
		Byte[] med = new Byte[]{r[0],r[1],r[2],r[3]};
		return conc(conc(conc(flagsAsBytes,typeCode),length),med);
	}

}

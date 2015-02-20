package com.bgppp.protoprocessor.rules;

public class AtomicAggregateAttributeType extends Attribute{

	String pathSegmentLength = "0";
	
	public AtomicAggregateAttributeType(Byte[] bytes) throws Exception{
		super();
		if(bytes[1].intValue() != 6){ 
			throw new AttributeTypeCreationException("Its not the correct attribute type");
		}
		this.isOptional = isBitSet(bytes[0], 7); 
		this.isTransitive = isBitSet(bytes[0], 6); 
		this.isPartial = isBitSet(bytes[0], 5); 
		this.isExtended = isBitSet(bytes[0], 4); 
	}
	/**
	 * First 4 flags are usually 0100. Other feilds of type and length are 6 and 0 as fixed. This feild is just an indicative for the use of aggregatoion. Additional details
	 * are folllowed up in section 5.1.6 of rfc4271
     * @param isOptional Is the feild optional
	 * @param isTransitive Is the feild transitive
	 * @param isPartial Is the feild Partial
	 * @param isExtended Is the feild Extended
	 */
	public AtomicAggregateAttributeType(boolean isOptional, boolean isTransitive, boolean isPartial, boolean isExtended){
		super(isOptional, isTransitive, isPartial, isExtended);
	}
	
	@Override
	public String getType() {
		return "6";
	}

	@Override
	public Byte[] getAsBytes() {
		Byte[] flagsAsBytes = getFlagsAsBytes();
		Byte[] typeCode = new Byte[]{Byte.parseByte(getType(),10)};
		Byte[] length = new Byte[]{Byte.parseByte("0",10)};
		return conc(conc(flagsAsBytes,typeCode),length);
	}
}

package com.bgppp.protoprocessor.rules;

public class OriginAttributeType extends Attribute{

	private String attrValueLength;
	private String attrValue;
	public OriginAttributeType(byte[] bytes) throws Exception{
		super();
		if(bytes.length!=4 || getIntegerFromBytes(new byte[]{bytes[1]}) != 1){
			throw new AttributeTypeCreationException("Either the bytes are not of correct number or its not the correct attribute type");
		}
		this.isOptional = isBitSet(bytes[0], 7);
		this.isTransitive = isBitSet(bytes[0], 6);
		this.isPartial = isBitSet(bytes[0], 5);
		this.isExtended = isBitSet(bytes[0], 4);

		this.attrValueLength = getIntegerFromBytes(new byte[]{bytes[2]})+"";
		this.attrValue = getIntegerFromBytes(new byte[]{bytes[3]})+"";
	}
	/**
	 * First 4 flags are usually 0100
	 * @param isOptional Is the feild optional
	 * @param isTransitive Is the feild transitive
	 * @param isPartial Is the feild Partial
	 * @param isExtended Is the feild Extended
	 * @param attrValueLength Feild value length is always 1 in this case. Has to be string though
	 * @param attrValue Feild values can be of values 0-IGP/1-EGP/2-INCOMPLETE
	 */
	public OriginAttributeType(boolean isOptional, boolean isTransitive, boolean isPartial, boolean isExtended, String attrValue){
		super(isOptional, isTransitive, isPartial, isExtended);
		this.attrValueLength = "1";
		this.attrValue = attrValue;
	}

	@Override
	public String getType() {
		return "1";
	}

	public Byte[] getAsBytes(){
		Byte[] flagsAsBytes = getFlagsAsBytes();
		Byte[] typeCode = new Byte[]{Byte.parseByte(getType(),10)};
		Byte[] attrValueLength = new Byte[]{Byte.parseByte(this.attrValueLength,10)};//value feild is always 1byte is ORIGIN
		Byte[] attrValue = new Byte[]{Byte.parseByte(this.attrValue,10)};
		return conc(conc(conc(flagsAsBytes, typeCode),attrValueLength),attrValue);
	}

	public String getAttrValue() {
		return attrValue;
	}

}

package com.bgppp.protoprocessor.rules;

public class MultiExitDiscAttributeType extends Attribute{

	String multiExitDiscriminator;
	String multiExitDiscriminatorLength;
	public MultiExitDiscAttributeType(byte[] bytes) throws Exception{
		super();
		if(getIntegerFromBytes(new byte[]{bytes[1]}) != 4){
			throw new AttributeTypeCreationException("Its not the correct attribute type");
		}
		this.isOptional = isBitSet(bytes[0], 7); 
		this.isTransitive = isBitSet(bytes[0], 6); 
		this.isPartial = isBitSet(bytes[0], 5); 
		this.isExtended = isBitSet(bytes[0], 4); 
		multiExitDiscriminatorLength = getIntegerFromBytes(new byte[]{bytes[2]})+"";
		multiExitDiscriminator = getIntegerFromBytes(new byte[]{bytes[3],bytes[4],bytes[5],bytes[6]})+"";
	}

	/**
	 * Not sure what this is used for. So not implementing yet. Just making a basic dummy, which can be added in an update packet, but would not invoke any reation.
	 * The first four flags are usually 1000 
	 * @param isOptional Is always true in this case.
	 * @param isTransitive Is always false;
	 * @param isPartial Is always false;
	 * @param isExtended Is always false;
	 * @param multiExitDiscriminator Note sure what this feild does, but its size is fixed at 4 and contains a number. If multiple exits are detected on an external(inter-AS)
	 * then the one with lower MED must be selected. this value is also called as the metric.
	 */
	public MultiExitDiscAttributeType(boolean isOptional, boolean isTransitive, boolean isPartial, boolean isExtended, String multiExitDiscriminator){
		super(true, false, false, false);//Always optional attribute.
		this.multiExitDiscriminator = multiExitDiscriminator;
		this.multiExitDiscriminatorLength = "4";
	}

	@Override
	public String getType() {
		return "4";
	}

	@Override
	public Byte[] getAsBytes() {
		Byte[] flagsAsBytes = getFlagsAsBytes();
		Byte[] typeCode = new Byte[]{Byte.parseByte(getType(),10)};
		Byte[] length = new Byte[]{Byte.parseByte(this.multiExitDiscriminatorLength,10)};
		byte[] r = getByteArrayForInteger(Integer.parseInt(this.multiExitDiscriminator), 4);
		Byte[] med = new Byte[]{r[0],r[1],r[2],r[3]};
		return conc(conc(conc(flagsAsBytes,typeCode),length),med);
	}

	public String getMultiExitDiscriminator() {
		return multiExitDiscriminator;
	}

}

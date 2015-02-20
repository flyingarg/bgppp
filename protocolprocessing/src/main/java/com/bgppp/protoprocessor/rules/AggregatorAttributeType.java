package com.bgppp.protoprocessor.rules;

public class AggregatorAttributeType extends Attribute{

	String lastAs;
	String lastBgpSpeaker;

	public AggregatorAttributeType(Byte[] bytes) throws Exception{
		super();
		if(bytes[1].intValue() != 7){ 
			throw new AttributeTypeCreationException("Its not the correct attribute type");
		}
		this.isOptional = isBitSet(bytes[0], 7); 
		this.isTransitive = isBitSet(bytes[0], 6); 
		this.isPartial = isBitSet(bytes[0], 5); 
		this.isExtended = isBitSet(bytes[0], 4);

		this.lastAs = getIntegerFromBytes(new byte[]{(byte)bytes[3],(byte)bytes[4]})+"";
		this.lastBgpSpeaker = getIntegerFromBytes(new byte[]{(byte)bytes[5]})+"."
			+getIntegerFromBytes(new byte[]{(byte)bytes[6]})+"."
			+getIntegerFromBytes(new byte[]{(byte)bytes[7]})+"."
			+getIntegerFromBytes(new byte[]{(byte)bytes[8]});
	}

	/**
	 * First 4 flags are usually 1100. Contains the details described in <code>lastAs</code> and <code>lastBgpSpeaker</code>. This being optional, we might not be using it. 
	 * Also, the RFC does not seem to say much about it. definately another todo
     * @param isOptional Is the feild optional
	 * @param isTransitive Is the feild transitive
	 * @param isPartial Is the feild Partial
	 * @param isExtended Is the feild Extendeid
	 * @param lastAs Last AS number that formed the aggregate route in 2 octets
	 * @param lastBgpSpeaker Last BgpSpeaker's IP address(BgpOdentifier) in 4 octates
	 */
	public AggregatorAttributeType(boolean isOptional, boolean isTransitive, boolean isPartial, boolean isExtended, String lastAs, String lastBgpSpeaker){
		super(isOptional, isTransitive, isPartial, isExtended);
		this.lastAs = lastAs;
		this.lastBgpSpeaker = lastBgpSpeaker;
	}
	
	@Override
	public String getType() {
		return "7";
	}

	@Override
	public Byte[] getAsBytes() {
		Byte[] flagsAsBytes = getFlagsAsBytes();
		Byte[] typeCode = new Byte[]{Byte.parseByte(getType(),10)};
		Byte[] length = new Byte[]{Byte.parseByte("6",10)};
		byte[] asNo = getByteArrayForInteger(Integer.parseInt(this.lastAs),2);
		Byte[] as = new Byte[]{asNo[0], asNo[1]};

		String[] identifier = this.lastBgpSpeaker.split(".");
		Byte[] speaker = new Byte[]{
		getByteArrayForInteger(Integer.parseInt(identifier[0]),1)[0],
		getByteArrayForInteger(Integer.parseInt(identifier[0]),2)[0],
		getByteArrayForInteger(Integer.parseInt(identifier[0]),3)[0],
		getByteArrayForInteger(Integer.parseInt(identifier[0]),4)[0]};

		return conc(conc(conc(conc(flagsAsBytes,typeCode),length),as),speaker);
	}
}

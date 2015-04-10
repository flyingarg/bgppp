package com.bgppp.protoprocessor.rules;

public class AsPathAttributeType extends Attribute{

	String pathSegmentType;
	String pathSegmentLength;
	String pathSegmentAsString="";

	public AsPathAttributeType(byte[] bytes) throws Exception{
		super();
		if(getIntegerFromBytes(new byte[]{bytes[1]}) != 2){ 
			throw new AttributeTypeCreationException("Its not the correct attribute type");
		}
		this.isOptional = isBitSet(bytes[0], 7); 
		this.isTransitive = isBitSet(bytes[0], 6); 
		this.isPartial = isBitSet(bytes[0], 5); 
		this.isExtended = isBitSet(bytes[0], 4); 

		pathSegmentType = getIntegerFromBytes(new byte[]{bytes[3]})+"";
		pathSegmentLength = getIntegerFromBytes(new byte[]{bytes[4]})+"";
		for(int i=0; i<getIntegerFromBytes(new byte[]{bytes[4]}); i++){
			pathSegmentAsString = pathSegmentAsString +"==" +  (""+getIntegerFromBytes(new byte[]{bytes[5+(i*2)],bytes[6+(i*2)]}));
		}
		pathSegmentAsString = pathSegmentAsString.replaceFirst("==","");
	}
	/**
	 * First 4 flags are usually 0100.
     * @param isOptional Is the feild optional
	 * @param isTransitive Is the feild transitive
	 * @param isPartial Is the feild Partial
	 * @param isExtended Is the feild Extended
	 * @param pathSegmentType Can have possible integer values 1 and 2. These mean AS_SET and AS_SEQUENCE.
	 * @param pathSegmentLength Contains number of AS(es). If the <code>pathSegmentValue.size() == 2</code> then <code>pathSegmentLength</code> should be set to 2.
	 * @param pathSegmentValue represented as '==' seperated string, the 
	 */
	public AsPathAttributeType(boolean isOptional, boolean isTransitive, boolean isPartial, boolean isExtended, String pathSegmentType, String pathSegmentLength, String pathSegmentAsString){
		super(isOptional, isTransitive, isPartial, isExtended);
		this.pathSegmentType = pathSegmentType;
		this.pathSegmentLength = pathSegmentLength;
		this.pathSegmentAsString = pathSegmentAsString;
	}
	
	@Override
	public String getType() {
		return "2";
	}

	@Override
	public Byte[] getAsBytes() {
		int len = 2;
		Byte[] flagsAsBytes = getFlagsAsBytes();
		Byte[] typeCode = new Byte[]{Byte.parseByte(getType(),10)};

		Byte[] psType = new Byte[]{Byte.parseByte(this.pathSegmentType,10)};//1 octate
		Byte[] psLength = new Byte[]{Byte.parseByte(this.pathSegmentLength,10)};//1 octate, contains the number of AS(es) each in two octates
		Byte[] psValue = new Byte[Integer.parseInt(pathSegmentLength)*2];//This is list of AS(es) that the packet has traversed through. 2 bytes are used to specify as AS
		String[] str = pathSegmentAsString.split("==");
		for(int i=0; i<Integer.parseInt(pathSegmentLength)*2; i=i+2){
			byte[] temp = getByteArrayForInteger(Integer.parseInt(str[i/2]),2);
			psValue[i] = temp[0];
			psValue[i+1] = temp[1];
			len+=2;
		}
		Byte[] totalLength = new Byte[]{Byte.parseByte(""+len,10)};
		return conc(conc(conc(conc(conc(flagsAsBytes,typeCode),totalLength),psType),psLength),psValue);
	}

	public String getPathSegmentType() {
		return this.pathSegmentType;
	}
	public String getPathSegmentLength() {
		return this.pathSegmentLength;
	}
	public String getPathSegmentAsString(){
		return this.pathSegmentAsString;
	}
	public void addPath(String node, String type){
		this.pathSegmentAsString = node+"=="+this.pathSegmentAsString;
		Long l = Long.parseLong(this.pathSegmentLength);
		this.pathSegmentLength = ""+(l.intValue()+1);
	}
}

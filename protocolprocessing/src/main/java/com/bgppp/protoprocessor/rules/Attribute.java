package com.bgppp.protoprocessor.rules;

import com.bgppp.protoprocessor.utils.ByteOperations;

public abstract class Attribute extends ByteOperations{
	public abstract String getType();
	public abstract Byte[] getAsBytes();

	boolean isOptional;
	boolean isTransitive;
	boolean isPartial;
	boolean isExtended;

	public Attribute(){
	}
	public Attribute(boolean isOptional, boolean isTransitive, boolean isPartial, boolean isExtended){
		this.isOptional = isOptional;
		this.isTransitive = isTransitive;
		this.isPartial = isPartial;
		this.isExtended = isExtended;
	}

	public Byte[] getFlagsAsBytes(){
		return getFlagsAsBytes(isOptional, isTransitive, isPartial, isExtended);
	}
}

package com.bgppp.protoprocessor.rules;

import java.util.List;

public class PathAttributeRule extends Rule{
	
	public PathAttributeRule(){
		super.setType(RuleType.PathAttribute);
	}
	
	public List<Attribute> attributes;
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public class Attribute{
		public Attribute(boolean isOptional, boolean isTransitive, boolean isPartial, boolean isExtended, AttributeTypeCode attributeType){
			this.isOptional = isOptional;
			this.isTransitive = isTransitive;
			this.isPartial = isPartial;
			this.isExtended = isExtended;
			this.attributeType =  attributeType;
		}
		public boolean isOptional;
		public boolean isTransitive;
		public boolean isPartial;
		public boolean isExtended;
		public AttributeTypeCode attributeType;
	}

	public enum AttributeTypeCode{
		ORIGIN(1),
		AS_PATH(2),
		NEXT_HOP(3),
		MULTI_EXIT_DISC(4),
		LOCAL_PREF(5),
		ATOMIC_AGGREGATE(6),
		AGGREGATOR(7);

		private int type;

		private AttributeTypeCode(int i){
			this.type = i;
		}
	}
}

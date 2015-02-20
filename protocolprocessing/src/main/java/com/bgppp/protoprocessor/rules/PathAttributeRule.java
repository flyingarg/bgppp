package com.bgppp.protoprocessor.rules;

import java.util.List;

public class PathAttributeRule extends Rule{
	
	public PathAttributeRule(){
		super.setType(RuleType.PathAttribute);
	}
	
	private List<Attribute> attributes;

	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
}

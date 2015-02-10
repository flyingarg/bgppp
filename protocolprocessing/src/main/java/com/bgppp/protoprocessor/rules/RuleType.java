package com.bgppp.protoprocessor.rules;

public enum RuleType{
	WithdrawnRoutes("0"),
	PathAttribute("1");
	private String type = "";
	private RuleType(String type){
		this.type = type;
	}
	public String toString(){
		return type;
	}
}

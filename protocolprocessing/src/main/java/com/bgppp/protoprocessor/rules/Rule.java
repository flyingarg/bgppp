package com.bgppp.protoprocessor.rules;

public class Rule{
	private RuleType type; //Withdrawn routes or path attributes.
	private int length;
	private String prefix;

	public RuleType getType() {
		return type;
	}
	public void setType(RuleType type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}

	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}

package com.bgppp.protoprocessor.rules;

import java.util.*;

public class Rule{
	private RuleType type; //Withdrawn routes or path attributes.
	//private int length;
	private List<String> prefix;

	public RuleType getType() {
		return type;
	}
	public void setType(RuleType type) {
		this.type = type;
	}

	/*public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}*/

	public List<String> getPrefix() {
		return prefix;
	}
	public void setPrefix(List<String> prefix) {
		this.prefix = prefix;
	}
}

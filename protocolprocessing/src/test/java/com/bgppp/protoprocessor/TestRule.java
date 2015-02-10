package com.bgppp.protoprocessor;

import com.bgppp.protoprocessor.rules.*;

import junit.framework.*;
import java.util.*;

public class TestRule extends TestCase{

	public RuleStore ruleStore = null;

	@Override
	public void setUp(){
		ruleStore = new RuleStore();
	}

	public void testWithdrawnRoutes(){
		WithdrawnRoutesRule wr = new WithdrawnRoutesRule();
		wr.setLength(16);
		wr.setPrefix("10.100.1.21");
		ruleStore.addAdjRibIn(wr);
		
		PathAttributeRule ar = new PathAttributeRule();
		ar.setLength(16);
		ar.setPrefix("10.100.1.21");
		List<PathAttributeRule.Attribute> attributes = new ArrayList<PathAttributeRule.Attribute>();
		PathAttributeRule.Attribute attribute1  = ar.new Attribute(true, true, false, true, PathAttributeRule.AttributeTypeCode.ORIGIN);
		attributes.add(attribute1);
		PathAttributeRule.Attribute attribute2  = ar.new Attribute(true, true, false, true, PathAttributeRule.AttributeTypeCode.NEXT_HOP);
		attributes.add(attribute2);
		ar.setAttributes(attributes);
		ruleStore.addAdjRibIn(ar);

		assertEquals(ruleStore.getAdjRibIn().size(), 2);
		assertEquals(attributes.size(), 2);
	}
}

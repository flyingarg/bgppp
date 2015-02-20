package com.bgppp.protoprocessor;

import com.bgppp.protoprocessor.rules.*;

import junit.framework.*;
import java.util.*;

public class TestRule extends TestCase{

	public RuleStore ruleStore = null;
	public OriginAttributeType o = new OriginAttributeType(false, true, false, false, "1", "1");

	@Override
	public void setUp(){
		ruleStore = new RuleStore();
	}

	public void testWithdrawnRoutes(){
		WithdrawnRoutesRule wr = new WithdrawnRoutesRule();
		//wr.setLength(16);
		List<String> prefixes = new ArrayList<String>();
		prefixes.add("10.10.0.0/24");
		wr.setPrefix(prefixes);
		ruleStore.addAdjRibIn(wr);
		
		PathAttributeRule ar = new PathAttributeRule();
		//ar.setLength(16);
		prefixes = new ArrayList<String>();
		prefixes.add("10.100.1.0/24");
		ar.setPrefix(prefixes);
		//One Origin Attribute
		OriginAttributeType oat = new OriginAttributeType(false, true, false, false, "1", "1");
		//One AsPathAttribute
		List<String> ases = new ArrayList<String>();
		ases.add("1212");
		ases.add("4545");
		AsPathAttributeType asat = new AsPathAttributeType(false, true, false, false, "1", "1", ases);
		//One Next Hop
		NextHopAttributeType nhat = new NextHopAttributeType(false, true, false, false, "50.2.3.4");
		//One Local Pre Attribute
		LocalPrefAttributeType lfa = new LocalPrefAttributeType(false, true, false, false, "100"); 
		//One MultiExit
		MultiExitDiscAttributeType medat = new MultiExitDiscAttributeType(true, false, false, false, "1111111");
		//One Atomic
		AtomicAggregateAttributeType aaat = new AtomicAggregateAttributeType(false, true, false, false);
		//One Aggregator
		AggregatorAttributeType aat = new AggregatorAttributeType(true, true, false, false, "33333", "20.30,40,50");
		List<Attribute> attribute = new ArrayList<Attribute>();
		attribute.add(oat);
		attribute.add(asat);
		attribute.add(nhat);
		attribute.add(lfa);
		attribute.add(medat);
		attribute.add(aaat);
		attribute.add(aat);
		
		/*List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute attribute1  = new Attribute(true, true, false, true, AttributeType.ORIGIN);
		attributes.add(attribute1);
		Attribute attribute2  = new Attribute(true, true, false, true, AttributeType.NEXT_HOP);
		attributes.add(attribute2);*/
		ar.setAttributes(attribute);
		ruleStore.addAdjRibIn(ar);

		assertEquals(ruleStore.getAdjRibIn().size(), 2);
		//assertEquals(attributes.size(), 2);
	}

	public void testIsBitSet(){
		byte b = (byte)7;
		assertTrue(o.isBitSet(b, 0));
		assertTrue(o.isBitSet(b, 1));
		assertTrue(o.isBitSet(b, 2));
		b = (byte)(b | 1<<0);
		assertTrue(o.isBitSet(b, 0));
		b = (byte)(b &(~(1<<0)));
		assertFalse(o.isBitSet(b, 0));
		assertEquals((byte)6, b);
		o.getByteArrayForInteger(280,2);
	}

	public void testGetByteArrayForInteger(){
		assertEquals(1542, o.getIntegerFromBytes(new byte[]{(byte)6,(byte)6}));
	}

	public void testGetIntegerFromByteArray(){
		assertEquals(o.getByteArrayForInteger(321,2)[0],(byte)1);
		assertEquals(o.getByteArrayForInteger(321,2)[1],(byte)65);
	}

	public void testGetFlagsAsByte(){
		System.out.println(o.getFlagsAsBytes());
		System.out.println(Integer.toBinaryString(o.getFlagsAsBytes()[0]));
		assertEquals(true, o.isBitSet(o.getFlagsAsBytes()[0],6));
	}
}

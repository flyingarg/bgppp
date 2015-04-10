package com.bgppp.protoprocessor;

import com.bgppp.protoprocessor.rules.*;
import com.bgppp.protoprocessor.packet.*;

import junit.framework.*;
import java.util.*;

public class TestRule extends TestCase{

	public RuleStore ruleStore = null;
	public OriginAttributeType o = new OriginAttributeType(false, true, false, false, "0");

	@Override
	public void setUp(){
		ruleStore = new RuleStore();
	}


	/*public void testWithdrawnRoutes(){
		//WithdrawnRoutesRule wr = new WithdrawnRoutesRule();
		//wr.setLength(16);
		List<String> prefixes = new ArrayList<String>();
		prefixes.add("10.10.0.0/24");
		//wr.setPrefix(prefixes);
		//ruleStore.addAdjRibIn(wr);
		
		//PathAttributeRule ar = new PathAttributeRule();
		//ar.setLength(16);
		prefixes = new ArrayList<String>();
		prefixes.add("10.100.1.0/24");
		//ar.setPrefix(prefixes);
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
		attributes.add(attribute2);
		//ar.setAttributes(attribute);
		//ruleStore.addAdjRibIn(ar);

		assertEquals(ruleStore.getAdjRibIn().size(), 2);
		//assertEquals(attributes.size(), 2);
	}*/

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

	public void testGetIntegerFromBytes(){
		assertEquals(1542, o.getIntegerFromBytes(new byte[]{(byte)6,(byte)6}));
	}

	public void testGetByteArrayForInteger(){
		assertEquals(o.getByteArrayForInteger(321,2)[0],(byte)1);
		assertEquals(o.getByteArrayForInteger(321,2)[1],(byte)65);
	}

	public void testGetFlagsAsByte(){
		assertEquals(true, o.isBitSet(o.getFlagsAsBytes()[0],6));
	}

	/*public void testGeneratePacketFromByte(){
		BgpUpdatePacket source = createUpdatePacket();
		BgpUpdatePacket destination = new BgpUpdatePacket(source.prepareUpdateSegment());
		Rule rule = new Rule(destination);
		assertNotNull(rule);
		System.out.println(rule);
	}*/

	public void testGetNlriPacket(){
		String paPrefixes = new String();
		paPrefixes = "2/8";
		BgpUpdatePacket up = new BgpUpdatePacket();
		Byte[] set1 = up.getPrefixPacket(paPrefixes);
		Byte[] test1 = new Byte[]{(byte)2, (byte)8};
		assertTrue(Arrays.equals(set1, test1));
	}

	private BgpUpdatePacket createUpdatePacket(){
		OriginAttributeType oat = new OriginAttributeType(false, true, false, false, "0");
		List<String> ases = new ArrayList<String>();
		ases.add("65522");
		ases.add("65511");
		AsPathAttributeType asat = new AsPathAttributeType(false, true, false, false, "3", "2", ases);
		NextHopAttributeType nhat = new NextHopAttributeType(false, true, false, false, "10.1.12.1");
		MultiExitDiscAttributeType medat = new MultiExitDiscAttributeType(true, false, false, false, "0");
		LocalPrefAttributeType lfa = new LocalPrefAttributeType(false, true, false, false, "100"); 
		/*MultiExitDiscAttributeType medat = new MultiExitDiscAttributeType(true, false, false, false, "1111111");
		 *AtomicAggregateAttributeType aaat = new AtomicAggregateAttributeType(false, true, false, false);
		 *AggregatorAttributeType aat = new AggregatorAttributeType(true, true, false, false, "33333", "20.30.40.50");*/
		List<Attribute> attribute = new ArrayList<Attribute>();
		attribute.add(oat);
		attribute.add(asat);
		attribute.add(nhat);
		attribute.add(medat);
		attribute.add(lfa);
		//tribute.add(aaat);
		//tribute.add(aat);
		String wrPrefixes = ""; 
		String nlri = "2/20";
		BgpUpdatePacket p = new BgpUpdatePacket(nlri, attribute, wrPrefixes);
        return p;
	}

	public void testStringFromPrefix(){
		BgpUpdatePacket up = new BgpUpdatePacket();
		System.out.println(up.stringFromPrefix(new byte[]{(byte)2, (byte)8}));
		System.out.println(up.stringFromPrefix(new byte[]{(byte)2, (byte)5, (byte)16}));
		assertEquals("2.0.0.0/8",up.stringFromPrefix(new byte[]{(byte)2, (byte)8}));
		assertEquals("2.5.0.0/16",up.stringFromPrefix(new byte[]{(byte)2, (byte)5, (byte)16}));
	}
}

package com.bgppp.protoprocessor.graphs;

import com.bgppp.protoprocessor.ProducerConsumerStore;

public class ThreePaths {

	ProducerConsumerStore pathFromPeers = new ProducerConsumerStore();//Calculated, no user access
	ProducerConsumerStore pathToPeers = new ProducerConsumerStore();//based on nodes removed/added to the pathFromAdmins
	ProducerConsumerStore pathFromAdmins = new ProducerConsumerStore();//Admins have access to modify these
	
}

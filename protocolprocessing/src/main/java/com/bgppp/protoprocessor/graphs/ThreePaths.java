package com.bgppp.protoprocessor.graphs;

import com.bgppp.protoprocessor.NodeStore;

public class ThreePaths {

	NodeStore pathFromPeers = new NodeStore();//Calculated, no user access
	NodeStore pathToPeers = new NodeStore();//based on nodes removed/added to the pathFromAdmins
	NodeStore pathFromAdmins = new NodeStore();//Admins have access to modify these
	
}

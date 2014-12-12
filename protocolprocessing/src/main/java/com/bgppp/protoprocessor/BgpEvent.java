package com.bgppp.protoprocessor;

public enum BgpEvent{
	Event1("ManualStart"),
	Event2("ManualStop"),
	Event3("AutomaticStart"),
	Event4("ManualStart_with_PassiveTcpEstablishment"),
	Event5("AutomaticStart_with_PassiveTcpEstablishment"),
	Event6("AutomaticStart_with_DampPeerOscillations"),
	Event7("AutomaticStart_with_DampPeerOscillations_and_PassiveTcpEstablishment"),
	Event8("AutomaticStop"),
	Event9("ConnectRetryTimer_Expires"),
	Event10("HoldTimer_Expires"),
	Event11("KeepaliveTimer_Expires"),
	Event12("DelayOpenTimer_Expires"),
	Event13("IdleHoldTimer_Expires"),
	Event14("TcpConnection_Valid"),
	Event15("Tcp_CR_Invalid"),
	Event16("Tcp_CR_Acked"),
	Event17("TcpConnectionConfirmed"),
	Event18("TcpConnectionFails"),
	Event19("BGPOpen"),
	Event20("BGPOpen with DelayOpenTimer running"),
	Event21("BGPHeaderErr"),
	Event22("BGPOpenMsgErr"),
	Event23("OpenCollisionDump"),
	Event24("NotifMsgVerErr"),
	Event25("NotifMsg"),
	Event26("KeepAliveMsg"),
	Event27("UpdateMsg"),
	Event28("UpdateMsgErr");

	private final String text = "";

	private BgpEvent(String text){
		text = this.text;
	}

	@Override
	public String toString(){
		return text;
	}
}

package com.bgppp.protoprocessor.packet;

public enum BgpError {
	/**
	 * Connection Not Synchronized. 1/1
	 */
	CONNECTION_NOT_SYNC((short) 1, (short) 1),
	/**
	 * Bad Message Length. 1/2
	 */
	BAD_MSG_LENGTH((short) 1, (short) 2),
	/**
	 * Bad Message Type. 1/3
	 */
	BAD_MSG_TYPE((short) 1, (short) 3),
	/**
	 * Unspecific Open Message error.
	 */
	UNSPECIFIC_OPEN_ERROR((short) 2, (short) 0),
	/**
	 * Unsupported Version Number. 2/1
	 */
	VERSION_NOT_SUPPORTED((short) 2, (short) 1),
	/**
	 * Bad Peer AS. 2/2
	 */
	BAD_PEER_AS((short) 2, (short) 2),
	/**
	 * Bad BGP Identifier. 2/3
	 */
	BAD_BGP_ID((short) 2, (short) 3),
	/**
	 * Unsupported Optional Parameter. 2/4
	 */
	OPT_PARAM_NOT_SUPPORTED((short) 2, (short) 4),
	/**
	 * Unacceptable Hold Time. 2/6
	 */
	HOLD_TIME_NOT_ACC((short) 2, (short) 6),
	/**
	 * Malformed Attribute List. 3/1
	 */
	MALFORMED_ATTR_LIST((short) 3, (short) 1),
	/**
	 * Unrecognized Well-known Attribute. 3/2
	 */
	WELL_KNOWN_ATTR_NOT_RECOGNIZED((short) 3, (short) 2),
	/**
	 * Missing Well-known Attribute. 3/3
	 */
	WELL_KNOWN_ATTR_MISSING((short) 3, (short) 3),
	/**
	 * Attribute Flags Error. 3/4
	 */
	ATTR_FLAGS_MISSING((short) 3, (short) 4),
	/**
	 * Attribute Length Error. 3/5
	 */
	ATTR_LENGTH_ERROR((short) 3, (short) 5),
	/**
	 * Invalid ORIGIN Attribute. 3/6
	 */
	ORIGIN_ATTR_NOT_VALID((short) 3, (short) 6),
	/**
	 * Invalid NEXT_HOP Attribute. 3/8
	 */
	NEXT_HOP_NOT_VALID((short) 3, (short) 8),
	/**
	 * Optional Attribute Error. 3/9
	 */
	OPT_ATTR_ERROR((short) 3, (short) 9),
	/**
	 * Invalid Network Field. 3/10
	 */
	NETWORK_NOT_VALID((short) 3, (short) 10),
	/**
	 * Malformed AS_PATH. 3/11
	 */
	AS_PATH_MALFORMED((short) 3, (short) 11),
	/**
	 * Hold Timer Expired. 4/0
	 */
	HOLD_TIMER_EXPIRED((short) 4, (short) 0),
	/**
	 * Finite State Machine Error. 5/0
	 */
	FSM_ERROR((short) 5, (short) 0),
	/**
	 * Cease. 6/0
	 */
	CEASE((short) 6, (short) 0);

	private final short code;

	private final short subcode;

	BgpError(final short code, final short subcode) {
		this.code = code;
		this.subcode = subcode;
	}

	public short getCode() {
		return this.code;
	}

	public short getSubcode() {
		return this.subcode;
	}

	public static BgpError forValue(final int e, final int s) {
		if (e == 1) {
			if (s == 1)
				return BgpError.CONNECTION_NOT_SYNC;
			if (s == 2)
				return BgpError.BAD_MSG_LENGTH;
			if (s == 3)
				return BgpError.BAD_MSG_TYPE;
		} else if (e == 2) {
			if (s == 0)
				return BgpError.UNSPECIFIC_OPEN_ERROR;
			if (s == 1)
				return BgpError.VERSION_NOT_SUPPORTED;
			if (s == 2)
				return BgpError.BAD_PEER_AS;
			if (s == 3)
				return BgpError.BAD_BGP_ID;
			if (s == 4)
				return BgpError.OPT_PARAM_NOT_SUPPORTED;
			if (s == 6)
				return BgpError.HOLD_TIME_NOT_ACC;
		} else if (e == 3) {
			if (s == 1)
				return BgpError.MALFORMED_ATTR_LIST;
			if (s == 2)
				return BgpError.WELL_KNOWN_ATTR_NOT_RECOGNIZED;
			if (s == 3)
				return BgpError.WELL_KNOWN_ATTR_MISSING;
			if (s == 4)
				return BgpError.ATTR_FLAGS_MISSING;
			if (s == 5)
				return BgpError.ATTR_LENGTH_ERROR;
			if (s == 6)
				return BgpError.ORIGIN_ATTR_NOT_VALID;
			if (s == 8)
				return BgpError.NEXT_HOP_NOT_VALID;
			if (s == 9)
				return BgpError.OPT_ATTR_ERROR;
			if (s == 10)
				return BgpError.NETWORK_NOT_VALID;
			if (s == 11)
				return BgpError.AS_PATH_MALFORMED;
		} else if (e == 4)
			return BgpError.HOLD_TIMER_EXPIRED;
		else if (e == 5)
			return BgpError.FSM_ERROR;
		else if (e == 6)
			return BgpError.CEASE;
		throw new IllegalArgumentException("BGP Error code " + e + " and subcode " + s + " not recognized.");
	}
}

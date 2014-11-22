package com.bgppp.protoprocessor.packet;

public class BgpOpenPacket extends BgpHeader{

	Byte[] version = null;
	Byte[] autonomousSystem = null;
	Byte[] holdTime = null;
	Byte[] bgpIdentifier = null;
	
	public byte[] prepareOpenSegment() throws Exception{
        Byte[] packet = new Byte[0];
        Byte[] version = getVersion();
        Byte[] autonomousSystem = getAutonomousSystem();
        Byte[] holdTime = getHoldTime();
        Byte[] bgpIdentifier = getBgpIdentifier();
        Byte[] optionalParameterLength = new Byte[]{Byte.parseByte("0",10)};
        packet = conc(conc(conc(conc(version, autonomousSystem),holdTime),bgpIdentifier),optionalParameterLength);
		
		Byte[] temp = addHeader(1, packet);
		return getbyteFromByte(temp);
	}
	//TODO : Once its determined that its a update packet, we need to fill that info onto the update packet
	public boolean isOpenMessage(){
		return true;
	}
	

	public Byte[] getVersion() throws Exception{
		if(version == null)
			version = new Byte[]{Byte.parseByte("4",10)};
		if(version.length!=1)
			throw new Exception("Version has to set with a byte array of size 1");
		return version;
	}
	/**
	 * @param version must be a <code>Byte</code> array of size 1 
	 * @throws Exception
	 */
	public void setVersion(Byte[] version) throws Exception{
		if(version.length != 1)
			throw new Exception("Version has to set with a byte array of size 1");
		this.version = version;
	}
	public Byte[] getAutonomousSystem() throws Exception{
		if(autonomousSystem.length!=2  || autonomousSystem == null)
			throw new Exception("AutonomousSystem has to set with a byte array of size 2 and canot be null");
		return autonomousSystem;
	}
	/**
	 * @param autonomousSystem  must be a <code>Byte</code> array of size 2
	 * @throws Exception
	 */
	public void setAutonomousSystem(Byte[] autonomousSystem)  throws Exception{
		if(autonomousSystem.length != 2)
			throw new Exception("AutonomousSystem has to set with a byte array of size 2 and cannot be null");
		this.autonomousSystem = autonomousSystem;
	}
	public Byte[] getHoldTime() throws Exception{
		if(holdTime.length!=2  || holdTime == null)
			throw new Exception("holdTime has to set with a byte array of size 2 and cannot be null");
		return holdTime;
	}
	/**
	 * @param holdTime  must be a <code>Byte</code> array of size 2
	 * @throws Exception
	 */
	public void setHoldTime(Byte[] holdTime)  throws Exception{
		if(holdTime.length != 2)
			throw new Exception("holdTime has to set with a byte array of size 2 and cannot be null");
		this.holdTime = holdTime;
	}
	public Byte[] getBgpIdentifier() throws Exception{
		if(bgpIdentifier.length!=4  || bgpIdentifier == null)
			throw new Exception("bgpIdentifier has to set with a byte array of size 4 and cannot be null");
		return bgpIdentifier;
	}
	/**
	 * @param bgpIdentifier  must be a <code>Byte</code> array of size 4
	 * @throws Exception
	 */
	public void setBgpIdentifier(Byte[] bgpIdentifier)  throws Exception{
		if(bgpIdentifier.length != 4)
			throw new Exception("bgpIdentifier has to set with a byte array of size 4 and cannot be null");
		this.bgpIdentifier = bgpIdentifier;
	}

}

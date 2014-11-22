package com.bgppp.protoprocessor;

public class CharCast {
	public static void main(String[] arguments) {
		byte bite[] = {00,19};
		System.out.println(getInt(bite));
	}
	
    public static int getInt(byte[] bite){
        String strInt = "";
        for(int j=bite.length-1;j>-1;j--){
            if (bite[j]<0)
                strInt = (127 - bite[j]) + strInt;
            else
                strInt = bite[j] + strInt;
            System.out.println(strInt);
        }
        Integer r = new Integer(strInt);
        return r.intValue();
    }


}

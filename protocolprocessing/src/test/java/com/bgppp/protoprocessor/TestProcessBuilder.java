package com.bgppp.protoprocessor;

import junit.framework.TestCase;

import org.apache.log4j.*;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class TestProcessBuilder extends TestCase {
	private static final Logger log = Logger.getLogger(TestProcessBuilder.class);

	public int processBuilderCode() {
		try{
			ProcessBuilder processBuilder = new ProcessBuilder("/home/rajumoh/test.sh");
			Process process = processBuilder.start();
			BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while( (line = output.readLine()) != null ){
				if(line.toLowerCase().contains("error")){
					return 1;
				}
			}
			output.close();
			return 0;
		}catch(IOException e){
			log.error(e.getMessage());
			return 1;
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testProcessBuilder() {
		assertEquals(1,processBuilderCode());
	}

}

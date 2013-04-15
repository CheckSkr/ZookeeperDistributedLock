package com.broada.zookeeper.common;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

import org.apache.zookeeper.KeeperException;
import org.junit.Test;

public class ZkCommonTest extends TestCase {
	
	private ZooKeeperClient zkClient = null;
	
	protected void setUp() throws Exception {
		System.out.println("set up....");
		ZkServerAddresses zkServerAddresses = new ZkServerAddresses("192.168.16.88:2181");
		  zkClient = new ZooKeeperClient(zkServerAddresses,5000);
	}
	
	@Test
	public void testStormZkRoot(){
		String stormRoot = "/storm-zk-root";
		
		List<String> children =  null;
		
		try {
			children = zkClient.get().getChildren(stormRoot, null);
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String string : children) {
			System.out.println(string);
		}
	}
}

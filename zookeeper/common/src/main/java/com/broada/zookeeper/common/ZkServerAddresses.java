package com.broada.zookeeper.common;

import java.util.ArrayList;
import java.util.List;

/**   
 * @Title: ZkServerAddresses.java 
 * @Package com.broada.zookeeper.common 
 * @Description: zookeeper  ����˵�ַ�����
 * 				���Ӹ�ʽΪ����192.168.100.100:2181,192.168.100.101:2181,��
 * @company broada.com
 * @author wujing1  
 * @mail wujing1@broada.com
 * @date 2013-3-29 ����5:15:54 
 * @version V1.0   
 */
public class ZkServerAddresses {
	
	/**
	 * zk��������ַ
	 */
	private List<ZkServerAddress> zkServerAddresses = new ArrayList<ZkServerAddresses.ZkServerAddress>();
	
	
	/**
	 * ���� zk��������ַ
	 * @param hosts ��������ַ��ʽΪ����host:port,host,host:port��
	 *    ����port ��ʹ��Ĭ�ϵ� port ��2181
	 */
	public ZkServerAddresses(String hosts){
		String[] hostsArr = hosts.split(ZkConts.COMMA);
		for (int i = 0; i < hostsArr.length; i++) {
			String[] hostPort = hostsArr[i].split(ZkConts.COLON);
			if(hostPort.length ==1)
				zkServerAddresses.add(new ZkServerAddress(hostPort[0]));
			else
				zkServerAddresses.add(new ZkServerAddress(hostPort[0], Integer.parseInt(hostPort[1])));
		}
	}
	
	/**
	 * ���һ�� hosts
	 * @param host
	 */
	public void add(String host){
		String[] hostPort = host.split(ZkConts.COLON);
		if(hostPort.length ==1)
			zkServerAddresses.add(new ZkServerAddress(hostPort[0]));
		else
			zkServerAddresses.add(new ZkServerAddress(hostPort[0], Integer.parseInt(hostPort[1])));
	}
	
	
	/**
	 * ת��ΪString
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (ZkServerAddress zkServerAddress : zkServerAddresses) {
			sb.append(zkServerAddress.host).append(ZkConts.COLON).append(zkServerAddress.port).append(ZkConts.COMMA);
		}
		return sb.toString().substring(0,sb.toString().length()-1);
	}
	
	private  class ZkServerAddress{
		public  int port;
		public  String host;
		public ZkServerAddress(String host,int port){
			this.host = host;
			this.port = port;
		}
		public ZkServerAddress(String  host){
			this(host,ZkConts.ZK_SERVER_DEFAULT_TPORT);
		}
	}
	
	
	
}

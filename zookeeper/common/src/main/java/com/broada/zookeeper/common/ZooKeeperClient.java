package com.broada.zookeeper.common;

import java.io.IOException;
import java.sql.Time;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * @Title: ZookeeperClient.java
 * @Package com.broada.zookeeper.common
 * @Description: zookeeper �ͻ���
 * @company broada.com
 * @author wujing1
 * @mail wujing1@broada.com
 * @date 2013-3-29 ����4:55:13
 * @version V1.0
 */
public class ZooKeeperClient {

	private static final Logger logger = Logger.getLogger(ZooKeeperClient.class
			.getName());

	/***
	 * zookeeper �ͻ���
	 */
	private ZooKeeper zooKeeper;

	private ZkSessionState zkSessionState;

	private ZkServerAddresses zkServerAddresses;

	private int sessionTimeout;

	private final Set<Watcher> watchers = Collections
			.synchronizedSet(new HashSet<Watcher>());

	public ZooKeeperClient(ZkServerAddresses zkServerAddresses,int sessionTimeout){
	this.zkServerAddresses = zkServerAddresses;
	this.sessionTimeout = sessionTimeout;
	}
	
	 /**
	  * ��ȡ�� zookeeper ��Ⱥ��һ�����ӣ�������������ֱ�ӷ��ؼ���
	  * ����������������
	  * @return
	  * @throws InterruptedException
	  * @throws TimeoutException
	  * @throws IOException
	  */
	public synchronized ZooKeeper get()
			throws InterruptedException, TimeoutException, IOException {
		if (zooKeeper == null) {
			final CountDownLatch ensureConnected = new CountDownLatch(1);
			Watcher watcher = new Watcher() {
				public void process(WatchedEvent event) {
					switch (event.getType()) {
					case None:
						switch (event.getState()) {
						case Expired:
							close();
							break;
						case SyncConnected:
							ensureConnected.countDown();
							break;
						}
					}
					synchronized (watchers) {
						for (Watcher watcher : watchers) {
							watcher.process(event);
						}
					}
				}
			};

			if (zkSessionState != null) {
				zooKeeper = new ZooKeeper(zkServerAddresses.toString(),
						sessionTimeout, watcher, zkSessionState.getSessionId(),
						zkSessionState.getSessionPasswd());
			} else {
				zooKeeper = new ZooKeeper(zkServerAddresses.toString(),
						sessionTimeout, watcher);
			}
			
			zkSessionState = new ZkSessionState(zooKeeper.getSessionId(),
					zooKeeper.getSessionPasswd());
			ensureConnected.await();
		}
		return zooKeeper;
	}

	/**
	 * �ر���������
	 */
	public synchronized void close() {
		if (zooKeeper != null) {
			try {
				zooKeeper.close();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.warn("�ر�zookeeper���ӳ����쳣");
			} finally {
				zooKeeper = null;
				zkSessionState = null;
			}
		}
	}

}

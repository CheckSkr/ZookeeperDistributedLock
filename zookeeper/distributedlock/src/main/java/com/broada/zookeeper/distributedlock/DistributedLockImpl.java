package com.broada.zookeeper.distributedlock;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import com.broada.zookeeper.common.ZkConts;
import com.broada.zookeeper.common.ZooKeeperClient;
import com.broada.zookeeper.common.ZookeeperUtil;
import com.broada.zookeeper.distributedlock.exception.ZkLockException;

/**
 * @Title: DistributedLockImpl.java
 * @Package com.broada.zookeeper.distributedlock
 * @Description: 分布式锁的实现类，该类具体持有某条路径下的锁
 * @company broada.com
 * @author wujing1
 * @mail wujing1@broada.com
 * @date 2013-3-30 下午2:54:37
 * @version V1.0
 */
public class DistributedLockImpl implements IDistributedLock {
	private static final Logger logger = Logger
			.getLogger(DistributedLockImpl.class.getName());

	/**
	 * 锁路径
	 */
	private String lockPath;
	/**
	 * zk客户端
	 */
	private final ZooKeeperClient zkClient;
	/**
	 * 是否已持有锁
	 */
	private volatile boolean isLocked;

	/**
	 * 当前请求锁的锁标记Id
	 */
	private String currentId;
	/**
	 * 当前锁住的节点ZNode，和lockPath 本质一样, 只不过他加上了 锁标记
	 */
	private String currentNode;
	
	/**
	 *  监听锁
	 */
	private LockWatcher watcher;

	/**
	 * zk acl控制
	 */
	private List<ACL> acls;

	public DistributedLockImpl(ZooKeeperClient zkClient, String lockPath) {
		this(zkClient, ZooDefs.Ids.OPEN_ACL_UNSAFE, lockPath);
	}

	public DistributedLockImpl(ZooKeeperClient zkClient, List<ACL> acls,
			String lockPath) {
		this.zkClient = zkClient;
		this.acls = acls;
		this.lockPath = lockPath;
	}
	
	public boolean isLocked(){
		return isLocked;
	}

	/**
	 * 在获取锁之前，要做一些工作： 1).确保 锁的Znode是存在的，若不存在，则直接创建 2).为本次获取锁创建一个临时的ZNode
	 * 
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public void doBeforeLock() throws InterruptedException, KeeperException,
			TimeoutException, IOException {
		ZookeeperUtil.ensurePath(zkClient, acls, lockPath);
		currentNode = zkClient.get().create(
				lockPath + ZkConts.ZK_BROADA_LOCK_MARKUP, null, acls,
				CreateMode.EPHEMERAL_SEQUENTIAL);
		currentId = currentNode.substring(currentNode.lastIndexOf('/') + 1);
		 this.watcher = new LockWatcher();
	}

	/**
	 * 获取锁操作
	 */
	public void lock() throws ZkLockException {
		if (isLocked) {
			throw new ZkLockException("已经拥有该节点的锁，请先释放已拥有的锁...");
		}

		try {
			doBeforeLock();
			watcher.canLockForCurrentId();
			if(!isLocked)//本次请求锁没有成功
				throw new ZkLockException("本次获取锁失败...");
		} catch (InterruptedException e) {
			giveUpAttemptGetLock();
			throw new ZkLockException("InterruptedException  异常抛出...");
		} catch (KeeperException e) {
			giveUpAttemptGetLock();
			throw new ZkLockException("KeeperException  异常抛出...");
		} catch (TimeoutException e) {
			giveUpAttemptGetLock();
			throw new ZkLockException("TimeoutException  异常抛出...");
		} catch (IOException e) {
			giveUpAttemptGetLock();
			throw new ZkLockException("IOException  异常抛出...");
		}

	}

	public boolean tryLock(long timeout) {
		 if (isLocked) {
		      throw new ZkLockException("已经拥有锁，请先释放已经拥有的锁...");
		    }
		    try {
		      doBeforeLock();
		      watcher.canLockForCurrentId();
		     //未获取到锁 等待时间后再获取
		      if (!isLocked) {
		    	  Thread.sleep(timeout);
		    	  watcher.canLockForCurrentId();
		      }
		      if (!isLocked) {
		    	  return false;
		      }
		      
		    } catch (InterruptedException e) {
				giveUpAttemptGetLock();
				throw new ZkLockException("InterruptedException  异常抛出...");
			} catch (KeeperException e) {
				giveUpAttemptGetLock();
				throw new ZkLockException("KeeperException  异常抛出...");
			} catch (TimeoutException e) {
				giveUpAttemptGetLock();
				throw new ZkLockException("TimeoutException  异常抛出...");
			} catch (IOException e) {
				giveUpAttemptGetLock();
				throw new ZkLockException("IOException  异常抛出...");
			}

		    return true;
	}

	public void unlock() throws ZkLockException {
	    if (currentId == null) {
	        throw new ZkLockException("没有获取到锁，请先获取锁...s");
	      }
	      if (isLocked) 
	        cleanup();
	}

	/**
	 * 在尝试多次获取锁失败，可以放弃获取锁操作
	 */
	public void giveUpAttemptGetLock() {
		cleanup();
		isLocked = false;
	}

	/**
	 * 锁清理工作，一般在锁释放，或申请锁尝试不成功的时候执行
	 */
	public void cleanup() {
		logger.info("锁清理开始....");
		try {
			Stat stat = zkClient.get().exists(currentNode, false);
			if (stat != null) {// 删除锁标记
				zkClient.get().delete(currentNode,
						ZkConts.ZK_ZNODE_DATA_ALLVERSION);
			} else {// 锁标记不存在
				logger.warn("锁执行清理，但是并没有锁节点删除...");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		isLocked = false;
		currentId = null;
		currentNode = null;
		logger.info("锁清理完成....");
	}

	/**
	 * locki按
	 * 
	 */
	private class LockWatcher implements Watcher {
		/***
		 * 本次请求是否能获取锁
		 */
		public synchronized void canLockForCurrentId() {
			if (currentId == null) {
				throw new ZkLockException("请求锁出现异常，没有获取锁的Id");
			}
		
			try {
				List<String> waitLockIds = zkClient.get().getChildren(lockPath,
						null);
				Collections.sort(waitLockIds);
				int currentIdIndex = waitLockIds.indexOf(currentId);
				/**
				 * 如果当前节点在第一位，可以获取锁，
				 * 如果不在，则检测该节点等待之前的节点是否存在，如不存在则表明，
				 * 该节点之前已经 放弃了锁
				 */
				if (currentIdIndex == 0) {
					isLocked = true;
				} else {
					String beforeCurrent = waitLockIds.get(currentIdIndex - 1);
					Stat stat = zkClient.get().exists(lockPath+"/"+beforeCurrent, this);
					if (stat == null) {
						canLockForCurrentId();
					}
				}
			} catch (KeeperException e) {
				giveUpAttemptGetLock();
			} catch (InterruptedException e) {
				giveUpAttemptGetLock();
			} catch (TimeoutException e) {
				giveUpAttemptGetLock();
			} catch (IOException e) {
				giveUpAttemptGetLock();
			}

		}

		/**
		 * 只要有节点 b删除，就监听是否能获取锁
		 */
		public void process(WatchedEvent event) {
			if (event.getType() == Event.EventType.NodeDeleted) {
				canLockForCurrentId();
		      } 
		}

	}

	public String lockInfo() {
		return "LockPath = "+this.lockPath+" isLocked = "+this.isLocked +" currentId = "+this.currentId;
	}
}

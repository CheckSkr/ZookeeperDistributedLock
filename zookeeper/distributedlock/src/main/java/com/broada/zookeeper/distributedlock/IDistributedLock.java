package com.broada.zookeeper.distributedlock;

import java.util.concurrent.locks.Lock;

import com.broada.zookeeper.distributedlock.exception.ZkLockException;

/**
 * @Title: ZookeeperClient.java
 * @Package com.broada.zookeeper.common
 * @Description: 获取锁接口
 * @company broada.com
 * @author wujing1
 * @mail wujing1@broada.com
 * @date 2013-3-30 下午2:34:56
 * @version V1.0
 */
public interface IDistributedLock {
	
	 void lock() throws ZkLockException;

	  boolean tryLock(long timeout);

	  void unlock() throws ZkLockException;
	  
	  boolean isLocked();
	  
	  public String lockInfo();
	  

}

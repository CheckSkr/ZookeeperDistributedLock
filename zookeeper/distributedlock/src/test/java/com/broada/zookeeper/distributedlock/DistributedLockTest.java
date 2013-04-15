package com.broada.zookeeper.distributedlock;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

import org.apache.zookeeper.KeeperException;
import org.junit.Test;

import com.broada.zookeeper.common.ZkServerAddresses;
import com.broada.zookeeper.common.ZooKeeperClient;
import com.broada.zookeeper.distributedlock.exception.ZkLockException;

public class DistributedLockTest extends TestCase {
	private ZooKeeperClient zkClient = null;

	protected void setUp() throws Exception {
		System.out.println("set up....");
		ZkServerAddresses zkServerAddresses = new ZkServerAddresses("192.168.16.88:2181");
		  zkClient = new ZooKeeperClient(zkServerAddresses,5000);
	}
	
	/**
	 * 测试锁
	 */
	@Test
	public void testLock(){
		System.out.println("execute testLock....");
		IDistributedLock  lock = new DistributedLockImpl(zkClient, "/config");
		lock.lock();
		System.out.println(lock.lockInfo());
	}
	
	
	/**
	 * 测试锁：同一个锁，锁资源两遍
	 * 结果：第一次锁能成功，第二次锁应该直接抛出异常。。
	 */
	@Test
	public void testLockTwice(){
		System.out.println("execute testLock....");
		IDistributedLock  lock = new DistributedLockImpl(zkClient, "/config");
		lock.lock();
		System.out.println(lock.lockInfo());
		try{
		lock.lock();//此处会有异常抛出
		}catch(ZkLockException e){
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 测试锁：同一个锁，锁资源两遍,再次请求锁的时候，先释放之前的锁
	 * 结果：两次次锁能成功
	 */
	@Test
	public void testLockRelease(){
		System.out.println("execute testLock....");
		IDistributedLock  lock = new DistributedLockImpl(zkClient, "/config");
		lock.lock();
		System.out.println(lock.lockInfo());
		lock.unlock();
		lock.lock();
	}
	
	/**
	 * 两个锁竞争同一资源
	 * 结果：第一次的锁成功，第二次的锁不成功
	 */
	public void testTwoLockCompetition(){
		System.out.println("execute testLock....");
		IDistributedLock  lock = new DistributedLockImpl(zkClient, "/config");
		lock.lock();
		lock.unlock();
		System.out.println(lock.lockInfo());
		IDistributedLock  lock1 = new DistributedLockImpl(zkClient, "/config");
		if(lock1.tryLock(1000))
			lock1.lock();
		System.out.println(lock1.lockInfo());
		}
	
	
	/**
	 * 多个锁竞争同一资源
	 */
	public void testNLockCompetition(){
		IDistributedLock  lock1 = new DistributedLockImpl(zkClient, "/config");
		IDistributedLock  lock2 = new DistributedLockImpl(zkClient, "/config");
		IDistributedLock  lock3 = new DistributedLockImpl(zkClient, "/config");
		IDistributedLock  lock4 = new DistributedLockImpl(zkClient, "/config");
		IDistributedLock  lock5 = new DistributedLockImpl(zkClient, "/config");
		IDistributedLock  lock6 = new DistributedLockImpl(zkClient, "/config");
		IDistributedLock  lock7 = new DistributedLockImpl(zkClient, "/config");
		IDistributedLock  lock8 = new DistributedLockImpl(zkClient, "/config");
		
		lock1.lock();
		System.out.println(lock1.lockInfo());
		lock1.unlock();
		
		lock2.lock();
		System.out.println(lock2.lockInfo());
		lock2.unlock();
		
		lock3.lock();
		System.out.println(lock3.lockInfo());
		lock3.unlock();
		
		lock4.lock();
		System.out.println(lock4.lockInfo());
		lock4.unlock();
		
		lock5.lock();
		System.out.println(lock5.lockInfo());
		lock5.unlock();
		
		lock6.lock();
		System.out.println(lock6.lockInfo());
		lock6.unlock();
		
		lock7.lock();
		System.out.println(lock7.lockInfo());
		lock7.unlock();
		
		lock8.lock();
		System.out.println(lock8.lockInfo());
		lock8.unlock();
		
	}
	
	
	
	/**
	 * 多个锁竞争同一资源
	 */
	public void testLockCompetition(){
		System.out.println("execute testLock....");
		int numOfThread = 9;
		Executor pool = Executors.newFixedThreadPool(numOfThread);
		for (int i = 0; i < numOfThread; i++) {
			pool.execute(new LockCompetition(new DistributedLockImpl(zkClient, "/config"),true ));
		}
	}

	private class LockCompetition implements Runnable{
		private IDistributedLock  lock;
		private boolean islock;
		public LockCompetition(IDistributedLock  lock,boolean islock){
			this.lock = lock;
			this.islock = islock;
		}
		public void run() {
			if(islock)
				this.lock.lock();
			else if(!this.lock.tryLock(1000))
				this.lock.unlock();
		}
	}
	
	
	  static
	    {
	        System.out.println("1");
	    }
	    {
	        System.out.println("2");
	    }
	    public DistributedLockTest()
	    {
	        System.err.println("3");
	    }
	    public static void main(String[] args)
	    {
	        new DistributedLockTest();
	    }
	
	
}

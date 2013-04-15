package com.broada.zookeeper.distributedlock.exception;


/**
 * @Title: ZkLockException.java
 * @Package com.broada.zookeeper.common
 * @Description: 获取锁出现异常
 * @company broada.com
 * @author wujing1
 * @mail wujing1@broada.com
 * @date 2013-3-30 下午3:35:10
 * @version V1.0
 */
public class ZkLockException extends RuntimeException {
    public ZkLockException(String msg, Exception e) {
	      super(msg, e);
	    }

	    public ZkLockException(String msg) {
	      super(msg);
	    }

}

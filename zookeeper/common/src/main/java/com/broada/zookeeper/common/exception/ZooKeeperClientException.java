package com.broada.zookeeper.common.exception;

/**   
 * @Title: ZooKeeperClientException.java 
 * @Package com.broada.zookeeper.common.exception 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @company broada.com
 * @author wujing1  
 * @mail wujing1@broada.com
 * @date 2013-3-29 ����5:43:51 
 * @version V1.0   
 */
public class ZooKeeperClientException extends Throwable {
	public ZooKeeperClientException(String msg, Exception e) {
	      super(msg, e);
	    }

	    public ZooKeeperClientException(String msg) {
	      super(msg);
	    }
	
}

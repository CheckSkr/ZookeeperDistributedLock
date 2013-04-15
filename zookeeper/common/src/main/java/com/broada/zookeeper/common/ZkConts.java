package com.broada.zookeeper.common;

/**   
 * @Title: ZkConts.java 
 * @Package com.broada.zookeeper.common 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @company broada.com
 * @author wujing1  
 * @mail wujing1@broada.com
 * @date 2013-3-29 下午5:28:46 
 * @version V1.0   
 */
public class ZkConts {
	/**
	 * 逗号英文
	 */
	public static final String COMMA =","; 
	/**
	 * 冒号英文
	 */
    public static final String  COLON = ":";
    /**
     * zk 服务器默认端口号
     */
    public static final int ZK_SERVER_DEFAULT_TPORT =2181;
    
    /**
     * 表示所有的zk znode数据的所有版本
     */
    public static final int ZK_ZNODE_DATA_ALLVERSION =-1;
    
    /**
     * 锁标记
     */
    public static final String ZK_BROADA_LOCK_MARKUP = "/broada.lock_";
    
	
}

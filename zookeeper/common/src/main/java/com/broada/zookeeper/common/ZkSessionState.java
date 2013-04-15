package com.broada.zookeeper.common;

/**
 * @Title: SessionState.java
 * @Package com.broada.zookeeper.common
 * @Description: zookeeper ��������Ϣ(sessionId �� sessionPasswd)
 * @company broada.com
 * @author wujing1
 * @mail wujing1@broada.com
 * @date 2013-3-29 ����5:08:19
 * @version V1.0
 */
public class ZkSessionState {

	private final long sessionId;
	private final byte[] sessionPasswd;

	public ZkSessionState(long sessionId, byte[] sessionPasswd) {
		this.sessionId = sessionId;
		this.sessionPasswd = sessionPasswd;
	}

	public long getSessionId() {
		return sessionId;
	}

	public byte[] getSessionPasswd() {
		return sessionPasswd;
	}

}

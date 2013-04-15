package com.broada.zookeeper.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.ACL;

public class ZookeeperUtil {

	/**
	 * ȷ�� ��zkClinet�� acl �� ����Path һ������, ��������ڣ���ֱ�Ӵ����ýڵ�
	 * 
	 * @param zkClient
	 * @param acl
	 * @param path
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public static void ensurePath(ZooKeeperClient zkClient, List<ACL> acls,
			String path) throws InterruptedException, KeeperException,
			TimeoutException, IOException {
		/**
		 * �û������п��ܴ���"/"�ڽ�β����ȥ��
		 */
		if (path.lastIndexOf('/') > 0)
			path = path.substring(0, path.lastIndexOf('/'));
		//����ڵ㲻���ڣ��򴴽�
		if (zkClient.get().exists(path, false) == null) {
			try {
				zkClient.get().create(path, null, acls, CreateMode.PERSISTENT);
			} catch (KeeperException.NodeExistsException e) {
			}
		}
	}
	
	/**
	 * ����������ǵĽڵ���˵��������ȴ��� ��Ids ��С��������
	 * @param currentedIds ĳ��lockPath �����е��ӽڵ㣬��������Ҫ��ֻ�� ��������ǵļ���
	 * @return ������� �ȴ���ȡ����Ids(������)
	 */
	public static List<String> getAllSortedLockIds(List<String> currentedIds){
		List<String> waitedIds  = new ArrayList<String>(currentedIds.size());
		for (String currentId : currentedIds) {
			int index = currentId.indexOf(ZkConts.ZK_BROADA_LOCK_MARKUP.substring(1));
			if(index >= 0){
				waitedIds.add(currentId.substring(index+ZkConts.ZK_BROADA_LOCK_MARKUP.substring(1).length()));
			}
		}
		Collections.sort(waitedIds);
		return waitedIds;
	}

}

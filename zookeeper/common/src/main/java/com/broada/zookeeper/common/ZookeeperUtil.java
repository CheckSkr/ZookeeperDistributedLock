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
	 * 确保 在zkClinet的 acl 下 ，该Path 一定存在, 如果不存在，则直接创建该节点
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
		 * 用户请求有可能存在"/"在结尾，先去掉
		 */
		if (path.lastIndexOf('/') > 0)
			path = path.substring(0, path.lastIndexOf('/'));
		//如果节点不存在，则创建
		if (zkClient.get().exists(path, false) == null) {
			try {
				zkClient.get().create(path, null, acls, CreateMode.PERSISTENT);
			} catch (KeeperException.NodeExistsException e) {
			}
		}
	}
	
	/**
	 * 将不含锁标记的节点过滤掉，并将等待的 锁Ids 从小到大排序
	 * @param currentedIds 某个lockPath 下所有的子节点，我们所需要的只是 含有锁标记的几点
	 * @return 已排序的 等待获取锁的Ids(已排序)
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

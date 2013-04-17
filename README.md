ZookeeperDistributedLock
========================

zookeeper distributed lock

A distributed lock implements with zookeeper.

The lock implement this in a very simple way.

if multi clients wants to change the particular znode say "/root/configuration", the all clients must write a temporary

Znode under the particular znode("/root/configuration") use the Zookeeper CreateMode.EPHEMERAL_SEQUENTIAL, so every client

has a temp znode sorted by time the visit the  particular znode, the lock always select the smallest znode,and give the lock
to that clinet.

Eaxmples:

if there are 3 clients want get the lock of "/root/configuration"

the 3 clients well create 3 temp Znode say they are :

broada.lock_0000000014 for client one;

broada.lock_0000000016 for client two;

broada.lock_0000000015 for client three;

the client one with get the lock (because the num of the temp Znode is the smallest mean that client one is the first
try to get the lock).

when client one released the lock the client three will obtain the lock.

pretty simple isn‘t it！！！

the next work:
1. the Configuration center used by zookeepr will fininshed in May.
2. the JUC will be finish in June,actually it was implemented by OpenUitility：https://github.com/qhwj2006/menagerie

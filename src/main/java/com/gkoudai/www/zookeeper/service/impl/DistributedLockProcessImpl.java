package com.gkoudai.www.zookeeper.service.impl;

import com.gkoudai.www.zookeeper.service.DistributedLockProcess;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Created by：Estranger
 * Description：通过zk实现分布式锁
 * Date：2021/3/1 11:25
 */
@Service("DistributedLockProcess")
public class DistributedLockProcessImpl implements DistributedLockProcess{

    private static Logger logger = LoggerFactory.getLogger(DistributedLockProcessImpl.class);

    private final String ZK_ROOT_LOCK = "/zkDistributed";

    private final CuratorFramework curatorFramework;

    @Autowired
    public DistributedLockProcessImpl(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    @PostConstruct
    public void init() {
        try {
            if (curatorFramework.checkExists().forPath(ZK_ROOT_LOCK) == null) {
                curatorFramework
                        .create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(ZK_ROOT_LOCK);

            }
            logger.info("ZooKeeper分布式锁根节点创建成功 path:{}", ZK_ROOT_LOCK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public InterProcessMutex tryLock(String key, int expireTime, TimeUnit timeUnit) {
        try {
            InterProcessMutex mutex = new InterProcessMutex(curatorFramework,String.format(ZK_ROOT_LOCK, key));
            boolean isLocked = mutex.acquire(expireTime, timeUnit);
            if(isLocked){
                logger.info("申请锁(" + key + ")成功" );
                return mutex;
            }
        } catch (Exception e) {
            logger.info("申请锁(" + key + ")失败" );
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void unLock(String key, InterProcessMutex lock) {
        try {
            // TODO: 2021/3/2 为什么release方法不会删除临时节点？ 断点发现它已经执行了releaseLock啊
            lock.release();
            logger.info("解锁(" + key + ")成功");
        } catch (Exception e) {
            logger.info("解锁(" + key + ")失败");
            e.printStackTrace();
        }
    }
}

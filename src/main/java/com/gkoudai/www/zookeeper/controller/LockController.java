package com.gkoudai.www.zookeeper.controller;

import com.gkoudai.www.zookeeper.service.DistributedLockProcess;
import com.gkoudai.www.zookeeper.service.impl.ClusterNodeProcessImpl;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by：Estranger
 * Description：
 * Date：2021/3/1 17:00
 */
@RestController
@RequestMapping("/lock")
public class LockController {

    private static Logger logger = LoggerFactory.getLogger(ClusterNodeProcessImpl.class);

    private final String ZK_LOCK_PREFIX = "lock";


    @Autowired
    private DistributedLockProcess distributedLockProcess;


    @RequestMapping("/testDistributedLock")
    public void testDistributedLock() {
        try {
            // 获取锁
            InterProcessMutex lock = distributedLockProcess.tryLock(ZK_LOCK_PREFIX, 10, TimeUnit.SECONDS);
            if (lock != null) {
                // 如果获取锁成功，则执行对应逻辑
                logger.info("获取分布式锁，执行对应逻辑...");
                // 释放锁
                distributedLockProcess.unLock(ZK_LOCK_PREFIX, lock);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}


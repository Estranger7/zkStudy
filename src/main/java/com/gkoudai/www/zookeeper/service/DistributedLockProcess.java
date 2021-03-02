package com.gkoudai.www.zookeeper.service;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit; /**
 * Created by：Estranger
 * Description：
 * Date：2021/3/1 17:47
 */
public interface DistributedLockProcess{
    /**
     * 加锁
     * @param key
     * @param expireTime
     * @param timeUnit
     * @return
     */
    InterProcessMutex tryLock(String key, int expireTime, TimeUnit timeUnit);


    /**
     * 释放锁
     * @param key
     * @param lock
     */
    void unLock(String key, InterProcessMutex lock);
}

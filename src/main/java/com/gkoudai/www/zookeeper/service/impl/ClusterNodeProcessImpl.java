package com.gkoudai.www.zookeeper.service.impl;

import com.alibaba.fastjson.JSON;
import com.gkoudai.www.zookeeper.service.ClusterNodeProcess;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by：Estranger
 * Description：
 * Date：2021/2/25 18:38
 */
@Service("ClusterNodeProcess")
public class ClusterNodeProcessImpl implements ClusterNodeProcess {
    private static Logger logger = LoggerFactory.getLogger(ClusterNodeProcessImpl.class);

    private final String ZOO_KEEPER_ROOT_URL = "/QuoteConfigCluster";

    private final String ZOO_KEEPER_SERVER_NODE_NAME_PREFIX = "server";

    private String currentNodePath;

    List<String> currentNodeList;

    private final CuratorFramework curatorFramework;

    @Autowired
    public ClusterNodeProcessImpl(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }


    @PostConstruct
    public void init() {
        try {
            // 判断根节点是否存在
            if (null == curatorFramework.checkExists().forPath(ZOO_KEEPER_ROOT_URL)) {
                // 创建一个持久的其他所有服务都具有所有权限的节点
                curatorFramework
                        .create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(ZOO_KEEPER_ROOT_URL);

                logger.info("ZooKeeper集群根节点创建成功 path:{}", ZOO_KEEPER_ROOT_URL);
            }

            // 创建一个短命、带有自增长序列的节点
            currentNodePath = curatorFramework
                    .create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(ZOO_KEEPER_ROOT_URL + "/" + ZOO_KEEPER_SERVER_NODE_NAME_PREFIX);
            logger.info("ZooKeeper集群节点创建成功 path:{}", currentNodePath);

            //监听方法
            watchNode();


        } catch (Exception e) {
            logger.error("ZooKeeper集群节点初始化创建异常_" + e.getMessage(), e);
        }
    }

    @Override
    public boolean isMaster() {
        if (currentNodePath == null || CollectionUtils.isEmpty(currentNodeList)) {
            return true;
        }
        //判断当前节点是否是最小的(最早创建的)
        return currentNodeList.indexOf(currentNodePath) == 0;
    }

    private void watchNode() {
        try {
            List<String> newCurrentList = new ArrayList<>();
            //创建监听事件
            List<String> childrenList =
                    curatorFramework.getChildren()
                            .usingWatcher((Watcher) watchedEvent -> {
                                if (Objects.equals(watchedEvent.getType(), Watcher.Event.EventType.NodeChildrenChanged)) {//watcher监听的数据节点的子节点列表发生变更（通过create、delete触发）
                                    logger.info("监听到节点事件：" + JSON.toJSONString(watchedEvent));
                                    //重新设置监听事件
                                    watchNode();
                                }
                            }).forPath(ZOO_KEEPER_ROOT_URL);

            if(!CollectionUtils.isEmpty(childrenList)) {
                for (String child :
                        childrenList) {
                    newCurrentList.add(ZOO_KEEPER_ROOT_URL + "/" + child);
                }
            }
            //server0000000456，将server截取，取后面的数字从小到大对注册上zookeeper的应用服务器节点进行排序
            currentNodeList = newCurrentList.stream().sorted(Comparator.comparing(s -> s.substring(ZOO_KEEPER_SERVER_NODE_NAME_PREFIX.length()))).collect(Collectors.toList());
            if (logger.isInfoEnabled()) {
                logger.info("集群节点信息刷新 最新节点信息:{}", currentNodeList);
            }

        } catch (Exception e) {
            logger.error("ZooKeeper集群节点信息刷新异常_" + e.getMessage(), e);
        }
    }

}

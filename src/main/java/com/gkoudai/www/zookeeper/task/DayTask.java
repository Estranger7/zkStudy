package com.gkoudai.www.zookeeper.task;

import com.gkoudai.www.zookeeper.service.ClusterNodeProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by：Estranger
 * Description：
 * Date：2021/2/25 20:31
 */
@Component
public class DayTask {

    private static Logger logger = LoggerFactory.getLogger(DayTask.class);

    private final ClusterNodeProcess clusterNodeProcess;

    @Autowired
    public DayTask(ClusterNodeProcess clusterNodeProcess) {
        this.clusterNodeProcess = clusterNodeProcess;
    }

    @Scheduled(cron = "0 05 10 * * ?")
    public void testTask(){
        if(clusterNodeProcess.isMaster()){
            logger.info("只有我能执行");
        }
    }
}

本项目演示了通过curator客户端操作zookeeper节点的一些示例

## 场景
### 定时任务重复执行问题
&nbsp;&nbsp;&nbsp;&nbsp;项目部署多个节点时，在执行定时任务时会被多次执行，当然我们可以指定某个ip的节点执行，或者使用redis setnx来处理此类问题。      
&nbsp;&nbsp;&nbsp;&nbsp;本项目通过zookeeper实现了这一功能，通过对某一父节点下的子节点进行监听，当有新的节点连接上zookeeper(即新节点被部署了)，       
会创建一个序列自增的临时节点，触发watcher，获取到所有子节点，按照自增序列进行排序。定时任务每次执行前，判断当前节点是否是        
最老的节点，是则执行逻辑。如果节点没有太多的变化，则类似于指定了某台机器来执行。 

### 分布式锁
&nbsp;&nbsp;&nbsp;&nbsp;原生的zookeeper和curatorFramework操作watcher时，需要反复注册；并且当断开重连后，watcher也需要重新注册，
不方便。因此，这里使用curator recipes中的相关cache类来操作

package com.tang.case2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Classname DistributedLock
 * @Description TODO
 * @Date 2022/6/21 07:11
 * @Author by tangyao
 */

public class DistributedLock {

    private final ZooKeeper zooKeeper;
    private final String rootPath = "/locks";
    public String connectString = "test1:2181,test2:2181,test3:2181,test4:2181,test5:2181";

    public int sessionTimeout = 200000;

    private CountDownLatch connectLatch = new CountDownLatch(1);
    private CountDownLatch waitLatch = new CountDownLatch(1);

    private String waitPath = "";
    private String currentMode;


    public DistributedLock() throws IOException, InterruptedException, KeeperException {
        // 获取连接
        // 判断根节点/locks 是否存在

        zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // connectLatch 如果连接上zk，可以释放
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    connectLatch.countDown();
                }
                //waitLatch 需要释放
                if (event.getType() == Event.EventType.NodeDeleted
                        &&
                        event.getPath().equals(waitPath)) {
                    waitLatch.countDown();
                }
            }
        });
        // 等待zk正常连接后，往下走程序
        connectLatch.await();

        // 判断节点是否存在


        Stat stat = zooKeeper.exists(rootPath, false);

        if (stat == null) {
            // 创建一下根节点
            String s = zooKeeper.create(
                    "/locks",
                    "locks".getBytes(StandardCharsets.UTF_8),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
            System.out.println("s = " + s);
        }


    }

    public void zkLock() {
        // 创建对应的临时带序号节点
        try {
            currentMode = zooKeeper.create("/locks/" + "seq-",
                    null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);

            // 判断创建的节点是否是最小的序号节点，如果是获取到锁，如果不是，监听他序号前一个节点

            List<String> children = zooKeeper.getChildren(rootPath, false);

            //如果children 只有一个值，那就直接获取锁，如果有多个节点，需要判断谁最小
            if (children.size() == 1) {
                return;
            } else {
                Collections.sort(children);

                // 获取节点名称 seq-00000000

                String thisNode = currentMode.substring("/locks/".length());
                // 通过seq-00000000 获取该节点在children集合的位置
                int index = children.indexOf(thisNode);
                if (index == -1) {
                    System.out.println("数据异常");
                } else if (index == 0) {
                    // 就一个节点，可以获取锁了
                    return;
                } else {
                    //需要监听他前一个节点
                    waitPath = "/locks/" + children.get(index - 1);
                    zooKeeper.getData(waitPath, true, new Stat());
                    // 等待监听
                    waitLatch.await();
                    return;
                }


            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void unZkLock() {
        // 删除节点
        try {
            zooKeeper.delete(currentMode, -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}

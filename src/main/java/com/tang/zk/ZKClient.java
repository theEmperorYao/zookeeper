package com.tang.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Classname ZkClient
 * @Description TODO
 * @Date 2022/6/18 23:22
 * @Author by tangyao
 */

public class ZKClient {

    /**
     * 逗号左右不能有空格
     */
    public String connectString = "test1:2181,test2:2181,test3:2181,test4:2181,test5:2181";

    public int sessionTimeout = 2000;
    private ZooKeeper zkClient;

    @Before
    public void init() throws IOException {

        // 升级为成员变量 ctrl  + alt + f
        // watcher 注册一次生效一次
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

                System.out.println("===================");
                List<String> children = null;
                try {
                    children = zkClient.getChildren("/", true);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (String child : children) {
                    System.out.println(child);
                }

            }
        });
    }

    @Test
    public void create() throws InterruptedException, KeeperException {

        String nodeCreated = zkClient.create(
                "/tang",
                "yao".getBytes(StandardCharsets.UTF_8),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);

        System.out.println("nodeCreated = " + nodeCreated);

    }

    @Test
    public void getChildren() throws InterruptedException, KeeperException {

        List<String> children = zkClient.getChildren("/", true);

        for (String child : children) {
            System.out.println(child);
        }

        // 延时
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void exist() throws InterruptedException, KeeperException {
        Stat stat = zkClient.exists("/tang6", false);
        String s = stat == null ? "not exist" : "exist";
        System.out.println(s);

    }

}

package com.tang.case1;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @Classname DistributeServer
 * @Description TODO
 * @Date 2022/6/19 15:41
 * @Author by tangyao
 */

public class DistributeServer {


    public String connectString = "test1:2181,test2:2181,test3:2181,test4:2181,test5:2181";

    public int sessionTimeout = 2000;
    private ZooKeeper zkClient;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        DistributeServer server = new DistributeServer();
        // 1、获取zk连接
        server.geConnect();
        // 2、注册服务器到zk集群
        server.register(args[0]);
        // 3、启动业务逻辑（睡觉）
        server.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void register(String hostname) throws InterruptedException, KeeperException {

        String s = zkClient.create(
                "/servers/" + hostname,
                hostname.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println(hostname + "is online");
    }

    private void geConnect() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {

            }
        });
    }


}

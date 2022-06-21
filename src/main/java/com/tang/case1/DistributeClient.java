package com.tang.case1;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname DistributeClient
 * @Description TODO
 * @Date 2022/6/19 15:51
 * @Author by tangyao
 */
public class DistributeClient {


    public String connectString = "test1:2181,test2:2181,test3:2181,test4:2181,test5:2181";

    public int sessionTimeout = 2000;
    private ZooKeeper zk;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {


        DistributeClient distributeClient = new DistributeClient();
        // 1、获取zk连接
        distributeClient.geConnect();
        //2、监听/servers 下面的子节点增加和删除
        distributeClient.getServerList();
        //3、业务逻辑（睡觉）
        distributeClient.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void getServerList() throws InterruptedException, KeeperException {

        List<String> children = zk.getChildren("/servers", true);


        List<String> collect = children.stream().map(child -> {
            System.out.println(child);
            byte[] data = null;
            try {
                data = zk.getData("/servers/" + child, false, null);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
            assert data != null;
            return new String(data);
        }).collect(Collectors.toList());

        System.out.println(collect);

    }

    private void geConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    getServerList();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

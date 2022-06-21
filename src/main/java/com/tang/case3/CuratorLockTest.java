package com.tang.case3;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Classname CuratorLockTest
 * @Description TODO
 * @Date 2022/6/21 23:19
 * @Author by tangyao
 */
public class CuratorLockTest {

    public static void main(String[] args) {
        //1、创建分布式锁1
        InterProcessMutex lock1 = new InterProcessMutex(getCuratorFramework(), "/locks");

        //2、创建分布式锁2
        InterProcessMutex lock2 = new InterProcessMutex(getCuratorFramework(), "/locks");

        new Thread(() -> {
            try {
                lock1.acquire();
                String name = Thread.currentThread().getName();
                System.out.println(name + " 获取到锁");
                lock1.acquire();
                System.out.println(name + " 再次获取到锁");
                Thread.sleep(5000);
                lock1.release();
                System.out.println(name + " 释放锁");
                lock1.release();
                System.out.println(name + " 再次释放锁");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, "A").start();

        new Thread(() -> {
            try {
                lock2.acquire();
                String name = Thread.currentThread().getName();
                System.out.println(name + " 获取到锁");
                lock2.acquire();
                System.out.println(name + " 再次获取到锁");
                Thread.sleep(5000);
                lock2.release();
                System.out.println(name + " 释放锁");
                lock2.release();
                System.out.println(name + " 再次释放锁");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, "B").start();


    }

    private static CuratorFramework getCuratorFramework() {

        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(3000, 3);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("test1:2181,test2:2181,test3:2181,test4:2181,test5:2181")
                .connectionTimeoutMs(5000)
                .retryPolicy(retry).build();
        client.start();
        System.out.println("zookeeper 启动成功");

        return client;
    }
}

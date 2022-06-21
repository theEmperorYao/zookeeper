package com.tang.case2;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * @Classname DistributedLockTest
 * @Description TODO
 * @Date 2022/6/21 07:46
 * @Author by tangyao
 */
@Slf4j
public class DistributedLockTest {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        DistributedLock lock1 = new DistributedLock();
        DistributedLock lock2 = new DistributedLock();

        new Thread(() -> {
            try {
                lock1.zkLock();
                log.info(Thread.currentThread().getName() + "启动，获取到锁");
                Thread.sleep(5000);
                lock1.unZkLock();
                log.info(Thread.currentThread().getName() + " 释放锁");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "A").start();


        new Thread(() -> {
            try {
                 lock2.zkLock();
                log.info(Thread.currentThread().getName() + " 启动，获取到锁");
                Thread.sleep(5000);
                lock2.unZkLock();
                log.info(Thread.currentThread().getName() + "释放锁");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "B").start();



    }
}

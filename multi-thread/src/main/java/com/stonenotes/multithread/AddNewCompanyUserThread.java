package com.stonenotes.multithread;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author: javan
 * @description ${description}
 * @date: 2019/8/16
 */
public class AddNewCompanyUserThread implements Callable<Boolean> {
    /**
     * 主线程监控
     */
    private CountDownLatch mainLatch;
    /**
     * 子线程监控
     */
    private CountDownLatch threadLatch;
    /**
     * 是否回滚
     */
    private RollBack rollBack;
    private int taskId;

    public AddNewCompanyUserThread(int taskId, CountDownLatch mainLatch, CountDownLatch threadLatch, RollBack rollBack) {
        this.taskId = taskId;
        this.mainLatch = mainLatch;
        this.threadLatch = threadLatch;
        this.rollBack = rollBack;
    }

    @Override
    public Boolean call() throws Exception {
        //为了保证事务不提交，此处只能调用一个有事务的方法，spring 中事务的颗粒度是方法，只有方法不退出，事务才不会提交
        return batchInsert();
    }

    private Boolean batchInsert() throws Exception {
        Boolean result = false;
        System.out.println("线程: {}创建参保人条数 : {}" + Thread.currentThread().getName());
        try {
            Thread.sleep(3000);
            //Exception 和 Error 都需要抓
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.out.println("线程: {}创建参保人出现异常： {} " + Thread.currentThread().getName() + ", " + throwable.getMessage());
            result = true;
        }
        threadLatch.countDown();
        System.out.println("子线程 {} 计算过程已经结束，等待主线程通知是否需要回滚 " + Thread.currentThread().getName());

        try {
            mainLatch.await();
            System.out.println("子线程 {} 再次启动 " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            System.out.println("批量创建参保人线程InterruptedException异常");
            throw new Exception("批量创建参保人线程InterruptedException异常");
        }

        if (rollBack.getRollBack()) {
            System.out.println("批量创建参保人线程回滚, 线程: {}, 需要更新的信息taskList: {}" +
                    Thread.currentThread().getName());
            System.out.println("子线程 {} 执行完毕，线程退出" + Thread.currentThread().getName());
            throw new Exception("批量创建参保人线程回滚");
        }
        System.out.println("子线程 {} 执行完毕，线程退出 " + Thread.currentThread().getName());
        return result;
    }
}

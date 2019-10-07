package com.stonenotes.multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class Main {
    public static void main(String[] args) throws Exception {
        //线程池的最大线程数
        int nThreads = 10;
        CountDownLatch mainLatch = new CountDownLatch(1);
        //监控子线程
        CountDownLatch threadLatch = new CountDownLatch(nThreads);
        //根据子线程执行结果判断是否需要回滚
        BlockingDeque<Boolean> resultList = new LinkedBlockingDeque<>(nThreads);
        //必须要使用对象，如果使用变量会造成线程之间不可共享变量值
        RollBack rollBack = new RollBack(false);
        ExecutorService fixedThreadPool = newFixedThreadPool(10);

        List<Future<Boolean>> futures = new ArrayList<>();
        List<Boolean> returnDataList = new ArrayList<>();
        //给每个线程分配任务
        for (int i = 0; i < nThreads; i++) {
            AddNewCompanyUserThread addNewCompanyUserThread = new AddNewCompanyUserThread(i, mainLatch, threadLatch, rollBack);
            Future<Boolean> future = fixedThreadPool.submit(addNewCompanyUserThread);
            futures.add(future);
        }
        /** 存放子线程返回结果. */
        List<Boolean> backUpResult = new ArrayList<>();
        try {
            //等待所有子线程执行完毕
            boolean await = threadLatch.await(20, TimeUnit.SECONDS);
            //如果超时，直接回滚
            if (!await) {
                rollBack.setRollBack(true);
            } else {
                System.out.println(("创建参保人子线程执行完毕，共 {} 个线程 " + nThreads));
                //查看执行情况，如果有存在需要回滚的线程，则全部回滚
                for (int i = 0; i < nThreads; i++) {
                    Boolean result = resultList.take();
                    backUpResult.add(result);
                    System.out.println("子线程返回结果result: {}" + result);
                    if (result) {
                        /** 有线程执行异常，需要回滚子线程. */
                        rollBack.setRollBack(true);
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("等待所有子线程执行完毕时，出现异常");
            throw new Exception("等待所有子线程执行完毕时，出现异常，整体回滚");
        } finally {
            //子线程再次开始执行
            mainLatch.countDown();
            System.out.println("关闭线程池，释放资源");
            fixedThreadPool.shutdown();
        }

        /** 检查子线程是否有异常，有异常整体回滚. */
        for (int i = 0; i < nThreads; i++) {
            if (backUpResult != null && backUpResult.size() > 0) {
                Boolean result = backUpResult.get(i);
                if (result) {
                    System.out.println("创建参保人失败，整体回滚");
                    throw new Exception("创建参保人失败");
                }
            } else {
                System.out.println("创建参保人失败，整体回滚");
                throw new Exception("创建参保人失败");
            }
        }

        //拼接结果
        try {
            for (Future<Boolean> future : futures) {
                returnDataList.add(future.get());
            }
        } catch (Exception e) {
            System.out.println("获取子线程操作结果出现异常,创建的参保人列表： {} ，异常信息： {}" + e.getMessage());
            throw new Exception("创建参保人子线程正常创建参保人成功，主线程出现异常，回滚失败");
        }
    }
}

package com.stonenotes.multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * @author: javan
 * @description ${description}
 * @date: 2019/8/16
 */
public class CountDownLatchTest {
    public static void main(String[] args) throws Exception {
        long lastTime = System.currentTimeMillis();
        int count = 10;
        CountDownLatch countDownLatch = new CountDownLatch(10);
        ExecutorService threadPool = newFixedThreadPool(4);
        List<WorkerFuture> resultList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Future<WorkerFuture> result = threadPool.submit(new MyTask(countDownLatch, i));
            resultList.add(result.get());
        }
        System.out.println("--------------" + resultList.size());
        countDownLatch.await();
        resultList.forEach(item-> System.out.println("---------" + item.toString()));
        System.out.println("----testTime: " + (System.currentTimeMillis() - lastTime));
    }

    static class MyTask implements Callable<WorkerFuture>{

        private CountDownLatch countDownLatch;
        private int taskId;

        public MyTask(CountDownLatch countDownLatch, int taskId) {
            this.countDownLatch = countDownLatch;
            this.taskId = taskId;
        }

        @Override
        public WorkerFuture call(){
            return doWorker();
        }

        public WorkerFuture doWorker(){
            System.out.println("执行线程：" + Thread.currentThread().getName() + ", taskId: " + taskId + " 开始!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
            System.out.println("执行线程：" + Thread.currentThread().getName() + ", taskId: " + taskId + " 完成!" + countDownLatch.getCount());
            WorkerFuture future = new WorkerFuture();
            future.setResult(true);
            future.setTaskId(taskId);
            future.setThreadName(Thread.currentThread().getName());
            return future;
        }
    }

    static class WorkerFuture {

        private Boolean result;
        private String threadName;
        private int taskId;

        public Boolean getResult() {
            return result;
        }

        public void setResult(Boolean result) {
            this.result = result;
        }

        public String getThreadName() {
            return threadName;
        }

        public void setThreadName(String threadName) {
            this.threadName = threadName;
        }

        public int getTaskId() {
            return taskId;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public String toString() {
            return "WorkerFuture{" +
                    "result=" + result +
                    ", threadName='" + threadName + '\'' +
                    ", taskId=" + taskId +
                    '}';
        }
    }
}

package com.stonenotes.postgresql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: javan
 * @Date: 2019/7/23
 */
public class PostgreSQLApplication {

    private static final long DATA_SIZE = 560000;
    private static final int BATCH_SIZE = 50000;

    private static int count;
    private static int times;
    private static Connection connection;

    public static void main(String[] args) throws Exception {
        List<Period> periodList = new ArrayList<Period>();
        for (int i = 0; i < DATA_SIZE; i++) {
            Period period = new Period();
            period.setPeriod(getPeriod((i + 1) + ""));
            period.setStartTime(new Date(System.currentTimeMillis()));
            period.setEndTime(new Date(System.currentTimeMillis()));
            period.setLotteryCode("BJSC");
            period.setSettleStatus(0);
            period.setStatus(0);
            period.setCreateTime(new Date(System.currentTimeMillis()));
            period.setCreateUser("root");
            periodList.add(period);
        }
        //batchInsert(periodList);
        batchInsertPeriodMultithread(periodList);
    }

    private static String getPeriod(String period) {
        StringBuilder sb = new StringBuilder();
        for (int i = period.length(); i < 7; i++) {
            sb.append("0");
        }
        return sb.toString() + period;
    }

    private static Connection getConnection() throws Exception {
//        if (connection != null) {
//            return connection;
//        }
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
        return connection;
    }

    public static synchronized void increCount() {
        count ++;
        System.out.println("-------------" + count);
    }

    private static boolean createTable() {
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = getConnection();
            System.out.println("Opened database successfully");
            stmt = connection.createStatement();
            String sql = "CREATE TABLE COMPANY " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " AGE            INT     NOT NULL, " +
                    " ADDRESS        CHAR(50), " +
                    " SALARY         REAL)";
            stmt.executeUpdate(sql);
            stmt.close();
            connection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
        System.out.println("Table created successfully");
        return true;
    }

    private static boolean batchInsert(List<Period> periodList) throws Exception {
        Connection conn = getConnection();
        Long startTime = System.currentTimeMillis();
        PreparedStatement pstmt = null;
        String sql = "insert into pl_lottery_period(lottery_code,period,start_time,end_time,status,settle_status,create_time,create_user) " +
                "values(?,?,?,?,?,?,?,?)";
        try {
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            for (int i = 1; i <= DATA_SIZE; i++) {
                Period period = periodList.get(i-1);
                pstmt.setString(1, period.getLotteryCode());
                pstmt.setString(2, period.getPeriod());
                pstmt.setDate(3, period.getStartTime());
                pstmt.setDate(4, period.getEndTime());
                pstmt.setInt(5, period.getStatus());
                pstmt.setInt(6, period.getSettleStatus());
                pstmt.setDate(7, period.getCreateTime());
                pstmt.setString(8, period.getCreateUser());
                pstmt.addBatch();
                if (i % BATCH_SIZE == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
            pstmt.executeBatch();
            pstmt.clearBatch();
            conn.commit();
            long end = System.currentTimeMillis(); //获取运行结束时间
            System.out.println("程序运行时间： " + (end - startTime) + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private static void batchInsertPeriodMultithread(List<Period> periodList) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        long lastTime = System.currentTimeMillis();
        int time = periodList.size() / BATCH_SIZE;
        times = time;
        for (int i = 0; i <= time; i++) {
            List<Period> periods;
            if (i == time) {
                periods = periodList.subList(i * BATCH_SIZE, periodList.size());
            } else {
                periods = periodList.subList(i * BATCH_SIZE, (i + 1) * BATCH_SIZE);
            }
            System.out.println("-------" + i + "----" + periods.size() + "---" + times);
            // new Thread(new BatchInsertTask(periods)).start();
            executorService.execute(new BatchInsertTask(periods));
        }
        while (true) {
            Thread.sleep(10);
            if (count - 1 == times) {
                System.out.println("------test time--" + (System.currentTimeMillis() - lastTime));
                executorService.shutdown();
                break;
            }
        }
    }

    private static class BatchInsertTask implements Runnable{
        List<Period> periodList;

        public BatchInsertTask(List<Period> periodList){
            this.periodList = periodList;
        }

        @Override
        public void run() {
            try {
                batchInsert();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void batchInsert() throws Exception {
            Connection conn = getConnection();
            Long startTime = System.currentTimeMillis();

            String sql = "insert into pl_lottery_period(lottery_code,period,start_time,end_time,status,settle_status,create_time,create_user) " +
                    "values(?,?,?,?,?,?,?,?)";
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < periodList.size(); i++) {
                Period period = periodList.get(i);
                pstmt.setString(1, period.getLotteryCode());
                pstmt.setString(2, period.getPeriod());
                pstmt.setDate(3, period.getStartTime());
                pstmt.setDate(4, period.getEndTime());
                pstmt.setInt(5, period.getStatus());
                pstmt.setInt(6, period.getSettleStatus());
                pstmt.setDate(7, period.getCreateTime());
                pstmt.setString(8, period.getCreateUser());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            pstmt.clearBatch();
            conn.commit();
            long end = System.currentTimeMillis(); //获取运行结束时间
            System.out.println(Thread.currentThread().getName() + "-----程序运行时间： " + (end - startTime) + "ms");
            increCount();
        }
    }
}

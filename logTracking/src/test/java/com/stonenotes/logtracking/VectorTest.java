package com.stonenotes.logtracking;

import org.jboss.logging.NDC;

import java.util.*;

/**
 * @Author: javan
 * @Date: 2019/6/21
 */
public class VectorTest {
    private static Vector<String> vector = new Vector<>();

    public static void main(String[] args) {
//        producer();
//        consumer();
        try {
            int a = 5/0;
            System.out.println(a);
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + " " + e.getLocalizedMessage());
        }
    }

    private static void producer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("producer: " + vector.size());
                vector.add("produce" + new Random().nextInt(1000));
            }
        }, 0, 50);
    }

    private static void consumer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                long lastTime = System.currentTimeMillis();
                System.out.println("start: " + vector.size());
                Iterator<String> iterator = vector.iterator();
                while (iterator.hasNext()) {
                    String item = iterator.next();
                    System.out.println(item);
                    iterator.remove();
                }
                System.out.println("finish: " + vector.size()+"---" + (System.currentTimeMillis() - lastTime));
            }
        }, 2000, 60 * 1000);
    }


}

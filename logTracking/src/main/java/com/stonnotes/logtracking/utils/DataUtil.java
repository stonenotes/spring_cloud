package com.stonnotes.logtracking.utils;

import com.stonnotes.logtracking.pojo.LogInfo;

import java.util.Random;
import java.util.Vector;

/**
 * @Author: javan
 * @Date: 2019/6/21
 */
public class DataUtil {

    private static final String mWordData = "Every major and minor release of the Elastic Stack introduces new and powerful features, as well as important bug fixes and enhancements to existing features. At the same time, the Elastic team is constantly striving to improve performance, stability, and security.\n" +
            "\n" +
            "While upgrading requires an investment of your time and resources, it enables you to benefit from these improvements and opens the door to using the stack in new and better ways. You can minimize the impacts of upgrading by scheduling regular upgrades to ensure that you are running actively supported versions of Elasticsearch, Kibana, Logstash, and Beats.\n" +
            "\n" +
            "The longer you wait between upgrades, the more difficult the upgrade process becomes. If you lag behind more than one major release, when you do upgrade you’ll face full cluster restarts or reindexing, as well as a more significant development effort to migrate your applications."
            + "A single click in the Elastic Cloud console can upgrade a cluster to a newer version, add more processing capacity, change plugins, and enable or disable high availability, all at the same time. During the upgrade process, Elasticsearch, Kibana, X-Pack and the officially included plugins are upgraded simultaneously.\n" +
            "\n" +
            "Although upgrading your Elastic Cloud clusters is easy, you still need to address breaking changes that affect your application. Minor version upgrades, upgrades from 6.8 to 7.1.1, and all other cluster configuration changes can be performed with no downtime.\n" +
            "\n" +
            "To avoid downtime when a full cluster restart is required:\n" +
            "\n" +
            "Provision an additional cluster with the new Elasticsearch version, reindex your data, and send index requests to both clusters temporarily.\n" +
            "Verify that the new cluster performs as expected, fix any problems, and then permanently swap in the new cluster.\n" +
            "Delete the old cluster to stop incurring additional costs. You are billed only for the time that the new cluster runs in parallel with your old cluster. Usage is billed on an hourly basis."
            ;

    private static String[] wordArray;// 随机单词器
    private static Vector<LogInfo> logInfoVector; // 缓存log对象

    static {
        wordArray = mWordData.split(" ");
        logInfoVector = new Vector<>();
    }

    public static String getRandomMessage() {
        return wordArray[new Random().nextInt(1000) % wordArray.length] + " " + wordArray[new Random().nextInt(100) % wordArray.length]
                + " " + wordArray[new Random().nextInt(10) % wordArray.length];
    }

    public static Vector<LogInfo> getLogInfoVector() {
        return logInfoVector;
    }
}

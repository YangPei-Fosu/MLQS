package io.mlqs.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class LogUtils {
    private static boolean debug = false;
    private static boolean report = false;
    @Autowired
    public void setDebugFlag(@Value("${log.debug}") boolean debugFlag, @Value("${log.report}") boolean reportFlag) {
        LogUtils.debug = debugFlag;
        LogUtils.report = reportFlag;
    }

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";

    //输出格式：时间 [类名] 消息
    public static void log(Class<?> clazz, String message) {
        System.out.println(new Timestamp(System.currentTimeMillis()) + GREEN + " INFO" + RESET + " [" + clazz.getName() + "] " + message);
    }

    //Debug
    public static void debug(Class<?> clazz, String message) {
        if(debug)
            System.out.println(new Timestamp(System.currentTimeMillis()) + BLUE + " DEBUG " + RESET + " [" + clazz.getName() + "] " + message);
    }

    //监控
    public static void report(Class<?> clazz, String reporter, String message) {
        if(report)
            System.out.println(new Timestamp(System.currentTimeMillis()) + BLUE + " "+ reporter +" " + RESET + " [" + clazz.getName() + "] " + message);
    }
}

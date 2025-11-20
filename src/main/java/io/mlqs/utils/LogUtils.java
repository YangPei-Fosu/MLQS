package io.mlqs.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class LogUtils {
    private static boolean debug = false;
    private static boolean report = false;
    @Autowired
    public void setDebugFlag(@Value("${mlqs.log.debug}") boolean debugFlag, @Value("${mlqs.log.report}") boolean reportFlag) {
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

    public static void debug(Class<?> clazz, String str , Object... args) {
        if(debug) {
            System.out.print(new Timestamp(System.currentTimeMillis()) + BLUE + " DEBUG " + RESET + " [" + clazz.getName() + "] ");
            System.out.println(String.format(str, args));
        }
    }

    //监控
    public static void report(Class<?> clazz, String reporter, String message) {
        if(report)
            System.out.println(new Timestamp(System.currentTimeMillis()) + BLUE + " REPORT ["+ reporter +"] " + RESET + " [" + clazz.getName() + "] " + message);
    }

    //进度条
    @Data
    public static class Bar{
        private static int f;
        private static int t;
        private static Class<?> c;
        private static String m;

        public Bar(int form, int to, Class<?> clazz, String message) {
            this.f = form;
            this.t = to;
            this.c = clazz;
            this.m = message;
        }

        public void start(){
            if(f == t)
                System.out.println(new Timestamp(System.currentTimeMillis()) + GREEN + " PROGRESS " + RESET + " [" + c.getName() + "] " + m + " [" + f + "/" + t + "]");
            else
                System.out.print(new Timestamp(System.currentTimeMillis()) + GREEN + " PROGRESS " + RESET + " [" + c.getName() + "] " + m + " [" + f + "/" + t + "]");
        }
        public void update(int i){
            f++;
            //覆盖上一条输出
            if(f < t){
                System.out.print("\u001B[2K\r");
                System.out.print(new Timestamp(System.currentTimeMillis()) + GREEN + " PROGRESS " + RESET + " [" + c.getName() + "] " + m + " [" + f + "/" + t + "]");
            }
            if(f >= t)
                System.out.println();
        }
    }
    public static Bar progress(Class<?> clazz, String message , int form, int to) {
        return new Bar(form, to ,clazz ,message);
    }
}

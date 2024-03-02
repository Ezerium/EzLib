package com.ezerium;

import com.ezerium.annotations.Async;
import com.ezerium.annotations.Cache;
import com.ezerium.annotations.Debug;
import com.ezerium.annotations.Timer;
import com.ezerium.annotations.config.Configuration;
import com.ezerium.annotations.test.GeneratedTest;

/**
 * Main class is mainly for testing purposes.
 */
@Configuration("main")
public class Main {

    public String test = "abc";

    public static void main(String[] args) {
        //ConfigHandler configHandler = new ConfigHandler();

        Main main = new Main();
        //configHandler.registerConfig(Main.class, main);

        main.test();
        main.test2("abc");
        main.test2("def");
        main.test2("def");

        //System.out.println(CacheUtils.cache);
    }

    @Debug(logOnCall = "test {this.test} aa {this.test} {test}")
    @GeneratedTest
    public void test() {

    }

    @Timer
    @Cache(cacheByParameter = "abc")
    public String test2(String abc) {
        String idk = "hello";
        if (abc.equals("abc")) {
            return "abcdefg";
        }

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return abc + idk;
    }

    @Async
    public String test3() {
        System.out.println("test3");
        return "test3";
    }

}

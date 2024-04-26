package com.ezerium;

import com.ezerium.annotations.Async;
import com.ezerium.annotations.Cache;
import com.ezerium.annotations.Debug;
import com.ezerium.annotations.Timer;
import com.ezerium.annotations.config.Configuration;
import com.ezerium.annotations.test.GeneratedTest;
import com.ezerium.logger.debug.DebugAt;

/**
 * Main class is mainly for testing purposes.
 */
@Configuration("main")
public final class Main {

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

    //@Debug(logOnCall = "test {abc}", debugAt = DebugAt.AUTO)
    @GeneratedTest
    public void test() {
        String abc = "abc";
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

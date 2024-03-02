package com.ezerium;

import com.ezerium.annotations.Cache;
import com.ezerium.annotations.Debug;
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
    }

    @Debug(logOnCall = "test {this.test} aa {this.test} {test}")
    @GeneratedTest
    public void test() {

    }

    @Cache(cacheByParameter = "abc")
    public String test2(String abc) {
        String idk = "hello";
        if (abc.equals("abc")) {
            return "abcdefg";
        }

        return abc + idk;
    }

}

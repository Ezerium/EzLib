package com.ezerium;

import com.ezerium.shared.annotations.Debug;
import com.ezerium.shared.annotations.config.Configuration;
import com.ezerium.shared.annotations.test.GeneratedTest;
import com.ezerium.shared.config.ConfigHandler;

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
    }

    @Debug(logOnCall = "test {this.test} aa {this.test} {test}")
    @GeneratedTest
    public void test() {

    }

}

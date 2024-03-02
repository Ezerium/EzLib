package com.ezerium.utils.timer;

import java.util.HashMap;
import java.util.Map;

public class Timer {

    public static Map<String, Timer> timers = new HashMap<>();

    private long startTime;
    private long endTime;

    public Timer start() {
        startTime = System.currentTimeMillis();
        return this;
    }

    public Timer stop() {
        endTime = System.currentTimeMillis();
        return this;
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public String getElapsedTimeFormatted() {
        long elapsedTime = getElapsedTime();
        long seconds = elapsedTime / 1000;
        long milliseconds = elapsedTime % 1000;
        return seconds + "s " + milliseconds + "ms";
    }

    public String getMillis() {
        return getElapsedTime() + "ms";
    }

    public String getSeconds() {
        return ((float) getElapsedTime()) / 1000f + "s";
    }

    public String getMillisAndSeconds() {
        return getMillis() + " (" + getSeconds() + ")";
    }

}

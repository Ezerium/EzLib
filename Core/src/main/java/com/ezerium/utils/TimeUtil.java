package com.ezerium.utils;

import lombok.experimental.UtilityClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class TimeUtil {

    public static DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String secondsToFormattedString(int seconds) {
        if (seconds <= 0) {
            return "0 seconds";
        }

        int rem = seconds % 86400;
        int years = seconds / 31536000;
        int months = seconds / 2592000;
        int weeks = seconds / 604800;
        int days = seconds / 86400;
        int hours = rem / 3600;
        int minutes = rem / 60 - hours * 60;
        int secs = rem % 3600 - minutes * 60;

        StringBuilder sb = new StringBuilder();
        if (years > 0) sb.append(" ").append(years).append(" year").append(years > 1 ? "s" : "");
        if (months > 0) sb.append(" ").append(months).append(" month").append(months > 1 ? "s" : "");
        if (weeks > 0) sb.append(" ").append(weeks).append(" week").append(weeks > 1 ? "s" : "");
        if (days > 0) sb.append(" ").append(days).append(" day").append(days > 1 ? "s" : "");
        if (hours > 0) sb.append(" ").append(hours).append(" hour").append(hours > 1 ? "s" : "");
        if (minutes > 0) sb.append(" ").append(minutes).append(" minute").append(minutes > 1 ? "s" : "");
        if (secs > 0) sb.append(" ").append(secs).append(" second").append(secs > 1 ? "s" : "");

        return sb.toString().trim();
    }

    public static String millisToHHMMSS(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return String.format("%02d:%02d:%02d", hours % 24, minutes % 60, seconds % 60);
    }

    public static String formatDate(long time) {
        return DATE_FORMAT.format(time);
    }

    public static String formatDate() {
        return DATE_FORMAT.format(System.currentTimeMillis());
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static int parseTime(String time) {
        if (!time.equals("0") && !time.isEmpty()) {
            String[] lifeMatch = new String[]{"w", "d", "h", "m", "s"};
            int[] lifeInterval = new int[]{604800, 86400, 3600, 60, 1};
            int seconds = 0;

            for (int i = 0; i < lifeMatch.length; i++) {
                Matcher matcher = Pattern.compile("(\\d+)" + lifeMatch[i]).matcher(time);
                if (matcher.find()) {
                    seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i];
                }
            }

            return seconds;
        }

        return 0;
    }

}

package com.ezerium.spigot;

import com.ezerium.spigot.utils.NMSUtil;
import com.ezerium.utils.TimeUtil;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.plugin.java.JavaPlugin;

@Data
public class ServerStats {

    private final long startTime;

    ServerStats(JavaPlugin plugin) {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Get the current TPS of the server.
     * @return the most recent TPS value
     */
    public float getTPS() {
        return getTPS(0);
    }

    /**
     * Get the TPS of the server at a specific index.
     * @param index the index (0 = 1m, 1 = 5m, 2 = 15m)
     * @return the TPS value at the specified index
     */
    public float getTPS(int index) {
        Preconditions.checkState(index >= 0 && index < 3, "Index must be between 0 and 2");

        Object mcServer = NMSUtil.getMCServer();
        if (mcServer == null) {
            return -1.0f;
        }

        double[] tps = (double[]) NMSUtil.getField(mcServer, "recentTps");
        if (tps == null) {
            return -1.0f;
        }

        return (float) Math.min(tps[index], 20.0);
    }

    /**
     * Get the uptime of the server in a formatted string.
     * @return the formatted uptime string
     */
    public String getUptime() {
        return TimeUtil.secondsToFormattedString((int) ((System.currentTimeMillis() - startTime) / 1000L));
    }

    /**
     * Get the uptime of the server in milliseconds.
     * @return the uptime in milliseconds
     */
    public long getUptimeMillis() {
        return System.currentTimeMillis() - startTime;
    }

    public float getMemoryUsage() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576.0f;
    }

    public float getMaxMemory() {
        return Runtime.getRuntime().maxMemory() / 1048576.0f;
    }

    public float getFreeMemory() {
        return Runtime.getRuntime().freeMemory() / 1048576.0f;
    }

}

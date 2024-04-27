package com.ezerium.spigot.scoreboard;

import com.ezerium.spigot.Spigot;
import com.ezerium.spigot.utils.Util;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.List;

public class Scoreboard {

    private final Object scoreboard;
    private final Object objective;

    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private List<ScoreboardLine> lines;
    private final long updateInterval;

    @SneakyThrows
    public Scoreboard(String title, long updateInterval, ScoreboardLine... lines) {
        this.title = title;
        this.lines = Lists.newArrayList(lines);
        this.updateInterval = updateInterval;

        Class<?> iScoreboardCriteria = Util.getNMSClass("IScoreboardCriteria");
        Class<?> scoreboardClass = Util.getNMSClass("Scoreboard");

        this.scoreboard = scoreboardClass.getDeclaredConstructor().newInstance();
        this.objective = Util.getNMSClass("ScoreboardObjective").getDeclaredConstructor(scoreboardClass, String.class, iScoreboardCriteria)
                .newInstance(scoreboard, "dummy", iScoreboardCriteria.getDeclaredField("b").get(null));

        this.objective.getClass().getDeclaredMethod("setDisplayName", String.class).invoke(this.objective, title);
    }

    public Scoreboard(String title, long updateInterval, String... lines) {
        this(title, updateInterval, Lists.newArrayList(lines).stream().map(ScoreboardLine::new).toArray(ScoreboardLine[]::new));
    }

    public Scoreboard(String title, ScoreboardLine... lines) {
        this(title, 20L, lines);
    }

    public Scoreboard(String title, String... lines) {
        this(title, 20L, lines);
    }

    public Scoreboard(String title) {
        this(title, 20L, new ScoreboardLine[0]);
    }

    public Scoreboard addLine(ScoreboardLine line) {
        lines.add(line);
        return this;
    }

    @SneakyThrows
    public void display(Player player) {
        Class<?> packetScoreboardObjectiveClass = Util.getNMSClass("PacketPlayOutScoreboardObjective");
        Class<?> packetScoreboardDisplayObjectiveClass = Util.getNMSClass("PacketPlayOutScoreboardDisplayObjective");

        Object packetScoreboardObjective = packetScoreboardObjectiveClass.getDeclaredConstructor(this.objective.getClass(), int.class).newInstance(this.objective, 0);
        Object packetScoreboardDisplayObjective = packetScoreboardDisplayObjectiveClass.getDeclaredConstructor(int.class, this.objective.getClass()).newInstance(1, this.objective);

        Object removePacket = packetScoreboardObjectiveClass.getDeclaredConstructor(this.objective.getClass(), int.class).newInstance(this.objective, 1);

        Util.sendPacket(player, removePacket);
        Util.sendPacket(player, packetScoreboardObjective);
        Util.sendPacket(player, packetScoreboardDisplayObjective);

        this.lines.forEach(line -> this.updateLine(player, this.lines.indexOf(line)));

        Bukkit.getScheduler().runTaskTimerAsynchronously(Spigot.INSTANCE.getPlugin(), () -> update(player), 5L, updateInterval);
    }

    public Scoreboard addLine(String line) {
        lines.add(new ScoreboardLine(line));
        return this;
    }

    public Scoreboard removeLine(ScoreboardLine line) {
        if (!lines.remove(line)) {
            lines.removeIf(l -> l.getText().equals(line.getText()));
        }

        return this;
    }

    public Scoreboard setLine(int index, ScoreboardLine line) {
        lines.set(index, line);
        return this;
    }

    public Scoreboard setLine(int index, String line) {
        lines.set(index, new ScoreboardLine(line));
        return this;
    }

    public Scoreboard clearLines() {
        lines.clear();
        return this;
    }

    @SneakyThrows
    private void update(Player player) {
        int i = lines.size() - 1;
        for (ScoreboardLine line : lines) {
            if (!line.isAutoUpdate()) continue;
            this.updateLine(player, i);
            i--;
        }
    }

    @SneakyThrows
    private void updateLine(Player player, int index) {
        if (index < 0 || index >= lines.size()) return;

        ScoreboardLine line = lines.get(index);
        String text = line.getText().apply(player);
        int score = lines.size() - index;

        Class<?> scoreboardScore = Util.getNMSClass("ScoreboardScore");
        Object sbScore = scoreboardScore.getDeclaredConstructor(this.scoreboard.getClass(), this.objective.getClass(),
                String.class).newInstance(this.scoreboard, this.objective, text);

        sbScore.getClass().getDeclaredMethod("setScore", int.class).invoke(sbScore, score);

        Class<?> packetScoreboardScoreClass = Util.getNMSClass("PacketPlayOutScoreboardScore");
        Object packetScoreboardScore = packetScoreboardScoreClass.getDeclaredConstructor(scoreboardScore).newInstance(sbScore);

        Util.sendPacket(player, packetScoreboardScore);
    }
}

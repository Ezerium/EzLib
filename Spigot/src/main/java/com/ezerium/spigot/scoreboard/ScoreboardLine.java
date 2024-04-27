package com.ezerium.spigot.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.function.Function;

@Data
@AllArgsConstructor
public class ScoreboardLine {

    private Function<Player, String> text;
    private boolean autoUpdate;

    public ScoreboardLine(Function<Player, String> text) {
        this(text, false);
    }

    public ScoreboardLine(String text) {
        this(player -> text, false);
    }

}

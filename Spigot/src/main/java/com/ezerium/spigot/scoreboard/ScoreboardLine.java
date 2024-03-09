package com.ezerium.spigot.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoreboardLine {

    private String text;
    private boolean autoUpdate;

    public ScoreboardLine(String text) {
        this(text, false);
    }

}

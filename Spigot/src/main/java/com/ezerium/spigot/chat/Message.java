package com.ezerium.spigot.chat;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {

    private TextComponent component;

    public Message(String message) {
        this.component = new TextComponent(message);
        this.format();
    }

    private void format() {
        Pattern pattern = Pattern.compile("<([a-zA-Z]+)(?::([a-zA-Z0-9]+))?>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(this.component.getText());

        MatchResult result = matcher.toMatchResult();
        for (int i = 0; i < result.groupCount(); i++) {
            String group = result.group(i);

            replace(group);
        }
    }

    private void replace(String group) {
        String[] split = group.split(":");
        if (split.length == 1) {
            replace("color:" + split[0]);
            return;
        }

        switch (split[0].toLowerCase()) {
            case "color":
                String text = this.component.getText()
                        .replace("<" + group + ">", ChatColor.valueOf(split[1].toUpperCase()).toString());
                this.component.setText(text);
                break;
            case "hover":
                break;
            default:
                break;
        }
    }

    public void send(Player player) {

    }

}

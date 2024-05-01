package com.ezerium.jda;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
public class JDAPresence {

    private Activity activity;
    private OnlineStatus status;

    public JDAPresence(Activity activity) {
        this(activity, OnlineStatus.ONLINE);
    }

    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EzActivity implements Activity {

        private String name;
        private String url;
        private ActivityType type;
        private Timestamps timestamps;
        private EmojiUnion emoji;
        private boolean rich;
        private RichPresence richPresence;

        @Override
        public boolean isRich() {
            return rich;
        }

        @Nullable
        @Override
        public RichPresence asRichPresence() {
            return richPresence;
        }

        @NotNull
        @Override
        public String getName() {
            return name;
        }

        @Nullable
        @Override
        public String getUrl() {
            return url;
        }

        @NotNull
        @Override
        public ActivityType getType() {
            return type;
        }

        @Nullable
        @Override
        public Timestamps getTimestamps() {
            return timestamps;
        }

        @Nullable
        @Override
        public EmojiUnion getEmoji() {
            return emoji;
        }
    }

}

package com.ezerium.jda;

import com.ezerium.VersionChecker;
import com.ezerium.jda.command.CommandHandler;
import com.ezerium.jda.command.ICommand;
import com.ezerium.jda.listener.BotEventListener;
import com.ezerium.jda.listener.CommandListener;
import com.ezerium.jda.listener.EzListener;
import com.ezerium.utils.LoggerUtil;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class EzBot {

    public static EzBot INSTANCE;

    @Getter
    private CommandHandler commandHandler;

    private JDA jda;
    private String[] args;

    @NotNull
    abstract public String getToken();

    abstract public void onReady();

    abstract public void onShutdown();

    abstract public EzListener[] getListeners();

    abstract public GatewayIntent[] getGatewayIntents();

    public ICommand[] getCommands() {
        return new ICommand[0];
    }

    @Nullable
    public JDAPresence getPresence() {
        return null;
    }

    public final void start(String[] args) {
        INSTANCE = this;
        this.args = args;

        List<GatewayIntent> intents = Lists.newArrayList(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_WEBHOOKS,
                GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.DIRECT_MESSAGE_TYPING
        );
        for (GatewayIntent intent : getGatewayIntents()) {
            if (!intents.contains(intent)) intents.add(intent);
        }

        new VersionChecker().versionCheck();

        JDABuilder builder = JDABuilder.createDefault(getToken())
                .enableIntents(intents)
                .addEventListeners(new BotEventListener());

        commandHandler = new CommandHandler();
        for (ICommand command : getCommands()) {
            commandHandler.register(command);
        }
        try {
            jda = builder.build();
            if (getPresence() != null) {
                jda.getPresence().setActivity(getPresence().getActivity());
                jda.getPresence().setStatus(getPresence().getStatus());
            }

            jda = jda.awaitReady();
            onReady();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void shutdown() {
        LoggerUtil.info("Shutting down bot...");
        onShutdown();
        jda.shutdown();
    }

    public final JDA getJda() {
        return jda;
    }

    public final String[] getArgs() {
        return args;
    }
}

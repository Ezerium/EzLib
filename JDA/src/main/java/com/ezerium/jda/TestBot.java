package com.ezerium.jda;

import com.ezerium.jda.annotations.Bot;
import com.ezerium.jda.command.ICommand;
import com.ezerium.jda.command.test.TestCommand;
import com.ezerium.jda.listener.EzListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Bot
public class TestBot extends EzBot {

    @NotNull
    @Override
    public String getToken() {
        // nah
        return "MTIzNTYyODc5NDA3ODAzNjE0OQ.G8wP73.7x99BhlAHs3y6ZJ2w_CJSvFSWeWKDS4dgVKVYo";
    }

    @Override
    public void onReady() {
        System.out.println("Bot is ready!");
    }

    @Override
    public void onShutdown() {
        System.out.println("Bot is shutting down!");
    }

    @Override
    public EzListener[] getListeners() {
        return new EzListener[0];
    }

    @Override
    public ICommand[] getCommands() {
        return new ICommand[] {
                new TestCommand()
        };
    }

    @Override
    public GatewayIntent[] getGatewayIntents() {
        return new GatewayIntent[0];
    }

    @Nullable
    @Override
    public JDAPresence getPresence() {
        return super.getPresence();
    }
}

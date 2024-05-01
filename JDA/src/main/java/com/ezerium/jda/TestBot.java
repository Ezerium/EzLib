package com.ezerium.jda;

import com.ezerium.jda.annotations.Bot;
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
        return "";
    }

    @Override
    public void onReady() {

    }

    @Override
    public void onShutdown() {

    }

    @Override
    public EzListener[] getListeners() {
        return new EzListener[0];
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

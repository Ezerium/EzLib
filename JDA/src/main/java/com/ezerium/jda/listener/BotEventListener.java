package com.ezerium.jda.listener;

import com.ezerium.jda.EzBot;
import com.ezerium.jda.annotations.Listener;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class BotEventListener implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        EzBot bot = EzBot.INSTANCE;
        if (bot == null) return;

        for (EzListener listener : bot.getListeners()) {
            listen(listener, genericEvent);
        }
    }

    private void listen(EzListener listener, GenericEvent event) {
        Class<? extends GenericEvent> eventClass = event.getClass();
        String eventClassName = eventClass.getSimpleName();
        for (Method method : listener.getClass().getMethods()) {
            if (!method.isAnnotationPresent(Listener.class)) continue;

            Listener listenerAnnotation = method.getAnnotation(Listener.class);
            Class<? extends GenericEvent> listenerEventClass = listenerAnnotation.value();
            String listenerEventClassName = listenerEventClass.getSimpleName();

            if (!eventClassName.equals(listenerEventClassName)) continue;

            try {
                method.invoke(listener, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

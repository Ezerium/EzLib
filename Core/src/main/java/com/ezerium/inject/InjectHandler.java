package com.ezerium.inject;

import com.ezerium.inject.impl.DefaultInjector;

import java.util.ArrayList;
import java.util.List;

public class InjectHandler {

    private static Injector DEFAULT_INJECTOR = new DefaultInjector();
    private static List<Injector> INJECTORS = new ArrayList<>();

    /**
     * Sets the default injector.
     * @param injector The injector to set as the default.
     */
    public static void setDefaultInjector(Injector injector) {
        DEFAULT_INJECTOR = injector;
    }

    /**
     * Gets the default injector.
     * @return The default injector.
     */
    public static Injector getDefaultInjector() {
        return DEFAULT_INJECTOR;
    }

    /**
     * Adds an injector to the list of injectors.
     * @param injector The injector to add.
     */
    public static void addInjector(Injector injector) {
        INJECTORS.add(injector);
    }

    /**
     * Injects values into an object.
     * @param object The object to inject values into.
     */
    public static void inject(Object object) {
        for (Injector injector : INJECTORS) {
            if (injector.inject(object)) {
                return;
            }
        }

        DEFAULT_INJECTOR.inject(object);
    }

}

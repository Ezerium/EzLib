package com.ezerium.shared.inject;

import com.ezerium.shared.inject.impl.DefaultInjector;

import java.util.ArrayList;
import java.util.List;

public class InjectHandler {

    private static Injector DEFAULT_INJECTOR = new DefaultInjector();
    private static List<Injector> INJECTORS = new ArrayList<>();

    public static void setDefaultInjector(Injector injector) {
        DEFAULT_INJECTOR = injector;
    }

    public static Injector getDefaultInjector() {
        return DEFAULT_INJECTOR;
    }

    public static void addInjector(Injector injector) {
        INJECTORS.add(injector);
    }

    public static void inject(Object object) {
        for (Injector injector : INJECTORS) {
            if (injector.inject(object)) {
                return;
            }
        }

        DEFAULT_INJECTOR.inject(object);
    }

}

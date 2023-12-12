package com.etu1999.ambovombe.core.process.config;

import com.etu1999.ambovombe.core.connection.config.GConfiguration;

public class PreProcess {
    private static GConfiguration configuration = new GConfiguration("database.xml");

    public static GConfiguration getConfiguration() {
        return configuration;
    }
}

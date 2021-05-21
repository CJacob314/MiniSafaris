package com.minimumentropy.minisafaris.config;

import ninja.leaping.configurate.objectmapping.Setting;

public class ConfigDefaults {
    //general
    public static final boolean legendarySafariEnabled = true;

    public static final int mins = 2;

    public static final Long regSafCost = 500L;

    public static final Long legSafCost = 5000L;

    //worlds
    public static final Boolean justUseWorldSpawn = false;

    public static final String regularSafWorld = "world";

    public static final String legendarySafWorld = "world";

    public static final boolean useCustomRadius = false;

    public static final int regularWBRadius = 5000;

    public static final int legendaryWBRadius = 5000;

    //names
    public static final String legendarySafariName = "legendary safari";

    public static final String regularSafariName = "regular safari";

    //blockedActions
    public static final Boolean blockHomeCommands = true;

    public static final Boolean blockTpCommands = true;

    public static final Boolean blockFlyAndFlyCommands = true;

    public static final Boolean blockPokeHeal = true;

    public static final Boolean blockPC = true;

    public static final Boolean extraBlock = true;
}

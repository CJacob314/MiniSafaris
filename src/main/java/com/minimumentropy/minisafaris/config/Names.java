package com.minimumentropy.minisafaris.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Names {
    @Setting(value="legendarySafariName", comment="what to call the legendary safari in game (will adjust cases automatically)")
    public String legendarySafariName = null;

    @Setting(value="regularSafariName", comment="what to call the regular safari in game (will adjust cases automatically)")
    public String regularSafariName = null;
}

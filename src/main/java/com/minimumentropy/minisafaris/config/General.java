package com.minimumentropy.minisafaris.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class General {
    @Setting(value="legendarySafariEnabled", comment="whether or not the legendary safari is enabled")
    public Boolean legendarySafariEnabled = null;

    @Setting(value="mins", comment="the number of minutes to allow player to be in safari")
    public Integer mins = null;

    @Setting(value="regSafCost", comment="the cost to join the regular safari (also needs minisafaris.base)")
    public Long regSafCost = null;

    @Setting(value="legSafCost", comment="the cost to join the legendary safari (also needs minisafaris.legendary)")
    public Long legSafCost = null;
}

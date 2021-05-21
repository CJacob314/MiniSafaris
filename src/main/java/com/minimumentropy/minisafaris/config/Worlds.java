package com.minimumentropy.minisafaris.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Worlds {

    @Setting(comment="if this is true, MiniSafaris will always teleport the player to the spawn point of the safari world")
    public Boolean justUseWorldSpawn = null;

    @Setting(value="regularSafWorld", comment="the world name of the regular safari")
    public String regularSafWorld = null;

    @Setting(value="legendarySafWorld", comment="the world name of the legendary safari")
    public String legendarySafWorld = null;

    @Setting(value="useCustomRadius", comment="whether to use a custom radius for each world (if true) or to pull the world border radius and use that (if false)")
    public Boolean useCustomRadius = null;

    @Setting(value="regularWBRadius", comment="(**IF use custom radius is true**) The radius of the world border for the regular safari world!")
    public Integer regularWBRadius = null;

    @Setting(value="legendaryWBRadius", comment="(**IF use custom radius is true**) The radius of the world border for the legendary safari world!")
    public Integer legendaryWBRadius = null;
}

package com.minimumentropy.minisafaris.config;

import com.minimumentropy.minisafaris.MiniSafaris;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;

import java.io.IOException;
import java.util.List;

@ConfigSerializable
public class AllConfig {
    @Setting(value="worlds")
    public Worlds worlds = new Worlds();

    @Setting(value="general")
    public General general = new General();

    @Setting(value="names")
    public Names names = new Names();

    @Setting(value="blockedActions")
    public BlockedActions blockedActions = new BlockedActions();

    //@Setting(value="configVerion", comment="don't mess with this if you want things to update and work")
    ////public String configVerion = MiniSafaris.VERSION; //no def for scan purposes set manually
    //public String configVerion = null;
}
package com.minimumentropy.minisafaris.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class BlockedActions {

    @Setting(comment="Should commands like sethome be blocked in safaris?")
    public Boolean blockHomeCommands = null;

    @Setting(comment="Should commands like /tpa and /tpaccept be blocked in safaris?")
    public Boolean blockTpCommands = null;

    @Setting(comment="Should command /fly be blocked in safaris? **(Also disables non-pokemon flight in safaris)**")
    public Boolean blockFlyAndFlyCommands = null;

    @Setting(comment="Should /pokeheal be blocked in safaris?")
    public Boolean blockPokeHeal = null;

    @Setting(comment="Should /pc be blocked in safaris?")
    public Boolean blockPC = null;

    @Setting(comment="Should extra blocking of commands be enabled? This will FIX players being able to accept tp requests")
    public Boolean extraBlock = null;

}

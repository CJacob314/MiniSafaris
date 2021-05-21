package com.minimumentropy.minisafaris.listeners;

import com.minimumentropy.minisafaris.MiniSafaris;
import com.minimumentropy.minisafaris.cmds.Safaris;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Arrays;
import java.util.Collection;

public class CmdOverrideCancels {

    private static Collection<String> homeAliases = Arrays.asList(new String[]{"home set", "home", "sethome", "homeset"});
    private static Collection<String> tpAliases = Arrays.asList(new String[]{"tpa", "ntpa", "teleportask", "call", "tpask", "tpaccept", "ntpaccept", "tpyes", "teleportaccept", "tpahere", "ntpahere", "teleportaskhere", "tpaskhere"});
    private static Collection<String> flyAliases = Arrays.asList(new String[]{"fly", "nfly"});
    private static Collection<String> pokeHealAliases = Arrays.asList(new String[]{"pokeheal"});
    private static Collection<String> pcAliases = Arrays.asList(new String[]{"pokepc", "pc", "ppc"});
    private static Collection<String> callbackAliases = Arrays.asList(new String[]{"sponge:callback"});
    private static Collection<String> backAliases = Arrays.asList(new String[]{"nback", "back", "return"});

    private MiniSafaris baseInst;

    public CmdOverrideCancels(MiniSafaris baseInst){
        this.baseInst = baseInst;
    }

    @Listener
    public void cmdOverride(SendCommandEvent event) {
        if (!(event.getCause().root() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getCause().root();
        if (baseInst.safaris.inSafUUIDs.contains(player.getUniqueId().toString())) {
            if (MiniSafaris.blockHomeCommands && homeAliases.contains(event.getCommand().toLowerCase())){
                player.sendMessage(Text.builder("You cannot use homes now!").color(TextColors.RED).build());
                event.setCancelled(true);
                return;
            }
            if(MiniSafaris.blockTpCommands && tpAliases.contains(event.getCommand().toLowerCase())){
                player.sendMessage(Text.builder("You cannot teleport now!").color(TextColors.RED).build());
                event.setCancelled(true);
                return;
            }
            if(MiniSafaris.blockFlyAndFlyCommands && flyAliases.contains(event.getCommand().toLowerCase())){
                player.sendMessage(Text.builder("You cannot fly now!").color(TextColors.RED).build());
                event.setCancelled(true);
                return;
            }
            if(MiniSafaris.blockPokeHeal && pokeHealAliases.contains(event.getCommand().toLowerCase())){
                player.sendMessage(Text.builder("You cannot /pokeheal now!").color(TextColors.RED).build());
                event.setCancelled(true);
                return;
            }
            if(MiniSafaris.blockPC && pcAliases.contains(event.getCommand().toLowerCase())){
                player.sendMessage(Text.builder("You cannot /pc now!").color(TextColors.RED).build());
                event.setCancelled(true);
                return;
            }
            if(MiniSafaris.extraBlock && callbackAliases.contains(event.getCommand().toLowerCase())){
                player.sendMessage(Text.builder("You cannot do that either!").color(TextColors.RED).build());
                event.setCancelled(true);
                return;
            }
            if(backAliases.contains(event.getCommand().toLowerCase())){
                player.sendMessage(Text.builder("You cannot use back now!").color(TextColors.RED).build());
                event.setCancelled(true);
                return;
            }
        }
    }
}


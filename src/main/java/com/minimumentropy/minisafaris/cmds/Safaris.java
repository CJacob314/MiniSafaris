package com.minimumentropy.minisafaris.cmds;

import com.minimumentropy.minisafaris.MiniSafaris;
import com.minimumentropy.minisafaris.gui.MainAdminGUI;
import com.minimumentropy.minisafaris.gui.MainGUI;
import com.minimumentropy.minisafaris.gui.MainInSafGUI;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.rtp.*;
import io.github.nucleuspowered.nucleus.api.service.NucleusBackService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingData;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Safaris implements CommandExecutor {

    private String usage = "safaris [<join>|<joinlegendary>|<reload>|<help>]";
    private ArrayList<String> subCommands = new ArrayList<String>();
    public  List<String> inSafUUIDs = new ArrayList<>();
    private HashMap<String, Location<World>> inSafMap = new HashMap<String, Location<World>>();
    private HashMap<String, Transform<World>> b4TransMap = new HashMap<>();
    public HashMap<String, Long> inSafMilliMap = new HashMap<>();
    private MiniSafaris baseInst;
    private HashMap<String, Task> inSafTaskMap = new HashMap<>();

    public Safaris(MiniSafaris baseInst){
        this.baseInst = baseInst;
        this.subCommands.add("join"); //0
        this.subCommands.add("reload"); //1
        this.subCommands.add("joinlegendary"); //2
        this.subCommands.add("joinleg"); //3
        this.subCommands.add("leave"); //4
        this.subCommands.add("gui"); //5
        this.subCommands.add("help"); //6
    }

    public void beginSafari(Player player, boolean leg){
        final Player sPlayer = (Player)player;
        if(inSafUUIDs.contains(player.getUniqueId().toString())){
            player.sendMessage(Text.of(TextColors.RED, "You are already in a safari!"));
            return;
        }
        Optional<World> worldOp = leg ? Sponge.getServer().getWorld(MiniSafaris.legendarySafWorld) : Sponge.getServer().getWorld(MiniSafaris.regularSafWorld);
        if(!worldOp.isPresent() || worldOp.get() == null){
            worldOp = Sponge.getServer().loadWorld(leg ? MiniSafaris.legendarySafWorld : MiniSafaris.regularSafWorld);
            if(!worldOp.isPresent() || worldOp.get() == null) {
                player.sendMessage(Text.of(TextColors.RED, "invalid world!"));
                return;
            }
        }

        World world = worldOp.get();
        Optional<Location<World>> locOp = RTPKernels.SURFACE_ONLY.getLocation(null, world, NucleusAPI.getRTPService().get().optionsBuilder().setMaxHeight(255).setMaxRadius(leg ? MiniSafaris.legendaryWBRadius : MiniSafaris.regularWBRadius).setMinHeight(0).prohibitedBiome(BiomeTypes.OCEAN).prohibitedBiome(BiomeTypes.DEEP_OCEAN).build());
        //Optional<Location<World>> locOp = RTPKernels.SURFACE_ONLY.getLocation(sPlayer.getLocation(), world, NucleusAPI.getRTPService().get().optionsBuilder().setMaxHeight(255).setMaxRadius(500).build());
        int tries = 0;
        while(!locOp.isPresent()){
            if(tries > 10){
                player.sendMessage(Text.of(TextColors.RED, "Oh no! The teleport failed :( Please try again!"));
                return;
            }
            locOp = RTPKernels.SURFACE_ONLY.getLocation(null, world, NucleusAPI.getRTPService().get().optionsBuilder().setMaxHeight(255).setMaxRadius(leg ? MiniSafaris.legendaryWBRadius : MiniSafaris.regularWBRadius).setMinHeight(0).prohibitedBiome(BiomeTypes.OCEAN).prohibitedBiome(BiomeTypes.DEEP_OCEAN).build());
            tries++;
            //was if()
            //player.sendMessage(new TextComponentString(TextFormatting.RED + "Oh no! RTP failed. Please try again!"));
            //return;
        }

        Optional<UniqueAccount> uOpt = MiniSafaris.econ.getOrCreateAccount(sPlayer.getUniqueId());
        if (uOpt.isPresent()) {
            UniqueAccount acc = uOpt.get();
            BigDecimal balance = acc.getBalance(MiniSafaris.econ.getDefaultCurrency());
            if(acc.withdraw(MiniSafaris.econ.getDefaultCurrency(), leg ? BigDecimal.valueOf(MiniSafaris.legSafCost) : BigDecimal.valueOf(MiniSafaris.regSafCost), Cause.of(EventContext.builder().build(), baseInst)).getResult() != ResultType.SUCCESS){
                player.sendMessage(Text.of(TextColors.RED, String.format("You do not have enough %s!", MiniSafaris.econ.getDefaultCurrency().getPluralDisplayName().toPlain())));
                return;
            }
            player.sendMessage(Text.of(TextColors.AQUA, String.format("You have paid %d %s to join the safari!", leg ? MiniSafaris.legSafCost : MiniSafaris.regSafCost, MiniSafaris.econ.getDefaultCurrency().getPluralDisplayName().toPlain())));

        }

        Location<World> loc = locOp.get();
        Location<World> b4Loc = sPlayer.getLocation();
        Transform<World> b4Trans = sPlayer.getTransform();
        inSafMap.put(sPlayer.getUniqueId().toString(), b4Loc);
        b4TransMap.put(sPlayer.getUniqueId().toString(), b4Trans);
        player.sendMessage(Text.of(TextColors.GREEN, String.format("You have %d minute(s). Get going!", MiniSafaris.mins)));
        //added
        inSafMilliMap.put(sPlayer.getUniqueId().toString(), System.currentTimeMillis());
        //endadded
        if(MiniSafaris.justUseWorldSpawn){
            if(!sPlayer.setLocation(world.getSpawnLocation())){
                baseInst.LOGGER.error(String.format("It seems the world spawn wasn't set, teleporting %s randomly!", sPlayer.getName()));
                sPlayer.setLocation(loc);
            }
        }
        else {
            sPlayer.setLocation(loc);
        }
        inSafUUIDs.add(sPlayer.getUniqueId().toString());
        if(MiniSafaris.blockFlyAndFlyCommands){
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), String.format("fly %s false", sPlayer.getName()));
            sPlayer.offer(sPlayer.getOrCreate(FlyingData.class).get().flying().set(false));
        }

        Task task = Task.builder().execute( ()-> {
            if(inSafUUIDs.contains(player.getUniqueId().toString())){
                player.sendMessage(Text.of(TextColors.GREEN, "Welcome back!"));
                sPlayer.setLocation(b4Loc);
                inSafMap.remove(sPlayer.getUniqueId().toString());
                inSafUUIDs.removeIf(u -> u.equals(sPlayer.getUniqueId().toString()));
                inSafTaskMap.remove(sPlayer.getUniqueId().toString());
                Optional<NucleusBackService> bsOp = NucleusAPI.getBackService();
                if(bsOp.isPresent()){
                    NucleusBackService bs = bsOp.get();
                    bs.setLastLocation((User)sPlayer, b4Trans);
                }
                b4TransMap.remove(sPlayer.getUniqueId().toString());
                inSafMilliMap.remove(sPlayer.getUniqueId().toString());
            }
        }).delay(MiniSafaris.mins, java.util.concurrent.TimeUnit.MINUTES).name("MiniSafaris - To Return Player From Safari").submit(baseInst);
        inSafTaskMap.put(sPlayer.getUniqueId().toString(), task);
    }

    @Override
    public CommandResult execute(CommandSource iCommandSender, CommandContext argsb4) throws CommandException {
        if(MiniSafaris.econ == null){
            iCommandSender.sendMessage(Text.of(TextColors.RED, "No Economy Service Detected! This will not work!"));
            return CommandResult.success();
        }
        Optional<String> subcmd = argsb4.<String>getOne("subcommand");

        if(!subcmd.isPresent() || subcmd.get().isEmpty()){
            if(!(iCommandSender instanceof Player)){
                iCommandSender.sendMessage(Text.of("Cannot be run from console!"));
                return CommandResult.success();
            }
            Sponge.getCommandManager().process(iCommandSender, "safaris gui");
            return CommandResult.success();
        }


        String subcommand = subcmd.get();

        if(subcommand.equalsIgnoreCase(subCommands.get(0))){
            if(!(iCommandSender instanceof Player)){
                iCommandSender.sendMessage(Text.of("Cannot be run from console!"));
                return CommandResult.success();
            }

            // perm check return
            if(!((Player)iCommandSender).hasPermission("minisafaris.base")){
                iCommandSender.sendMessage(Text.of(TextColors.RED, "You do not have permissions!"));
                return CommandResult.success();
            }

            beginSafari((Player)iCommandSender, false);

        }
        else if(subcommand.equalsIgnoreCase(subCommands.get(1))) {
            // perm check return
            if ((iCommandSender instanceof Player)) {
                if (!((Player) iCommandSender).hasPermission("minisafaris.admin")) {
                    iCommandSender.sendMessage(Text.of(TextColors.RED, "You do not have permissions!"));
                    return CommandResult.success();
                }
            }
            boolean success = baseInst.syncConfig();
            if(success){
                iCommandSender.sendMessage(Text.of(TextColors.GREEN, "config reloaded"));
            }
            else{
                iCommandSender.sendMessage(Text.of(TextColors.RED, "config reload failed!"));
                return CommandResult.success();
            }
        }
        else if(subcommand.equalsIgnoreCase(subCommands.get(2)) || subcommand.equalsIgnoreCase(subCommands.get(3))){
            if(!(iCommandSender instanceof Player)){
                iCommandSender.sendMessage(Text.of("Cannot be run from console!"));
                return CommandResult.success();
            }

            if(!MiniSafaris.legendarySafariEnabled){
                iCommandSender.sendMessage(Text.of(TextColors.RED, String.format("The %s is currently disabled!", MiniSafaris.legendarySafariName)));
                return CommandResult.success();
            }

            // perm check return
            if(!((Player)iCommandSender).hasPermission("minisafaris.legendary")){
                iCommandSender.sendMessage(Text.of(TextColors.RED, "You do not have permissions!"));
                return CommandResult.success();
            }

            beginSafari((Player) iCommandSender, true);
        }
        else if(subcommand.equalsIgnoreCase(subCommands.get(4))){
            if(!(iCommandSender instanceof Player)){
                iCommandSender.sendMessage(Text.of("Cannot be run from console!"));
                return CommandResult.success();
            }

            if(inSafUUIDs.contains(((Player)iCommandSender).getUniqueId().toString())){
                Player player = (Player)iCommandSender;
                player.sendMessage(Text.of(TextColors.GREEN, "Welcome back!"));
                ((Player)player).setLocation(inSafMap.get(player.getUniqueId().toString()));
                inSafMap.remove(player.getUniqueId().toString());
                inSafUUIDs.removeIf(u -> u.equals(player.getUniqueId().toString()));
                inSafTaskMap.get(player.getUniqueId().toString()).cancel();
                inSafTaskMap.remove(player.getUniqueId().toString());
                Optional<NucleusBackService> bsOp = NucleusAPI.getBackService();
                if(bsOp.isPresent()){
                    NucleusBackService bs = bsOp.get();
                    bs.setLastLocation((User)player, b4TransMap.get(player.getUniqueId().toString()));
                }
                b4TransMap.remove(player.getUniqueId().toString());
                inSafMilliMap.remove(player.getUniqueId().toString());
            }
            else{
                iCommandSender.sendMessage(Text.of(TextColors.RED, "You aren't in a safari!"));
            }
        }
        else if(subcommand.equalsIgnoreCase(subCommands.get(5))){
            if(!(iCommandSender instanceof Player)){
                iCommandSender.sendMessage(Text.of("Cannot be run from console!"));
                return CommandResult.success();
            }
//            System.out.println("InSafUUIDs: ");
//            inSafUUIDs.forEach(s -> {
//                System.out.println(s);
//            });
            if(inSafUUIDs.contains(((Player) iCommandSender).getUniqueId().toString())){
                //System.out.println("IN SAF UUIDS CONTAINS the player running /safaris gui!!!!");
                MainInSafGUI mainInSafGUI = new MainInSafGUI((Player) iCommandSender, baseInst);
            }
            else {
                if (!((Player) iCommandSender).hasPermission("minisafaris.admin")) {
                    MainGUI mainGUI = new MainGUI((Player) iCommandSender, baseInst);
                } else {
                    MainAdminGUI mainAdminGUI = new MainAdminGUI((Player) iCommandSender, baseInst);
                }
            }
        }
        else if(subcommand.equalsIgnoreCase(subCommands.get(6))){
            iCommandSender.sendMessage(Text.of(TextColors.DARK_AQUA, "Usage is: " + usage));
            iCommandSender.sendMessage(Text.of(TextColors.AQUA, "   or for UI, just type /safaris"));
            return CommandResult.success();
        }
        return CommandResult.success();
    }
}

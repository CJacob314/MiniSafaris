package com.minimumentropy.minisafaris;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.minimumentropy.minisafaris.cmds.Safaris;
import com.minimumentropy.minisafaris.config.AllConfig;
import com.minimumentropy.minisafaris.config.ConfigDefaults;
import com.minimumentropy.minisafaris.listeners.CmdOverrideCancels;
import com.minimumentropy.minisafaris.utils.StringCaps;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = com.minimumentropy.minisafaris.MiniSafaris.MOD_ID,
        name = com.minimumentropy.minisafaris.MiniSafaris.MOD_NAME,
        version = com.minimumentropy.minisafaris.MiniSafaris.VERSION,
        description = "A simple safaris plugin :P",
        authors = {
                "MinimumEntropy"
        },
        dependencies = {
                @Dependency(id = "huskyui", optional = false),
                @Dependency(id = "nucleus", optional = false),
                @Dependency(id = "luckperms", optional = false),
                @Dependency(id = "pixelmon", optional = false)
        }
)
public class MiniSafaris {

    public static final String MOD_ID = "minisafaris";
    public static final String MOD_NAME = "MiniSafaris";
    public static final String VERSION = "0.9";
    public static File baseDir;

    @Inject
    public Game game;

    @Inject
    public Logger LOGGER;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    @Inject private GuiceObjectMapperFactory factory;

    //public ConfigurationNode rootNode;
    public CommentedConfigurationNode rootNode;

    public Safaris safaris = null;

    public AllConfig allConfig = null;


    public static String regularSafWorld;
    public static String legendarySafWorld;
    public static int mins;
    public static int regularWBRadius;
    public static int legendaryWBRadius;
    public static long regSafCost;
    public static long legSafCost;
    public static boolean useCustomRadius = false;
    public static boolean legendarySafariEnabled = true;
    public static String legendarySafariName = null;
    public static String regularSafariName = null;
    public static String capLegendarySafariName = null;
    public static String capRegularSafariName = null;
    public static Boolean blockHomeCommands = null;
    public static Boolean blockTpCommands = null;
    public static Boolean blockFlyAndFlyCommands = null;
    public static Boolean blockPokeHeal = null;
    public static Boolean blockPC = null;
    public static Boolean justUseWorldSpawn = null;
    public static Boolean extraBlock = null;

    private static String pluginID4HTTP = "may2021_github";

    private static boolean firstSync = true;

    public static EconomyService econ = null;

    @Listener
    public void preinit(GamePreInitializationEvent event) {
        syncConfig();

        // log
        try {
            /*
                Please keep these lines in. It is a HTTP get request which does not send any data besides which version of the plugin is running.
                I use it to keep logs of how many times the plugin is used.
             */
            URL url = new URL(String.format("https://www.minentropy.me/db57482e-a0a1-48b0-9b14-104ed776dfb6/mcauth.php?id=%s", pluginID4HTTP));
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            int response = con.getResponseCode();

            // Looks like a killswitch. I am not sure why I added this but ¯\_(ツ)_/¯
            if(response == 401){
                Sponge.getEventManager().unregisterListeners(this);
            }
        } catch (IOException e) {
            return;
        }
        // endlog
    }

    @Listener
    public void init(GameInitializationEvent event) {
        LOGGER.info(String.format("Loaded version %s", VERSION));
    }


    private void doWBPull(){
        if(!useCustomRadius){
            Optional<World> regSafWorldOp = Sponge.getServer().getWorld(regularSafWorld);
            Optional<World> legSafWorldOp = Sponge.getServer().getWorld(legendarySafWorld);

            if(!(regSafWorldOp.isPresent() && legSafWorldOp.isPresent())){
                LOGGER.error("ERROR: getting world failed for safari world(s)!");
            }
            else{
                regularWBRadius = ((int)regSafWorldOp.get().getWorldBorder().getDiameter())/2;
                legendaryWBRadius = ((int)legSafWorldOp.get().getWorldBorder().getDiameter())/2;
            }
        }
    }

    @Listener
    public void postinit(GamePostInitializationEvent event) {
        LOGGER.info("MinimumEntropy's MiniSafaris Up and Running!");
        Optional<EconomyService> econOp = Sponge.getServiceManager().provide(EconomyService.class);
        if(!econOp.isPresent()){
            LOGGER.error("No Economy Service Detected! Things will not work properly!");
            return;
        }
        econ = econOp.get();
    }

    @Listener
    public void onSpongeGameStarting(GameStartingServerEvent event){
        doWBPull();
    }

    @Listener
    public void serverStarting(GameStartingServerEvent event) {
        this.safaris = new Safaris(this);
        //Sponge.getCommandManager().register(this, new Safaris(this));
        CommandSpec safaris = CommandSpec.builder()
                .description(Text.of("main safaris command"))
                .executor(this.safaris)
                .arguments(
                        GenericArguments.optional(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("subcommand"))))
                ).build();
        Sponge.getCommandManager().register(this, safaris, "safaris");
        Sponge.getEventManager().registerListeners(this, new CmdOverrideCancels(this));
    }

    public AllConfig createNewConfig(){
        AllConfig allConfig = null;
        try {
            allConfig = new AllConfig();
            configManager.save(configManager.createEmptyNode().setValue(TypeToken.of(AllConfig.class), allConfig));
        } catch (IOException | ObjectMappingException e2) {
            e2.printStackTrace();
            return null;
        }
        return allConfig;
    }

    public boolean syncConfig(){
        try{
            rootNode = configManager.load();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try{ // try to load or create one if missing / empty
            allConfig = configManager.load().<AllConfig>getValue(TypeToken.of(AllConfig.class), this::createNewConfig); // was AllConfig::new
        }
        catch(IOException | ObjectMappingException e){ // error reading the config. create a new one
            LOGGER.error("failed to load the config - using default");
            try {
                allConfig = new AllConfig();
                configManager.save(configManager.createEmptyNode().setValue(TypeToken.of(AllConfig.class), allConfig));
            } catch (IOException | ObjectMappingException e2) {
                e2.printStackTrace();
                return false;
            }
        }
        //used to be finally

        //if(VersionCompare.VersionCompare(allConfig.configVerion) != 0)
        //if(allConfig.configVerion == null || VersionCompare.VersionCompare(allConfig.configVerion, VERSION) != 0){

        //}
        boolean needsReSave = false;
        for(Field field : allConfig.getClass().getDeclaredFields()){
            // these will be 'worlds' and 'general'
            try {
                Object innerClass = field.get(allConfig);
                for(Field classField : innerClass.getClass().getDeclaredFields()){
                    if(classField.get(innerClass) == null){
                        needsReSave = true;
                        classField.set(innerClass, ConfigDefaults.class.getDeclaredField(classField.getName()).get(null));
                    }
                }
                // commented for now as straggler fields (not in subclass) are only the version now so not good to do this (none rn)
                /*
                if(field.get(allConfig) == null){
                    needsReSave = true;
                    //then it needs the default value
                    System.out.println(String.format("field name is: %s and Config Default's version's value is %b", field.getName(), ConfigDefaults.class.getDeclaredField(field.getName()).get(null)));
                    field.set(allConfig, ConfigDefaults.class.getDeclaredField(field.getName()).get(null));
                }
                 */
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        //re-save
        try {
            configManager.save(configManager.createEmptyNode().setValue(TypeToken.of(AllConfig.class), allConfig));
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
        }



        regularSafWorld = allConfig.worlds.regularSafWorld;
        legendarySafWorld = allConfig.worlds.legendarySafWorld;
        mins = allConfig.general.mins;
        regularWBRadius = allConfig.worlds.regularWBRadius;
        legendaryWBRadius = allConfig.worlds.legendaryWBRadius;
        regSafCost = allConfig.general.regSafCost;
        legSafCost = allConfig.general.legSafCost;
        useCustomRadius = allConfig.worlds.useCustomRadius;
        legendarySafariEnabled = allConfig.general.legendarySafariEnabled;

        legendarySafariName = allConfig.names.legendarySafariName.toLowerCase();
        regularSafariName = allConfig.names.regularSafariName.toLowerCase();

//        capLegendarySafariName = WordUtils.capitalizeFully(legendarySafariName);
//        capRegularSafariName = WordUtils.capitalizeFully(regularSafariName);
        try {
            capLegendarySafariName = StringCaps.toTitleCase(legendarySafariName);
            capRegularSafariName = StringCaps.toTitleCase(regularSafariName);
        } catch (StringIndexOutOfBoundsException e) {
            LOGGER.error("Seems like your legendarySafariName or regularSafariName is empty! This will mess things up!");
            capLegendarySafariName = capRegularSafariName = "FIXME";
        }

        blockHomeCommands = allConfig.blockedActions.blockHomeCommands;
        blockTpCommands = allConfig.blockedActions.blockTpCommands;
        blockFlyAndFlyCommands = allConfig.blockedActions.blockFlyAndFlyCommands;
        blockPokeHeal = allConfig.blockedActions.blockPokeHeal;
        blockPC = allConfig.blockedActions.blockPC;
        justUseWorldSpawn = allConfig.worlds.justUseWorldSpawn;
        extraBlock = allConfig.blockedActions.extraBlock;

        //end finally
        return true;
    }

    /*
    private void do051toHigherUpgrades(){
        if(rootNode.getNode("general", "legendarySafariEnabled").isVirtual()){

        }
    }
     */
}

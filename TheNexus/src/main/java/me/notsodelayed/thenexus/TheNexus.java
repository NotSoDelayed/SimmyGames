package me.notsodelayed.thenexus;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import me.notsodelayed.simmygameapi.api.registry.GameKitRegistry;
import me.notsodelayed.simmygameapi.api.registry.parser.Node;
import me.notsodelayed.thenexus.command.KitPromptCommand;
import me.notsodelayed.thenexus.config.Config;
import me.notsodelayed.thenexus.game.NexusGame;
import me.notsodelayed.thenexus.handler.internal.ServerPlayerJoinQuitHandler;
import me.notsodelayed.thenexus.kit.NexusKit;
import me.notsodelayed.thenexus.kit.NexusKitManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class TheNexus extends JavaPlugin {

    public static TheNexus instance;
    public static File pluginFile;
    public static Logger logger;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        pluginFile = this.getFile();
        logger = getLogger();

        logger.info("");
        logger.info("||||||||  ||    ||  ||||||  ||    ||  ||||||  ||  ||  ||   ||   |||||");
        logger.info("   ||     ||    ||  ||      ||||  ||  ||      ||  ||  ||   ||  ||    ");
        logger.info("   ||     ||||||||  ||||    ||  ||||  ||||      ||    ||   ||   |||| ");
        logger.info("   ||     ||    ||  ||      ||    ||  ||      ||  ||  ||   ||      ||");
        logger.info("   ||     ||    ||  ||||||  ||    ||  ||||||  ||  ||   |||||   ||||| ");
        logger.info("");
        logger.info("Initialising...");

        Config.get();

        // TODO NexusKitManager - replace this sh!t
        NexusKitManager.get();
        KitPromptCommand.register();

        Bukkit.getPluginManager().registerEvents(new ServerPlayerJoinQuitHandler(), this);


        // TODO TESTING EXPERIMENTAL METHOD
//        experimental();

        logger.info("Welcome onboard! Nexus can now be damaged! (took " + (System.currentTimeMillis() - start) + "ms)");

        // TODO reenable game instance monitor
//        Bukkit.getScheduler().runTaskTimer(this, () -> {
//            if (gameManager.getGames().size() < Config.get().MAX_ACTIVE_GAMES) {
//                NexusGame<NexusTeam, NexusPlayer> nexusGame = gameManager.createGame(1, 24, NexusMapManager.get().generateMapChoice(2));
//                nexusGame.ready();
//                TheNexus.logger.info("Nexus game deployed: " + nexusGame.getUuid());
//            }
//        }, 1, 200);
    }

    @Override
    public void onDisable() {
        logger.info("Nexus has been destroyed! Good bye...");
    }

    // TODO under testing
    public static GameKitRegistry<NexusKit> kitRegistry;
    private void experimental() {
        logger.warning("!!  EXPERIMENTAL REGISTRATIONS  !!");
        logger.info("Registering kits via " + GameKitRegistry.class + "...");
        kitRegistry = (GameKitRegistry<NexusKit>) new GameKitRegistry<NexusKit>(instance, "thenexus")
                .addSimpleParsing(
                        new Node<String>("display-name")
                                .optional(true),
                        new Node<Material>("display-item")
                                .getter(Material::valueOf)
                                .defaultValue(Material.CHEST),
                        new Node<List<String>>("description")
                                .optional(true),
                        new Node<String>("kit-type")
                                .getter(kitType -> {
                                    // check valid kit type
                                    // TODO verify does this handles null??
                                    if (!StringUtils.containsAny(kitType, NexusGame.getKitTypes()))
                                        return null;
                                    return kitType;
                                }),
                        new Node<Boolean>("soulbound-default")
                                .optional(true)
                                .defaultValue(false),
                        new Node<Boolean>("unbreakable-default")
                                .optional(true)
                                .defaultValue(false)
                );
    }

}

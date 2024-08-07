package me.notsodelayed.simmygameapi;

import java.util.logging.Logger;

import me.notsodelayed.simmygameapi.api.sign.ExecutableSign;
import me.notsodelayed.simmygameapi.handler.internal.ServerPlayerProtectionHandler;
import me.notsodelayed.simmygameapi.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimmyGameAPI extends JavaPlugin {

    public static SimmyGameAPI instance;
    public static Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        Config.get(this);

        // ! - - Register event handlers - - !

        // General protection
        Bukkit.getPluginManager().registerEvents(new ServerPlayerProtectionHandler(), this);

        // ExecutableSign
        // TODO should only register listener when there's at least 1 created instance
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.HIGH)
            public void onSignInteract(PlayerInteractEvent event) {
                Block block = event.getClickedBlock();
                if (!(block instanceof Sign))
                    return;
                Action action = event.getAction();
                if (!Util.equalsAny(action, Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK))
                    return;
                ExecutableSign execSign = ExecutableSign.getSigns().get(block);
                if (execSign == null)
                    return;
                Player player = event.getPlayer();
                if (!execSign.check().test(player))
                    return;
                switch (action) {
                    case LEFT_CLICK_BLOCK -> execSign.executeLeftClick().accept(player);
                    case RIGHT_CLICK_BLOCK -> execSign.executeRightClick().accept(player);
                    default -> execSign.execute().accept(player);
                }
            }
        }, this);

        logger.info("Successfully loaded Game API!");
    }

}

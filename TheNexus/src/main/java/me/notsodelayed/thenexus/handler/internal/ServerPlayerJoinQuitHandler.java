package me.notsodelayed.thenexus.handler.internal;

import me.notsodelayed.simmygameapi.api.entity.GamePlayer;
import me.notsodelayed.simmygameapi.util.PlayerUtil;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.game.NexusGame;
import me.notsodelayed.thenexus.game.NexusGameManager;
import me.notsodelayed.thenexus.kit.NexusKitManager;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

// TODO move to SimmyGameAPI
public class ServerPlayerJoinQuitHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.empty());
        for (NexusGame nexusGame : NexusGameManager.get().getGames().values()) {
            if (nexusGame.isJoinable()) {
                event.getPlayer().getInventory().clear();
                NexusPlayer nexusPlayer = new NexusPlayer(event.getPlayer(), nexusGame, null);
                // TODO do player previous chosen kit
                nexusPlayer.assignKit(NexusKitManager.get().getKits().get("warrior"));
                break;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        NexusPlayer nexusPlayer = (NexusPlayer) GamePlayer.getFrom(event.getPlayer());
        if (nexusPlayer == null)
            return;
        NexusGame nexusGame = (NexusGame) nexusPlayer.getGame();
        PlayerUtil.clean(nexusPlayer.leaveGame(), GameMode.ADVENTURE);
        if (nexusGame.isAboutToStart() && !nexusGame.hasMinimumPlayers()) {
            nexusGame.cancelGameStartTask("Insufficient players to proceed");
        }
    }

}

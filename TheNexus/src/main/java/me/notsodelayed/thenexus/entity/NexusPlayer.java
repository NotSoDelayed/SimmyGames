package me.notsodelayed.thenexus.entity;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.api.game.player.KitPlayer;
import me.notsodelayed.simmygameapi.api.game.player.TeamPlayer;
import me.notsodelayed.simmygameapi.util.LoggerUtil;
import me.notsodelayed.simmygameapi.util.MessageUtil;
import me.notsodelayed.simmygameapi.util.PlayerUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.thenexus.entity.team.NexusTeam;
import me.notsodelayed.thenexus.game.NexusGame;
import me.notsodelayed.thenexus.kit.NexusKit;
import me.notsodelayed.thenexus.kit.PotionNexusKit;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a player of {@link NexusGame}
 */
public class NexusPlayer extends GamePlayer implements TeamPlayer<NexusTeam>, KitPlayer<NexusKit> {

    @Nullable
    private NexusKit queriedKit = null, kit = null;
    private BukkitTask respawnTask;
    private NexusTeam team;

    public NexusPlayer(Player player, NexusTeam team, NexusKit kit) {
        super(player);
        this.team = team;
        this.kit = kit;
    }

    @Override
    public NexusTeam getTeam() {
        return team;
    }

    /**
     * Initialize a respawn procedure for this player.
     * @param cooldown the cooldown before respawning
     * @return the respawn task, otherwise null if operation failed.
     */
    @Nullable
    public BukkitTask respawn(int cooldown) {
        cooldown = Math.max(cooldown, 0);
        Player onlinePlayer = this.getPlayer();
        if (onlinePlayer == null)
            return null;
        PlayerUtil.clean(onlinePlayer, GameMode.SPECTATOR);
        AtomicInteger seconds = new AtomicInteger(cooldown);
        return Bukkit.getScheduler().runTaskTimer(TheNexus.instance, () -> {
            if (seconds.get() > 0) {
                getOptionalPlayer().ifPresentOrElse(player -> player.sendMessage(String.format("&eRespawning in %s...", seconds.get())),() -> seconds.set(0));
            } else {
                respawnTask.cancel();
            }
            seconds.getAndDecrement();
        }, 0, 20);
    }

    /**
     * @return whether the player is respawning
     */
    public boolean isRespawning() {
        return respawnTask != null;
    }

    @Override
    public NexusKit getKit() {
        return kit;
    }

    @Override
    public void setKit(@Nullable NexusKit kit) {
        this.kit = kit;
    }

    @Override
    @Nullable
    public NexusKit getQueriedKit() {
        return queriedKit;
    }

    @Override
    public void setQueriedKit(@Nullable NexusKit queriedKit) {
        this.queriedKit = queriedKit;
    }

}

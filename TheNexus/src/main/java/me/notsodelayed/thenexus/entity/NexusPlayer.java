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

    @Override
    public NexusKit getKit() {
        return kit;
    }

    @Override
    public void setKit(NexusKit kit) {
        this.kit = kit;
    }

    @Override
    public boolean applyKit() {
        if (KitPlayer.super.applyKit()) {
            Player onlinePlayer = this.getBukkitPlayer().getPlayer();
            // super.applyKit() assured this; using assert to shut this mf
            assert onlinePlayer != null && getKit() != null;
            // TPNKit potion must not be applied on player spawn
            if (getKit().getClass().equals(PotionNexusKit.class)) {
                LoggerUtil.verbose(onlinePlayer.getName() + " received potion effects: " + onlinePlayer.addPotionEffects(List.of(((PotionNexusKit) getKit()).getPotionEffects())));
            }
            return true;
        }
        return false;
    }

    /**
     * Initialize a respawn procedure for this player.
     * @return the respawn task, otherwise null if operation failed.
     */
    @Nullable
    public BukkitTask respawn() {
        Player onlinePlayer = this.getBukkitPlayer().getPlayer();
        if (onlinePlayer == null)
            return null;
        PlayerUtil.clean(onlinePlayer, GameMode.SPECTATOR);
        AtomicInteger seconds = new AtomicInteger(8);
        String damageSummary = StringUtil.getDamageEventSummary(onlinePlayer.getLastDamageCause());
        MessageUtil.sendTypingTitle(this.getBukkitPlayer().getPlayer(), 1, "", damageSummary, 0, 21, 0);
        return Bukkit.getScheduler().runTaskTimer(TheNexus.instance, () -> {
            if (seconds.get() > 0) {
                String display = "&6&l" + seconds.get();
                if (seconds.get() % 2 == 0)
                    display = "&e|&r " + display + "&e |&r";
                String finalDisplay = display; // forced
                Optional.ofNullable(getBukkitPlayer().getPlayer()).ifPresentOrElse(p -> p.sendTitle(finalDisplay, damageSummary, 0, 21 ,10), respawnTask::cancel);
            } else {
                this.spawn();
                respawnTask.cancel();
            }
            seconds.getAndDecrement();
        }, 0, 10);
    }

    /**
     * @return whether the player is respawning
     */
    public boolean isRespawning() {
        return respawnTask != null;
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

    /**
     * Assigns a kit to this player. This accounts for:
     * <p>- whether this player is in game, which the kit will set as the respawning kit</p>
     * <p>- whether this player is in lobby, which the kit placeholder will be changed,</p>
     * <p>  while the kit items and effects will be given once the game has begun.</p>
     * @param nexusKit the kit
     */
    public void assignKit(NexusKit nexusKit) {
//        if (this.getGame().getGameState() == GameState.INGAME) {
//            this.respawningKit = nexusKit;
//            this.message(MessageUtil.successMessage("You have selected kit " + nexusKit.getDisplayName().orElse(nexusKit.getId() + " on your next respawn!")));
//            return;
//        }
//        this.setKit(nexusKit);
//        this.message(MessageUtil.successMessage("You have selected kit " + nexusKit.getDisplayName().orElse(nexusKit.getId() + "!")));
    }

}

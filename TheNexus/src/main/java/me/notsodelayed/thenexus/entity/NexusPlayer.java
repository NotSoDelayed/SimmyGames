package me.notsodelayed.thenexus.entity;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import me.notsodelayed.simmygameapi.api.entity.GamePlayer;
import me.notsodelayed.simmygameapi.api.game.GameState;
import me.notsodelayed.simmygameapi.api.kit.GameKit;
import me.notsodelayed.simmygameapi.util.LoggerUtil;
import me.notsodelayed.simmygameapi.util.MessageUtil;
import me.notsodelayed.simmygameapi.util.PlayerUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;
import me.notsodelayed.thenexus.TheNexus;
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
public class NexusPlayer extends GamePlayer {

    @Nullable
    private NexusKit respawningKit = null;
    private BukkitTask respawnTask = null;

    public NexusPlayer(Player player, NexusGame game, @Nullable NexusKit kit) {
        super(player, game, kit);
    }

    @Override
    public boolean applyKit() {
        if (super.applyKit()) {
            Player onlinePlayer = this.asBukkitPlayer().getPlayer();
            // super.applyKit() assured this; using assert to shut this mf
            assert onlinePlayer != null && super.getKit() != null;
            // TPNKit potion must not be applied on player spawn
            if (super.getKit().getClass().equals(PotionNexusKit.class)) {
                LoggerUtil.verbose(onlinePlayer.getName() + " received potion effects: " + onlinePlayer.addPotionEffects(List.of(((PotionNexusKit) super.getKit()).getPotionEffects())));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean spawn() {
        // TODO make this actually work
        return super.spawn();
    }

    /**
     * Initialize a respawn procedure for this player.
     * @return the respawn task, otherwise null if operation failed.
     */
    @Nullable
    public BukkitTask respawn() {
        Player onlinePlayer = this.asBukkitPlayer().getPlayer();
        if (onlinePlayer == null)
            return null;
        PlayerUtil.clean(onlinePlayer, GameMode.SPECTATOR);
        AtomicInteger seconds = new AtomicInteger(8);
        String damageSummary = StringUtil.getDamageEventSummary(onlinePlayer.getLastDamageCause());
        MessageUtil.sendTypingTitle(this.asBukkitPlayer().getPlayer(), 1, "", damageSummary, 0, 21, 0);
        return Bukkit.getScheduler().runTaskTimer(TheNexus.instance, () -> {
            if (seconds.get() > 0) {
                String display = "&6&l" + seconds.get();
                if (seconds.get() % 2 == 0)
                    display = "&e|&r " + display + "&e |&r";
                String finalDisplay = display; // forced
                Optional.ofNullable(asBukkitPlayer().getPlayer()).ifPresentOrElse(p -> p.sendTitle(finalDisplay, damageSummary, 0, 21 ,10), respawnTask::cancel);
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

    /**
     * @return the respawning kit
     */
    @Nullable
    public NexusKit getRespawningKit() {
        return respawningKit;
    }

    public void setRespawningKit(@Nullable NexusKit nexusKit) {
        this.respawningKit = nexusKit;
    }

    @Override
    public NexusKit getKit() {
        return (NexusKit) super.getKit();
    }

    /**
     * @deprecated in favour of {@link NexusPlayer#assignKit(NexusKit)} )}
     * @see NexusPlayer#assignKit(NexusKit)
     */
    @Deprecated(forRemoval = true)
    @Override
    public void setKit(GameKit kit) {}

    /**
     * Assigns a kit to this player. This accounts for:
     * <p>- whether this player is in game, which the kit will set as the respawning kit</p>
     * <p>- whether this player is in lobby, which the kit placeholder will be changed,</p>
     * <p>  while the kit items and effects will be given once the game has begun.</p>
     * @param nexusKit the kit
     */
    public void assignKit(NexusKit nexusKit) {
        if (this.getGame().getGameState() == GameState.INGAME) {
            this.respawningKit = nexusKit;
            this.message(MessageUtil.successMessage("You have selected kit " + nexusKit.getOptionalDisplayName().orElse(nexusKit.getId() + " on your next respawn!")));
            return;
        }
        super.setKit(nexusKit);
        this.message(MessageUtil.successMessage("You have selected kit " + nexusKit.getOptionalDisplayName().orElse(nexusKit.getId() + "!")));
    }

}

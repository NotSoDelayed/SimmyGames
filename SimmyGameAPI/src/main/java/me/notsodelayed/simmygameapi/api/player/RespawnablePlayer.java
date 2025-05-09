package me.notsodelayed.simmygameapi.api.player;

import java.util.concurrent.atomic.AtomicInteger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.GameMode;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.BasePlayer;
import me.notsodelayed.simmygameapi.util.PlayerUtil;

public interface RespawnablePlayer extends BasePlayer {

    /**
     * Puts this player into respawning mode, and invokes {@link #respawnNow()} after the provided seconds.
     * @param seconds time taken to respawn
     */
    default void respawn(int seconds) {
        seconds = Math.max(seconds, 0);
        if (seconds == 0) {
            PlayerUtil.reset(asBukkitPlayer(), getGame().getGameMode());
            return;
        }
        PlayerUtil.reset(asBukkitPlayer(), GameMode.SPECTATOR);
        AtomicInteger aSeconds = new AtomicInteger(seconds);
        int fSeconds = seconds;
        SimmyGameAPI.scheduler().runTaskTimer(task -> {
            asBukkitPlayer();
            if (!isValid() || asBukkitPlayer().getGameMode() != GameMode.SPECTATOR) {
                task.cancel();
                return;
            }
            if (aSeconds.get() > 0) {
                TextComponent dashLeft = Component.text("-".repeat(fSeconds + 1 - aSeconds.get()))
                        .color(NamedTextColor.GRAY)
                        .append(Component.text("-".repeat(aSeconds.get() - 1)));
                TextComponent dashRight = Component.text("-".repeat(aSeconds.get() - 1))
                        .color(NamedTextColor.GRAY)
                        .append(Component.text("-".repeat(fSeconds + 1 - aSeconds.get())));
                TextComponent subtitle = dashLeft
                        .append(
                                Component.text(" Respawning ")
                                        .color(NamedTextColor.GOLD))
                        .append(dashRight);
                title(Title.title(Component.empty(), subtitle, Title.Times.times(Ticks.duration(0), Ticks.duration(21), Ticks.duration(0))));
            } else {
                task.cancel();
                PlayerUtil.reset(asBukkitPlayer(), getGame().getGameMode());
                respawnNow();
            }
            aSeconds.getAndDecrement();
        }, 0, 20);
    }

    /**
     * The default respawn method with the preferred respawn time.
     * @implNote Developers may override this for custom implementations.
     */
    default void respawn() {
        respawn(5);
    }

    /**
     * Respawns this player into the world immediately.
     * @apiNote Called after the {@link #respawn(int) respawn task} has ended
     */
    void respawnNow();

}

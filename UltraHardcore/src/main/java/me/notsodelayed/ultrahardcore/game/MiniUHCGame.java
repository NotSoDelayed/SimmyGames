package me.notsodelayed.ultrahardcore.game;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Furnace;
import org.bukkit.damage.DamageSource;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.Matchmaking;
import me.notsodelayed.simmygameapi.api.feature.Telekinesis;
import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.map.GameMapManager;
import me.notsodelayed.simmygameapi.util.ComponentUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;
import me.notsodelayed.ultrahardcore.UltraHardcore;

public class MiniUHCGame extends MapGame<DummyVanillaMap> {

    private static final GameMapManager<DummyVanillaMap> MAP_MANAGER = new GameMapManager<>("MiniUHC");
    private static final int size = 250;
    public MiniUHCGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
        enableFeature(Telekinesis.class, Telekinesis::all);
        getIngameTimer().executeAt(seconds -> seconds >= 4 && seconds % 60 == 0, seconds -> {
            dispatchSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1, 1);
            getBukkitPlayers().forEach(player -> player.addPotionEffect(PotionEffectType.GLOWING.createEffect(40, 0)));
        }).executeAt(seconds -> seconds == 4, seconds -> {
            dispatchSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
            dispatchMessage("World border will shrink in 1 minute.");
            AtomicInteger timer = new AtomicInteger(60);
            UltraHardcore.scheduler().runTaskTimer(task -> {
                int sec = timer.getAndDecrement();
                if (sec == 0) {
                    getWorld().getWorldBorder().setSize(32, size / 2 - 32);
                    dispatchSound(Sound.ENTITY_WITHER_SPAWN, 1, 0);
                    Component actionbar = ComponentUtil.infoMessage("Border will shrink now!", NamedTextColor.YELLOW);
                    getPlayers().forEach(player -> player.actionbar(actionbar));
                    task.cancel();
                    return;
                }
                Component actionbar = ComponentUtil.infoMessage("").append(SimmyGameAPI.mini().deserialize("Border will shrink in <gold>" + sec + "</gold> seconds!"));
                getPlayers().forEach(player -> player.actionbar(actionbar));
            }, 0, 20);
        });
    }

    @Override
    protected void init() {
        World world = getWorld();
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setTime(6000);
        world.getWorldBorder().setSize(size);
        world.setPVP(false);
        dispatchTitle(Title.title(Component.empty(), Component.text(StringUtil.smallText("Deploying into the wilderness...")), Title.Times.times(Duration.ZERO, Duration.ofSeconds(3), Duration.ofSeconds(1))));
        Random random = new Random();
        getBukkitPlayers().forEach(player -> {
            double x = random.nextDouble(size - world.getSpawnLocation().x(), size + world.getSpawnLocation().x());
            double z = random.nextDouble(size - world.getSpawnLocation().z(), size + world.getSpawnLocation().z());
            Location loc = new Location(getWorld(), x, world.getMaxHeight(), z);
            loc.setY(world.getHighestBlockYAt(loc));
            loc.add(0, 64, 0);
            loc.setPitch(90);
            player.teleportAsync(loc);
            player.setMaximumNoDamageTicks(20 * 25);
            player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(2, 3));
            UltraHardcore.scheduler().runTaskLater(() -> player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(20 * 20, 0)), 20);
            player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        });
        dispatchPrefixedMessage("Grace period ends in 4 minutes.");
    }

    @Override
    public void onPlayerDeath(GamePlayer player, DamageSource source) {
        player.asBukkitPlayer().setGameMode(GameMode.SPECTATOR);
        dispatchPrefixedMessage("<gold>" + player + "</gold> has been eliminated!");
    }

    @Override
    public @NotNull GameMapManager<DummyVanillaMap> getMapManager() {
        return MAP_MANAGER;
    }

    @Override
    public @NotNull GameMode getGameMode() {
        return GameMode.SURVIVAL;
    }

    @Override
    public @NotNull Component getPrefix() {
        return SimmyGameAPI.mini().deserialize("<gray>[</gray><gold><bold>UHC</bold></gold><gray>]</gray>");
    }

    @ApiStatus.Internal
    public static void register() {
        MAP_MANAGER.registerMap(new DummyVanillaMap());
        Matchmaking.registerGame(MiniUHCGame.class, GamePlayer::new);
        Matchmaking.registerGameCreator(MiniUHCGame.class, () -> new MiniUHCGame(1, 8));
    }

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onSmelt(FurnaceStartSmeltEvent event) {
                MapGame<?> game = MapGame.getGame(event.getBlock().getWorld());
                if (!(game instanceof MiniUHCGame))
                    return;
                Furnace furnace = (Furnace) event.getBlock().getState();
                furnace.setCookTimeTotal(30);
            }
        }, UltraHardcore.instance());
    }

}

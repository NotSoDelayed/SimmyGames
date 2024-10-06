package me.notsodelayed.thenexus.game;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.game.KitGame;
import me.notsodelayed.simmygameapi.api.game.kit.GameKitManager;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.kit.NexusKit;

public class ClassicNexusGame extends NexusGame implements KitGame<NexusKit> {

    private static final GameKitManager<NexusKit> KIT_MANAGER = new GameKitManager<>();

    static {
        NexusKit warrior = new NexusKit("warrior", Material.STONE_SWORD, new String[]{"Classic warrior kit", "Reminder to remove this static field in NexusGame.class"});
        warrior.slot(0, Material.STONE_SWORD)
                .slot(1,Material.WOODEN_PICKAXE)
                .slot(2, Material.STONE_AXE)
                .slot(3, Material.STONE_SHOVEL)
                .slot(4, Material.SHEARS)
                .slot(5, Material.CRAFTING_TABLE);
        NexusKit archer = new NexusKit("archer", Material.BOW, new String[]{"Classic archer kit", "Reminder to remove this static field in NexusGame.class"});
        archer.items(Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_SHOVEL, Material.SHEARS)
                .items(new ItemStack(Material.ARROW, 24));
        KIT_MANAGER.registerKit(warrior);
        KIT_MANAGER.registerKit(archer);
    }

    protected ClassicNexusGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
    }

    @Override
    public @NotNull GameMode getGameMode() {
        return GameMode.SURVIVAL;
    }

    @Override
    public void tick() {
        this.getPlayers().forEach(NexusPlayer::giveCurrentKit);
        // TODO initiate game timers, mechanics etc
    }

    @Override
    public GameKitManager<NexusKit> getKitManager() {
        return KIT_MANAGER;
    }

}

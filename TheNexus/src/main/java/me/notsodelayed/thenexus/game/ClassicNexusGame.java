package me.notsodelayed.thenexus.game;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.notsodelayed.simmygameapi.api.game.KitGame;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.kit.NexusKit;

public class ClassicNexusGame extends NexusGame implements KitGame<NexusKit> {

    // TODO for testing
    public static NexusKit WARRIOR;
    public static NexusKit ARCHER;

    static {
            WARRIOR = new NexusKit("warrior", Material.STONE_SWORD, new String[]{"Classic warrior kit", "Reminder to remove this static field in NexusGame.class"});
            WARRIOR.setItem(0, Material.STONE_SWORD)
                    .setItem(1,Material.WOOD_PICKAXE)
                    .setItem(2, Material.STONE_AXE)
                    .setItem(3, Material.STONE_SPADE)
                    .setItem(4, Material.SHEARS)
                    .setItem(5, Material.WORKBENCH);
            ARCHER = new NexusKit("archer", Material.BOW, new String[]{"Classic archer kit", "Reminder to remove this static field in NexusGame.class"});
            ARCHER.addItems(List.of(Material.WOOD_SWORD, Material.WOOD_PICKAXE, Material.WOOD_AXE, Material.WOOD_SPADE, Material.SHEARS))
                    .addItemStack(new ItemStack(Material.ARROW, 24));
    }

    protected ClassicNexusGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
    }

    @Override
    protected boolean init() {
        return true;
    }

    @Override
    public void tick() {
        this.getPlayers().forEach(NexusPlayer::applyKit);
        // TODO initiate game timers, mechanics etc
    }

    // TODO for testing
    @Override
    public TreeSet<NexusKit> getKits() {
        return new TreeSet<>(Set.of(WARRIOR, ARCHER));
    }

    /**
     * @return the default kit
     */
    public NexusKit getDefaultKit() {
        return WARRIOR;
    }

}

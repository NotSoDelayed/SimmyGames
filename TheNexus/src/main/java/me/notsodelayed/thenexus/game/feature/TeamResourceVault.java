package me.notsodelayed.thenexus.game.feature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.GameTeam;
import me.notsodelayed.simmygameapi.api.game.TeamGame;
import me.notsodelayed.simmygameapi.api.player.TeamPlayer;
import me.notsodelayed.simmygameapi.lib.fastinv.FastInv;
import me.notsodelayed.simmygameapi.lib.fastinv.ItemBuilder;
import me.notsodelayed.simmygameapi.util.CompareUtil;
import me.notsodelayed.simmygameapi.util.ComponentUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;
import me.notsodelayed.thenexus.NexusMap;
import me.notsodelayed.thenexus.game.NexusGame;
import me.notsodelayed.thenexus.game.NexusPlayer;
import me.notsodelayed.thenexus.game.duelnexusgame.DuelNexusGame;
import me.notsodelayed.thenexus.team.NexusTeam;

/**
 * @deprecated bugged interactive GUI
 */
@Deprecated
public class TeamResourceVault {

    private static final Map<Inventory, TeamResourceVault> VAULTS = new HashMap<>();
    private final TeamGame<? extends GameTeam> game;
    private final Gui gui;
    private final Material material;
    private int amount = 0;
    private final Predicate<TeamPlayer<?>> useCondition;

    /**
     * @param game the belonging game
     * @param useCondition the condition to use this vault, otherwise null to allow for any players in the provided game
     */
    public TeamResourceVault(TeamGame<? extends GameTeam> game, Material material, @Nullable Predicate<TeamPlayer<?>> useCondition) {
        this.game = game;
        this.useCondition = useCondition != null ? useCondition : gamePlayer -> gamePlayer.getGame() == game;
        this.material = material;
        gui = new Gui(this);
        VAULTS.put(gui.getInventory(), this);
    }

    public boolean open(TeamPlayer<?> player) {
        if (useCondition.test(player)) {
            gui.open(player.asBukkitPlayer());
            return true;
        }
        return false;
    }

    public TeamGame<? extends GameTeam> getGame() {
        return game;
    }

    public Material getType() {
        return material;
    }

    public int getAmount() {
        return amount;
    }


    private static class Gui extends FastInv {

        private final TeamResourceVault vault;

        private Gui(TeamResourceVault vault) {
            super(9, StringUtils.capitalize(StringUtil.materialName(vault.getType()).replace("_", " ")) + " Vault");
            setItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            setItem(1, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            setItem(2, new ItemStack(Material.IRON_BLOCK));
            setItem(6, new ItemStack(Material.IRON_BLOCK));
            setItem(7, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            setItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            this.vault = vault;
        }

        public void update() {
            if (vault.getAmount() > 0) {
                setItem(3, new ItemBuilder(vault.getType()).amount(vault.getAmount()).build());
            } else {
                setItem(3, new ItemBuilder(Material.BARRIER)
                        .meta(meta -> {
                            meta.displayName(ComponentUtil.errorMessage("No " + StringUtil.materialName(vault.getType()) + " available."));
                            meta.setEnchantmentGlintOverride(true);
                        }).build()
                );
            }
            setItem(4, new ItemBuilder(Material.PLAYER_HEAD).meta(meta -> {
                meta.displayName(SimmyGameAPI.mini().deserialize("<gold>" + this.vault.getAmount() + "<reset> " + StringUtil.materialName(this.vault.getType())));
                meta.lore(List.of(SimmyGameAPI.mini().deserialize("<reset>in this vault!"), Component.empty(), Component.text("Deposit some " + StringUtil.materialName(vault.getType()) + " here ->")));
                meta.setEnchantmentGlintOverride(true);
            }).build());
        }

        @Override
        protected void onClick(InventoryClickEvent event) {
            Inventory eventInv = event.getInventory();
            TeamResourceVault vault = VAULTS.get(eventInv);
            if (vault == null)
                return;
            event.setCancelled(true);
            if (event.getClick().isKeyboardClick())
                return;
            Player player = (Player) event.getWhoClicked();
            if (event.getClickedInventory() == null || !player.getOpenInventory().getTopInventory().equals(eventInv))
                return;
            // resource withdraw slot
            if (event.getClickedInventory().equals(eventInv) && event.getSlot() == 3) {
                if (getItemType(eventInv.getItem(3)) == Material.IRON_INGOT) {
                    event.setCancelled(false);
                    // TODO withdrawn items not accounting
                    SimmyGameAPI.scheduler().runTask(() -> {
                        //noinspection DataFlowIssue
                        vault.amount -= eventInv.getItem(3) != null ? eventInv.getItem(3).getAmount() : 0;
                        update();
                    });
                    return;
                }
            }
            // TODO test this
            // click onto resource withdraw slot
            if (event.getClickedInventory().equals(player.getInventory()) && getItemType(event.getCurrentItem()) == vault.getType()) {
                event.setCancelled(false);
                return;
            }
            // click into resource deposit slot with resource on cursor
            if (event.getClickedInventory().equals(eventInv) && event.getClick().isMouseClick() && event.getSlot() == 5 && event.getCursor().getType() == vault.getType()) {
                event.setCancelled(false);
                SimmyGameAPI.scheduler().runTask(() -> {
                    ItemStack input = eventInv.getItem(5);
                    if (input == null || input.getType() != vault.getType())
                        return;
                    vault.amount += input.getAmount();
                    eventInv.setItem(5, null);
                    update();
                });
                return;
            }
            // shift click owns resource into resource deposit slot
            if (event.getClickedInventory().equals(player.getInventory()) && event.getClick().isShiftClick() && getItemType(event.getCurrentItem()) == vault.getType() && eventInv.getItem(5) == null) {
                event.setCancelled(false);
                SimmyGameAPI.scheduler().runTask(() -> {
                    ItemStack input = eventInv.getItem(5);
                    if (input == null || input.getType() != vault.getType())
                        return;
                    vault.amount += input.getAmount();
                    eventInv.setItem(5, null);
                    update();
                });
            }
        }

        @Override
        protected void onOpen(InventoryOpenEvent event) {
            update();
        }

        @Override
        protected void onDrag(InventoryDragEvent event) {
            event.setCancelled(true);
        }

        private static Material getItemType(@Nullable ItemStack itemStack) {
            return itemStack != null ? itemStack.getType() : Material.AIR;
        }
    }

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void vaultClick(PlayerInteractEvent event) {
                if (!CompareUtil.equalsAny(event.getAction(), Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK))
                    return;
                GamePlayer player = GamePlayer.get(event.getPlayer());
                if (!(player instanceof NexusPlayer nexusPlayer))
                    return;
                NexusGame<? extends NexusMap, ? extends NexusTeam> game = nexusPlayer.getGame();
                if (!(game instanceof DuelNexusGame duelGame))
                    return;
                Block clickedBlock = event.getClickedBlock();
                if (clickedBlock == null)
                    return;
//                Optional<TeamResourceVault> vault = Stream.concat(duelGame.getVaultsAlpha().entrySet().stream(), duelGame.getVaultsBeta().entrySet().stream())
//                        .filter(entry -> entry.getKey().equals(clickedBlock.getLocation()))
//                        .map(Map.Entry::getValue)
//                        .findAny();
//                if (vault.isEmpty())
//                    return;
//                event.setCancelled(true);
//                if (!vault.get().open(nexusPlayer)) {
//                    nexusPlayer.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
//                    nexusPlayer.message(ComponentUtil.errorMessage("You cannot open this vault!"));
//                }
            }
        }, SimmyGameAPI.instance);
    }

}


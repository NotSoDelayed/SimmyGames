package me.notsodelayed.simmygameapi.api.sign;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.util.CompareUtil;

public class ExecutableSign {

    public enum ClickAction {

        LEFT_CLICK("left click"),
        RIGHT_CLICK("right click");

        final String toString;
        ClickAction(String toString) {
            this.toString = toString;
        }

        @Override
        public String toString() {
            return toString;
        }

    }

    private static final Listener SIGN_LISTENER = new Listener() {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onSignInteract(PlayerInteractEvent event) {
            Block block = event.getClickedBlock();
            if (!(block instanceof Sign))
                return;
            Action action = event.getAction();
            if (!CompareUtil.equalsAny(action, Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK))
                return;
            ExecutableSign execSign = ExecutableSign.getSigns().get(block);
            if (execSign == null)
                return;
            Player player = event.getPlayer();
            if (!execSign.check().test(player))
                return;
            ExecutableSign.ClickAction clickAction = action == Action.LEFT_CLICK_BLOCK ? ExecutableSign.ClickAction.LEFT_CLICK : ExecutableSign.ClickAction.RIGHT_CLICK;
            execSign.execute(player, clickAction);

        }
    };
    private static final Map<Block, ExecutableSign> SIGNS = new HashMap<>();
    private final Block block;
    private Predicate<Player> check = player -> true;
    private Consumer<Player> leftClick = player -> {}, rightClick = player -> {};

    /**
     * @param block the sign block
     * @apiNote In event of the block has been changed after this instance creation, no actions will occur until the block is reverted back to a sign.
     */
    public ExecutableSign(Block block) {
        Preconditions.checkArgument(block instanceof Sign, "provided block is not a sign");
        this.block = block;
        if (SIGNS.isEmpty())
            Bukkit.getPluginManager().registerEvents(SIGN_LISTENER, SimmyGameAPI.instance);
        SIGNS.put(block, this);
    }

    /**
     * Unregisters itself -- having itself no longer an executable sign.
     */
    public void unregister() {
        SIGNS.remove(this.getBlock());
        if (SIGNS.isEmpty())
            HandlerList.unregisterAll(SIGN_LISTENER);
    }

    /**
     * @return the predicate for executing tasks
     */
    public Predicate<Player> check() {
        return check;
    }

    /**
     * @param check the predicate for executing tasks
     * @return itself, for chaining
     */
    public ExecutableSign check(Predicate<Player> check) {
        this.check = check;
        return this;
    }

    /**
     * Invokes a player into this executable sign.
     * @param player the player
     * @param action the action
     */
    public void execute(@NotNull Player player, @NotNull ClickAction action) {
        execute(player, action, false);
    }

    /**
     * Invokes a player into this executable sign.
     * @param player the player
     * @param action the action
     * @param force whether to force execute (ignores {@link #check()})
     */
    public void execute(@NotNull Player player, @NotNull ClickAction action, boolean force) {
        if (!force && !check.test(player))
            return;
        if (action == ClickAction.LEFT_CLICK) {
            leftClick.accept(player);
        } else {
            rightClick.accept(player);
        }
    }

    /**
     * @return the task to run on sign left click
     */
    public Consumer<Player> leftClick() {
        return leftClick;
    }

    /**
     * @param task the task to run on sign click
     * @return this instance
     */
    public ExecutableSign leftClick(Consumer<Player> task) {
        leftClick = task;
        return this;
    }

    /**
     * @return the task to run on sign right click
     */
    public Consumer<Player> rightClick() {
        return rightClick;
    }

    /**
     * @param task the task to run on sign click
     * @return this instance
     */
    public ExecutableSign rightClick(Consumer<Player> task) {
        rightClick = task;
        return this;
    }

    /**
     * @return the sign block
     */
    public Block getBlock() {
        return block;
    }

    /**
     * @return the {@link Sign} instance of the sign block
     */
    public Sign getSign() {
        return (Sign) block.getState();
    }

    /**
     * @return an immutable map of executable signs
     */
    public static Map<Block, ExecutableSign> getSigns() {
        return Map.copyOf(SIGNS);
    }

}

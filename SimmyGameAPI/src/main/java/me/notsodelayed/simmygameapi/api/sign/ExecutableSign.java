package me.notsodelayed.simmygameapi.api.sign;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        SIGNS.put(block, this);
    }

    /**
     * @return an immutable map of executable signs
     */
    public static Map<Block, ExecutableSign> getSigns() {
        return Map.copyOf(SIGNS);
    }

    /**
     * @return the predicate for executing tasks
     */
    public Predicate<Player> check() {
        return check;
    }

    /**
     * @param check the predicate for executing tasks
     * @return
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

}

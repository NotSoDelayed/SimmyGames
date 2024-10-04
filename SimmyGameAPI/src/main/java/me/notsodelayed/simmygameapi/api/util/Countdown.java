package me.notsodelayed.simmygameapi.api.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.game.Game;

public class Countdown {

    private final Game game;
    private BukkitTask countdownTask;
    private final AtomicInteger aSeconds = new AtomicInteger(0);
    private final List<BiConsumer<Integer, Game>> pretasks = new ArrayList<>();
    private final Map<Integer, List<BiConsumer<Integer, Game>>> tasks = new HashMap<>();
    private final List<Map.Entry<Predicate<Integer>, BiConsumer<Integer, Game>>> predicateTasks = new ArrayList<>();

    public Countdown(Game game) {
        this.game = game;
    }

    /**
     * @param seconds the seconds to execute this task
     * @param task the task
     * @return itself
     */
    public Countdown executeAt(int seconds, BiConsumer<Integer, Game> task) {
        tasks.computeIfAbsent(seconds, s -> new ArrayList<>())
                .add(task);
        return this;
    }

    /**
     * @param task the task to execute upon {@link #start(int)}
     * @return itself
     */
    public Countdown executeOnActive(BiConsumer<Integer, Game> task) {
        pretasks.add(task);
        return this;
    }

    /**
     * @param condition the condition to check against the timer for running this task
     * @param task the task
     * @return itself
     */
    public Countdown executeAt(Predicate<Integer> condition, BiConsumer<Integer, Game> task) {
        predicateTasks.add(Map.entry(condition, task));
        return this;
    }

    /**
     * @param task the task to execute once <b>{@link #getSeconds()} == 0</b>
     * @return itself
     */
    public Countdown executeAfterDepletes(BiConsumer<Integer, Game> task) {
        return executeAt(0, task);
    }

    public void start(int seconds) {
        if (isActive())
            return;
        Preconditions.checkArgument(seconds > 0, "seconds must be more than 0");
        aSeconds.set(seconds);
        for (BiConsumer<Integer, Game> task : pretasks)
            task.accept(aSeconds.get(), game);
        countdownTask = Bukkit.getScheduler().runTaskTimer(SimmyGameAPI.instance, () -> {
            List<BiConsumer<Integer, Game>> tasks = this.tasks.get(aSeconds.get());
            if (!tasks.isEmpty())
                tasks.forEach(task -> task.accept(aSeconds.get(), game));
            for (Map.Entry<Predicate<Integer>, BiConsumer<Integer, Game>> entry : predicateTasks) {
                if (entry.getKey().test(aSeconds.get()))
                    entry.getValue().accept(aSeconds.get(), game);
            }
            if (aSeconds.getAndDecrement() == 0) {
                cancel();
            }
        }, 0, 20);
    }

    public void cancel() {
        countdownTask.cancel();
        countdownTask = null;
    }

    /**
     * @return if <b>{@link #isActive()} = true</b>, the current seconds of this countdown, else null
     */
    @Nullable
    public Integer getSeconds() {
        if (isActive())
            return aSeconds.get();
        return null;
    }

    public boolean isActive() {
        return countdownTask != null;
    }

    /**
     * @return the total amount of tasks registered in this countdown
     */
    public int tasksCount() {
        return tasks.size() + predicateTasks.size();
    }

}

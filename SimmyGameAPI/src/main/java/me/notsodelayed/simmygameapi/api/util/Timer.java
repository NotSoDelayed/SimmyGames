package me.notsodelayed.simmygameapi.api.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.scheduler.BukkitTask;

public abstract class Timer {

    protected BukkitTask timerTask;
    protected final AtomicInteger aSeconds = new AtomicInteger(0);
    protected final List<Consumer<Integer>> pretasks = new ArrayList<>();
    protected final Map<Integer, List<Consumer<Integer>>> tasks = new HashMap<>();
    protected final List<Map.Entry<Predicate<Integer>, Consumer<Integer>>> predicateTasks = new ArrayList<>();

    protected Timer() {}

    /**
     * @param seconds the seconds to execute this task
     * @param task the task
     * @return itself
     */
    public Timer executeAt(int seconds, Consumer<Integer> task) {
        tasks.computeIfAbsent(seconds, s -> new ArrayList<>())
                .add(task);
        return this;
    }

    /**
     * @param task the task to execute upon active
     * @return itself
     */
    public Timer executeOnActive(Consumer<Integer> task) {
        pretasks.add(task);
        return this;
    }

    /**
     * @param condition the condition to check against the timer for running this task
     * @param task the task
     * @return itself
     */
    public Timer executeAt(Predicate<Integer> condition, Consumer<Integer> task) {
        predicateTasks.add(Map.entry(condition, task));
        return this;
    }

    public void cancel() {
        timerTask.cancel();
        timerTask = null;
    }

    /**
     * @return the current seconds of this timer
     * @throws IllegalStateException if the timer is not active
     */
    public int getSeconds() {
        if (!isActive())
            throw new IllegalStateException("timer is not active");
        return aSeconds.get();
    }

    public boolean isActive() {
        return timerTask != null;
    }

    /**
     * @return the total amount of tasks registered in this timer
     */
    public int tasksCount() {
        return tasks.size() + predicateTasks.size();
    }

}

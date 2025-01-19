package me.notsodelayed.simmygameapi.api.util;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;

public class AscendingTimer extends Timer {

    private Instant since, ended;
    private Integer endsAt;

    public AscendingTimer() {
        super();
    }

    /**
     * @param seconds the seconds to end this timer
     * @param task the task to run upon
     * @return itself
     * @apiNote Tasks not stackable
     */
    public AscendingTimer endsAt(int seconds, Consumer<Integer> task) {
        endsAt = seconds;
        return (AscendingTimer) executeAt(seconds, task);
    }

    public void start() {
        if (isActive() || hasEnded())
            return;
        since = Instant.now();
        for (Consumer<Integer> task : pretasks)
            task.accept(aSeconds.get());
        timerTask = SimmyGameAPI.scheduler().runTaskTimer(() -> {
            List<Consumer<Integer>> tasks = this.tasks.get(aSeconds.get());
            if (!tasks.isEmpty())
                tasks.forEach(task -> task.accept(aSeconds.get()));
            for (Map.Entry<Predicate<Integer>, Consumer<Integer>> entry : predicateTasks) {
                if (entry.getKey().test(aSeconds.get()))
                    entry.getValue().accept(aSeconds.get());
            }
            if (endsAt != null && aSeconds.incrementAndGet() == endsAt) {
                this.tasks.get(aSeconds.get()).getFirst().accept(aSeconds.get());
                end();
            }
        }, 0, 20);
    }

    @Override
    public void cancel() {
        super.cancel();
        since = null;
    }

    public void end() {
        super.cancel();
        ended = Instant.now();
    }

    /**
     * @return the time since starting this timer
     * @apiNote Does not return null if {@link #end()} is called
     */
    public @Nullable Instant since() {
        return since;
    }

    /**
     * @return the time since this timer ended
     */
    public @Nullable Instant ended() {
        return ended;
    }

    /**
     * @return the seconds to end this timer
     */
    public @Nullable Integer endsAt() {
        return endsAt;
    }

    public boolean hasEnded() {
        return ended != null;
    }

}


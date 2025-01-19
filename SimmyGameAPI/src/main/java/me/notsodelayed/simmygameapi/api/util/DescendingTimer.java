package me.notsodelayed.simmygameapi.api.util;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;

import me.notsodelayed.simmygameapi.SimmyGameAPI;

public class DescendingTimer extends Timer {

    public DescendingTimer() {
        super();
    }

    /**
     * @param task the task to execute upon timer reaching 0 seconds
     * @return itself
     */
    public DescendingTimer executeAtEnd(Consumer<Integer> task) {
        return (DescendingTimer) executeAt(0, task);
    }

    public void start(int seconds) {
        if (isActive())
            return;
        Preconditions.checkArgument(seconds > 0, "seconds must be more than 0");
        aSeconds.set(seconds);
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
            if (aSeconds.getAndDecrement() == 0) {
                cancel();
            }
        }, 0, 20);
    }

}


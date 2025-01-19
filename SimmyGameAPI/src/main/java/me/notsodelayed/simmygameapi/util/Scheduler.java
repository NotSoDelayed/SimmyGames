package me.notsodelayed.simmygameapi.util;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * A wrapper scheduler of Bukkit's.
 */
public class Scheduler {

    private final JavaPlugin plugin;
    
    public Scheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public BukkitTask runTask(Runnable task) {
        return Bukkit.getScheduler().runTask(plugin, task);
    }

    public void runTask(Consumer<? super BukkitTask> task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    public BukkitTask runTaskLater(Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delay);
    }

    public void runTaskLater(Consumer<? super BukkitTask> task, long delay) {
        Bukkit.getScheduler().runTaskLater(plugin, task, delay);
    }

    public BukkitTask runTaskTimer(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
    }

    public void runTaskTimer(Consumer<? super BukkitTask> task, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
    }

    public BukkitTask runTaskAsynchronously(Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin,task);
    }

    public void runTaskAsynchronously(Consumer<? super BukkitTask> task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin,task);
    }

    public BukkitTask runTaskLaterAsynchronously(Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    public void runTaskLaterAsynchronously(Consumer<? super BukkitTask> task, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    public BukkitTask runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    public void runTaskTimerAsynchronously(Consumer<? super BukkitTask> task, long delay, long period) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
    }

}

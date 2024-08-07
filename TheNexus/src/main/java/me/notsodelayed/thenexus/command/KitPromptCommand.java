package me.notsodelayed.thenexus.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.util.MessageUtil;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.kit.NexusKit;
import me.notsodelayed.thenexus.kit.NexusKitManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KitPromptCommand implements TabExecutor {

    private KitPromptCommand() {}

    public static void register() {
        Bukkit.getPluginCommand("kit").setExecutor(new KitPromptCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtil.sendErrorMessage(sender, "You don't need a kit to begin with.");
            return true;
        }
        GamePlayer gamePlayer = GamePlayer.getFrom(player);
        if (gamePlayer == null) {
            MessageUtil.sendErrorMessage(player, "You must be in game to use this command!");
            return true;
        }
        if (!(gamePlayer instanceof NexusPlayer nexusPlayer))
            return true;
        if (args.length > 0) {
            NexusKit selectedKit = NexusKitManager.get().getKits().get(args[0]);
            if (selectedKit == null) {
                MessageUtil.sendErrorMessage(sender, "Kit '&6" + args[0] + "&c' does not exist!");
                return true;
            }
            nexusPlayer.assignKit(selectedKit);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return null;
        Map<String, NexusKit> kits = NexusKitManager.get().getKits();
        if (kits.isEmpty())
            return null;
        return new ArrayList<>(kits.keySet());
    }

}

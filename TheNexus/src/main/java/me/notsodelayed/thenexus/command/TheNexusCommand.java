package me.notsodelayed.thenexus.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import me.notsodelayed.simmygameapi.api.entity.GamePlayer;
import me.notsodelayed.simmygameapi.api.event.game.GameStartCountdownEvent;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.util.MessageUtil;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.game.NexusGame;
import me.notsodelayed.thenexus.game.NexusGameManager;
import me.notsodelayed.thenexus.kit.NexusKit;
import me.notsodelayed.thenexus.kit.NexusKitManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TheNexusCommand implements TabExecutor {

    private TheNexusCommand() {}

    public static void register() {
        Bukkit.getPluginCommand("thenexus").setExecutor(new TheNexusCommand());
    }

    public static final String PERMISSION_ADMIN = "thenexus.command.admin";

    private void help(CommandSender sender) {
        MessageUtil.sendMessage(sender, "&e&kii&r &6&lTheNexus&e &kii");
        MessageUtil.sendMessage(sender, "&7&o(Implemented with " + Bukkit.getBukkitVersion() + ", Plugin version " + TheNexus.instance.getDescription().getVersion() + ")");
        MessageUtil.sendMessage(sender, "&6➥&r Made with &4&l♥&f by &6DelayedGaming&f ~");
        MessageUtil.sendMessage(sender, "&b➥&r Structured with &b&lstyle&f by &6maxomees&f ~");
        MessageUtil.sendPermissionMessage(sender, PERMISSION_ADMIN, "");
        MessageUtil.sendPermissionMessage(sender, PERMISSION_ADMIN, "&eGet started with &f/thenexus admin");
    }

    // /thenexus game ...

    // TODO we're moving this nigg to Game API in future
    private void game(CommandSender sender, String[] args) {
        if (args.length > 0) {
            Game<?> game;
            String uuid;
            if (args[0].equals("this")) {
                if (!(sender instanceof Player player)) {
                    MessageUtil.sendErrorMessage(sender, "You tryna use '&6this&c' in a console? bruh.");
                    return;
                }
                GamePlayer gamePlayer = GamePlayer.getFrom(player);
                if (gamePlayer == null) {
                    MessageUtil.sendErrorMessage(player, "You are not in a game to use '&6this&c'!");
                    return;
                }
                uuid = gamePlayer.getGame().getUuid().toString();
            } else {
                try {
                    UUID parseUuid = UUID.fromString(args[0]);
                    uuid = parseUuid.toString();
                } catch (IllegalArgumentException ex) {
                    uuid = args[0];
                }
            }
            game = NexusGameManager.get().getGame(uuid);
            if (game == null) {
                MessageUtil.sendErrorMessage(sender, "No game with matching UUID '&6" + uuid + "&c' found.");
                return;
            }
            switch (args[1]) {
                case "start" -> {
                    if (!game.start(GameStartCountdownEvent.StartCause.MANUAL_REQUEST, true)) {
                        MessageUtil.sendErrorMessage(sender, "Unable to force start game " + uuid + "!");
                        return;
                    }
                    MessageUtil.sendSuccessMessage(sender, "Game " + uuid + " requested for force start.");
                }
                case "end", "tie" -> {
                    boolean tie = args[0].equals("tie");
                    game.end(tie);
                    MessageUtil.sendSuccessMessage(sender, "Game " + uuid + " called to " + (tie ? "tie!" : "end!"));
                }
            }
            return;
        }
        MessageUtil.sendMessage(sender, "/thenexus game (this|<portion 1 uuid>|<uuid>) (start|end)");
    }

    // /thenexus kit ...
    private void kit(CommandSender sender, String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "info" -> {
                    if (args.length >= 2) {
                        NexusKit kit = NexusKitManager.get().getKits().get(args[1]);
                        if (kit == null) {
                            MessageUtil.sendErrorMessage(sender, "Kit with id '&6" + args[1] + "&c' not found!");
                            return;
                        }
                        MessageUtil.sendMessage(sender, "&6&l|&r " + kit.getOptionalDisplayName().orElse(kit.getId()), kit.toString());
                    }
                }
                case "list" -> {
                    MessageUtil.sendMessage(sender, "Kits: &a(" + NexusKitManager.get().getKits().size() + ")");
                    for (NexusKit kit : NexusKitManager.get().getKits().values()) {
                        MessageUtil.sendMessage(sender, "&7- &e" + kit.getDisplayName());
                    }
                }
                case "reload" -> {
                    if (!NexusKitManager.get().reload(sender))
                        MessageUtil.sendErrorMessage(sender, "An ongoing kit loading process is currently active! Please try again later.");
                }
            }
            return;
        }
        MessageUtil.sendMessage(sender, "/thenexus kit &c(list|reload) &7...");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission(PERMISSION_ADMIN) && args.length >= 1) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            if (args[0].equals("kit")) {
                kit(sender, subArgs);
            }
            else if (args[0].equals("game")) {
                game(sender, subArgs);
            }
            return true;
        }
        help(sender);
        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1)
            return List.of("game", "kit");
        if (args[0].equals("game")) {
            if (args.length == 2) {
                // smart caching needed
                return NexusGameManager.get().getGames().keySet().stream()
                        .map(UUID::toString)
                        .toList();
            }
            if (args.length == 3)
                return List.of("start", "end", "tie");
        }
        if (args[0].equals("kit")) {
            if (args.length == 2)
                return List.of("info", "reload");
            if (args.length == 3 && args[1].equals("info")) {
                return new ArrayList<>(NexusKitManager.get().getKits().keySet());
            }
        }
        return null;
    }

}

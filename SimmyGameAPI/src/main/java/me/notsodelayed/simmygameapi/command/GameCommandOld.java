package me.notsodelayed.simmygameapi.command;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.notsodelayed.simmygameapi.api.Matchmaking;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.util.CommandUtil;
import me.notsodelayed.simmygameapi.util.MessageUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;
import me.notsodelayed.simmygameapi.util.Symbol;

public class GameCommandOld extends BaseCommandOld {

    public GameCommandOld(String label) {
        super(label);
    }

    // /game (this|<uuid>) (start|end)
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        if (sender.hasPermission("simmygameapi.game.admin")) {
            if (strings.length > 0) {
                if (strings[0].equalsIgnoreCase("create")) {
                    if (strings.length < 2) {
                        MessageUtil.sendErrorMessage(sender, "Please specify a game type to create.");
                        return true;
                    }
                    Game game = Matchmaking.newGame(strings[2]);
                    if (game != null) {
                        sender.sendMessage(StringUtil.color("&aA new " + game.getClass().getSimpleName() + " has been created!"), StringUtil.color("UUID: &a" + game.getUuid()));
                        if (sender instanceof Player)
                            sender.sendMessage(Component.text(Symbol.ARROW_DOWN + "Click here to join").clickEvent(ClickEvent.suggestCommand("/game join " + game.getUuid())));
                    }
                    return true;
                }

                // /game join
                if (strings[0].equalsIgnoreCase("join")) {
                    if (strings.length > 1) {
                        if (!(sender instanceof Player player)) {
                            MessageUtil.playerOnly(sender);
                            return true;
                        }
                        String uuidArg = strings[2];
                        GamePlayer gamePlayer = GamePlayer.get(player);
                        if (gamePlayer != null) {
                            MessageUtil.sendErrorMessage(player, "You are already in a game.");
                            return true;
                        }
                        Game game = CommandUtil.parseGame(player, uuidArg);
                        if (game == null)
                            return true;
                        BiConsumer<Player, Game> joinTask = (BiConsumer<Player, Game>) Matchmaking.getJoinTask(game.getClass());
                        if (joinTask == null) {
                            MessageUtil.sendErrorMessage(player, "Unfortunately, the game core does not know how to join you into a " + game.getClass().getSimpleName() + " game.", "Please contact the server administrator regarding this.");
                            return true;
                        }
                        sender.sendMessage(StringUtil.color("&aJoining " + game.getClass().getSimpleName() + "-" + StringUtil.getDisplayUuid(game.getUuid()) + "..."));
                        joinTask.accept(player, game);
                        return true;
                    }
                    MessageUtil.sendInfoMessage(sender, "&e/game join (<uuid>|<portion 1 uuid>) (start|end)");
                    return true;
                }

                if (strings[0].equalsIgnoreCase("control")) {
                    if (strings.length > 2) {
                        Game game;
                        String uuidArg = strings[1];
                        if (uuidArg.equals("this")) {
                            if (!CommandUtil.isPlayer(sender))
                                return true;

                            GamePlayer gamePlayer = GamePlayer.get((Player) sender);
                            if (gamePlayer == null) {
                                MessageUtil.sendErrorMessage(sender, "You must be in a game to use 'this'.");
                                return true;
                            }

                            game = gamePlayer.getGame();
                        } else {
                            game = CommandUtil.parseGame(sender, uuidArg);
                            if (game == null)
                                return true;
                        }

                        if (strings.length > 3) {
                            String taskArg = strings[1];
                            switch (taskArg) {
                                case "start" -> {
                                    if (!game.hasBegun()) {
                                        MessageUtil.sendInfoMessage(sender, "This game has already started.");
                                        return true;
                                    }
                                    MessageUtil.sendSuccessMessage(sender, "This game has been requested to force start.");
                                    game.start(true);
                                    return true;
                                }
                                case "end" -> {
                                    if (!game.hasEnded()) {
                                        MessageUtil.sendInfoMessage(sender, "This game has already ended.");
                                        return true;
                                    }
                                    MessageUtil.sendSuccessMessage(sender, "This game has been requested to end.");
                                    game.end();
                                    return true;
                                }
                            }
                            MessageUtil.sendInfoMessage(sender, "&e/game control (this|<uuid>|<portion 1 uuid>) &6(start|end)");
                            return true;
                        }
                    }
                    MessageUtil.sendInfoMessage(sender, "&e/game control (this|<uuid>|<portion 1 uuid>) (start|end)");
                    return true;
                }
            }
            sender.sendMessage("-- game command");
            return true;
        }
        sender.sendMessage("Powered by SimmyGameAPI!");
        return true;
    }

    /**
     * Set this to null to update the cache.
     */
    public static List<String> UUIDS_CACHE = null;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] strings) {
        if (!CommandUtil.hasPermission(sender, command.getPermission()))
            return null;
        if (strings[0].equalsIgnoreCase("create"))
            return Matchmaking.getRegisteredGames();
        if (strings[0].equalsIgnoreCase("control") || strings[0].equalsIgnoreCase("join")) {
            if (strings.length > 1) {
                if (strings[0].equalsIgnoreCase("control") && strings.length > 2)
                    return List.of("start", "end");
                if (UUIDS_CACHE == null)
                    UUIDS_CACHE = Game.getGames().keySet().stream()
                            .map(UUID::toString)
                            .toList();
                return UUIDS_CACHE;
            }
        }
        return null;
    }

}

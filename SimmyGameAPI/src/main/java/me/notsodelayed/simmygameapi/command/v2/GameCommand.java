package me.notsodelayed.simmygameapi.command.v2;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Matchmaking;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.util.ComponentUtil;
import me.notsodelayed.simmygameapi.util.MessageUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;
import me.notsodelayed.simmygameapi.util.Symbol;

public class GameCommand {

    public GameCommand(String label) {
        CommandAPICommand gameCreate = new CommandAPICommand("create")
                .withPermission(SimmyGameAPI.ADMIN_PERMISSION)
                .withArguments(new StringArgument("gametype"))
                .executes((sender, args) -> {
                   String gametype = (String) args.get("gametype");

                   // If game type is not specified
                   if (gametype == null) {
                       MessageUtil.sendInfoMessage(sender, "Please specify a game type to create:");
                       Component clickGameTypes = Component.text(Symbol.ARROW_DOWN, NamedTextColor.DARK_GRAY)
                               .appendSpace();
                       List<String> registered = Matchmaking.getRegisteredGames();
                       for (int i = 0; i < registered.size(); i++) {
                           String type = registered.get(i);
                           clickGameTypes = clickGameTypes.append(
                                   Component.text(type)
                                           .hoverEvent(HoverEvent.showText(Component.text(type)))
                                           .clickEvent(ClickEvent.suggestCommand("/" + label + " create " + type))
                           );
                           if (i + 1 != registered.size())
                               clickGameTypes = clickGameTypes.append(Component.text(", ", NamedTextColor.GRAY));
                       }
                       sender.sendMessage(clickGameTypes);
                       return;
                   }
                   Game game = Matchmaking.newGame(gametype);
                   if (game == null) {
                       MessageUtil.sendErrorMessage(sender, "Invalid game type '&f" + gametype + "&r' to create.");
                       return;
                   }
                   Component successMessage = ComponentUtil.successMessage("Created a new game of " + gametype + "!");
                   if (sender instanceof Player)
                           successMessage = successMessage.appendSpace().append(
                                   Component.text(StringUtil.smallText("join?"), NamedTextColor.GREEN)
                                           .clickEvent(ClickEvent.suggestCommand("/" + label + " join " + game.getUuid()))
                           );
                    sender.sendMessage(successMessage);
                });
        CommandAPICommand gameJoin = new CommandAPICommand("join")
                .withArguments(new StringArgument("uuid"))
                .executesPlayer((player, args) -> {
                    String inputUuid = (String) args.get("uuid");
                    if (inputUuid == null) {
                        player.sendMessage(ComponentUtil.infoMessage("Please specify a UUID of a game to join."));
                        return;
                    }
                    Game game = Game.getGame(inputUuid);
                    if (game == null) {
                        player.sendMessage(ComponentUtil.errorMessage("No matching game of UUID " + inputUuid + "."));
                        return;
                    }
                    //noinspection unchecked
                    BiConsumer<Player, Game> joinTask = ((BiConsumer<Player, Game>) Matchmaking.getJoinTask(game.getClass()));
                    if (joinTask == null) {
                        player.sendMessage(ComponentUtil.errorMessage("This game does not support joining via commands."));
                        return;
                    }
                    if (!game.isJoinable()) {
                        if (game.isFull() && !player.hasPermission(SimmyGameAPI.ADMIN_PERMISSION)) {
                            player.sendMessage(ComponentUtil.errorMessage("This game is full."));
                        } else {
                            player.sendMessage(ComponentUtil.errorMessage("This game is not joinable at this time."));
                        }
                        return;
                    }
                    player.sendMessage(ComponentUtil.infoMessage("Joining " + StringUtil.getDisplayUuid(game.getUuid()) + "..."));
                    joinTask.accept(player, game);
                });
        CommandAPICommand gameList = new CommandAPICommand("list")
                .withPermission(SimmyGameAPI.ADMIN_PERMISSION)
                .executes((sender, args) -> {
                    Collection<Game> games = Game.getGames().values();
                    sender.sendMessage(Component.text("Active games: (").append(Component.text(games.size(), NamedTextColor.GREEN).append(Component.text(")", NamedTextColor.WHITE))));
                    games.stream().sorted(Comparator.comparingLong(Game::createdAt).reversed())
                            .forEachOrdered(game -> {
                                Component gameInfo = Component.text(Symbol.ARROW_DOWN, game.getGameState().getColor())
                                        .appendSpace()
                                        .append(Component.text(game.getFormattedName(), NamedTextColor.WHITE))
                                        .hoverEvent(HoverEvent.showText(Component.text(game.getFormattedName()).appendSpace().append(Component.text(StringUtil.smallText(game.getGameState().toString()), game.getGameState().getColor()))))
                                        .clickEvent(ClickEvent.suggestCommand("/" + label + " " + gameJoin.getName() + " " + game.getUuid()));
                                sender.sendMessage(gameInfo);
                            });
                });
        CommandAPICommand gameCommand = new CommandAPICommand(label)
                .withSubcommands(gameCreate, gameList, gameJoin);
        gameCommand.executes((sender, args) -> {
            sender.sendMessage(ComponentUtil.successMessage("Game command."));
            gameCommand.getSubcommands().forEach(sub -> sender.sendPlainMessage("- /" + label + " " + sub.getName()));
        });
        gameCommand.register();
    }

}

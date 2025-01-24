package me.notsodelayed.simmygameapi.commands;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Matchmaking;
import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.util.ComponentUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;
import me.notsodelayed.simmygameapi.util.Symbol;

public class GameCommand {

    /**
     * Helper function for handling game UUID argument
     */
    private static final BiFunction<CommandSender, String, @Nullable Game> PARSE_GAME = (sender, uuid) -> {
        if (uuid.equals("this")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ComponentUtil.errorMessage("You are a console. Consider specifying a game UUID to proceed. ;)"));
                return null;
            }
            GamePlayer gamePlayer = GamePlayer.get(player);
            if (gamePlayer == null) {
                sender.sendMessage(ComponentUtil.errorMessage("You must be in a game to use keyword '<white>this<red>'."));
                return null;
            }
            return gamePlayer.getGame();
        }
        Game game = Game.getGame(uuid);
        if (game == null)
            sender.sendMessage(ComponentUtil.errorMessage("No matching game of UUID '<white>" + sender + "<red>."));
        return game;
    };

    public GameCommand(String label) {
        ArgumentSuggestions<CommandSender> suggestionsActiveGames = ArgumentSuggestions.strings(info -> Game.getGames().keySet().stream().map(UUID::toString).toArray(String[]::new));

        CommandAPICommand gameCreate = new CommandAPICommand("create")
                .withPermission(SimmyGameAPI.ADMIN_PERMISSION)
                .withArguments(
                        new StringArgument("gametype").replaceSuggestions(ArgumentSuggestions.strings(info -> Matchmaking.getRegisteredGamesList().toArray(new String[0]))))
                .executes((sender, args) -> {
                   String gameType = (String) args.get("gametype");

                   // If game type is not specified
                   if (gameType == null) {
                       sender.sendMessage(ComponentUtil.infoMessage("Please specify a game type to create:"));
                       Component clickGameTypes = Component.text(Symbol.ARROW_DOWN, NamedTextColor.DARK_GRAY)
                               .appendSpace();
                       List<String> registered = Matchmaking.getRegisteredGamesList();
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
                   Game game = Matchmaking.newGame(gameType);
                   if (game == null) {
                       sender.sendMessage(ComponentUtil.errorMessage("Invalid game type: ").append(Component.text(gameType, NamedTextColor.WHITE)));
                       return;
                   }
                   Component successMessage = ComponentUtil.successMessage("Created a new game of " + game.getClass().getSimpleName() + "!");
                   if (sender instanceof Player)
                           successMessage = successMessage.appendSpace().append(
                                   Component.text(StringUtil.smallText("join?"), NamedTextColor.GREEN)
                                           .hoverEvent(HoverEvent.showText(Component.text("Click to join " + game.getFormattedName())))
                                           .clickEvent(ClickEvent.suggestCommand("/" + label + " join " + game.getUuid()))
                           );
                    sender.sendMessage(successMessage);
                });
        CommandAPICommand gameJoin = new CommandAPICommand("join")
                .withPermission(SimmyGameAPI.ADMIN_PERMISSION)
                .withArguments(new StringArgument("uuid").replaceSuggestions(suggestionsActiveGames))
                .withOptionalArguments(new PlayerArgument("targetplayer"))
                .executes((sender, args) -> {
                    String uuidInput = (String) args.get("uuid");
                    Game game = PARSE_GAME.apply(sender, uuidInput);
                    if (game == null)
                        return;
                    Player target = (Player) args.get("targetplayer");
                    if (target == null) {
                        if (!(sender instanceof Player player)) {
                            sender.sendMessage(ComponentUtil.errorMessage("Please specify a player to join " + game.getFormattedName() + "."));
                            return;
                        }
                        target = player;
                    }
                    if (GamePlayer.get(target) != null) {
                        // TODO fix
                        sender.sendMessage(ComponentUtil.errorMessage("You are already in a game!"));
                        return;
                    }
                    //noinspection unchecked
                    BiConsumer<Player, Game> joinTask = ((BiConsumer<Player, Game>) Matchmaking.getJoinTask(game.getClass()));
                    if (joinTask == null) {
                        sender.sendMessage(ComponentUtil.errorMessage("This game does not support joining via commands."));
                        return;
                    }
                    if (!game.isJoinable()) {
                        if (game.isFull() && !target.hasPermission(SimmyGameAPI.ADMIN_PERMISSION)) {
                            sender.sendMessage(ComponentUtil.errorMessage("This game is full."));
                        } else {
                            sender.sendMessage(ComponentUtil.errorMessage("This game is not joinable at this time."));
                        }
                        return;
                    }
                    target.sendMessage(ComponentUtil.infoMessage("Joining " + game.getFormattedName() + "..."));
                    joinTask.accept(target, game);
                    if (!sender.equals(target)) {
                        GamePlayer gamePlayer = GamePlayer.get(target);
                        if (gamePlayer != null && gamePlayer.getGame().equals(game))
                            sender.sendMessage(ComponentUtil.successMessage("Sent " + target.getName() + " to " + game.getFormattedName() + "!"));
                    }
                });
        CommandAPICommand gameStart = new CommandAPICommand("start")
                .withPermission(SimmyGameAPI.ADMIN_PERMISSION)
                .withArguments(new StringArgument("uuid").replaceSuggestions(suggestionsActiveGames))
                .executes((sender, args) -> {
                    String uuidInput = (String) args.get("uuid");
                    if (uuidInput == null) {
                        sender.sendMessage(ComponentUtil.infoMessage("Please specify a UUID of a game to force start."));
                        return;
                    }
                    Game game = PARSE_GAME.apply(sender, uuidInput);
                    if (game == null)
                        return;
                    if (game.isAboutToStart() || game.hasBegun()) {
                        sender.sendMessage(ComponentUtil.errorMessage(game.getFormattedName() + " has already started."));
                        return;
                    }
                    sender.sendMessage(ComponentUtil.successMessage("Force starting " + game.getFormattedName() + "..."));
                    game.start(true);
                });
        CommandAPICommand gameStop = new CommandAPICommand("end")
                .withPermission(SimmyGameAPI.ADMIN_PERMISSION)
                .withArguments(
                        new StringArgument("uuid").replaceSuggestions(suggestionsActiveGames))
                .executes((sender, args) -> {
                    String uuidInput = (String) args.get("uuid");
                    Game game = PARSE_GAME.apply(sender, uuidInput);
                    if (game == null)
                        return;
                    if (!game.hasBegun()) {
                        sender.sendMessage(ComponentUtil.errorMessage(game.getFormattedName() + " has not begin."));
                        return;
                    }
                    sender.sendMessage(ComponentUtil.successMessage("Ending " + game.getFormattedName() + "..."));
                    game.end();
                });
        CommandAPICommand gameInfo = new CommandAPICommand("info")
                .withPermission(SimmyGameAPI.ADMIN_PERMISSION)
                .withArguments(
                        new StringArgument("uuid").replaceSuggestions(suggestionsActiveGames))
                .executes((sender, args) -> {
                    String uuidInput = (String) args.get("uuid");
                    Game game = PARSE_GAME.apply(sender, uuidInput);
                    if (game == null)
                        return;
                    game.showInfo(sender);
                });
        CommandAPICommand gameList = new CommandAPICommand("list")
                .withPermission(SimmyGameAPI.ADMIN_PERMISSION)
                .executes((sender, args) -> {
                    Collection<Game> games = Game.getGames().values();
                    sender.sendMessage(Component.text("Active games: (").append(Component.text(games.size(), NamedTextColor.GREEN).append(Component.text(")", NamedTextColor.WHITE))));
                    games.stream().sorted(Comparator.comparingLong(Game::createdAt).reversed())
                            .forEachOrdered(game -> {
                                Component gameInfoList = Component.text(Symbol.ARROW_DOWN, game.getGameState().getColor())
                                        .appendSpace()
                                        .append(Component.text(game.getFormattedName(), NamedTextColor.WHITE))
                                        .hoverEvent(HoverEvent.showText(Component.text(game.getFormattedName()).appendSpace().append(Component.text(StringUtil.smallText(game.getGameState().toString()), game.getGameState().getColor()))))
                                        .clickEvent(ClickEvent.suggestCommand("/" + label + " " + gameJoin.getName() + " " + game.getUuid()));
                                sender.sendMessage(gameInfoList);
                            });
                });
        CommandAPICommand gameCommand = new CommandAPICommand(label)
                .withSubcommands(gameCreate, gameList, gameStart, gameStop, gameInfo, gameJoin);
        gameCommand.executes((sender, args) -> {
            sender.sendMessage(ComponentUtil.successMessage("Game command."));
            gameCommand.getSubcommands().forEach(sub -> sender.sendPlainMessage("- /" + label + " " + sub.getName()));
        });
        gameCommand.register();
    }

}

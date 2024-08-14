package me.notsodelayed.simmygameapi.command;

import java.util.List;
import java.util.UUID;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.game.event.GameStartCountdownEvent;
import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.util.CommandUtil;
import me.notsodelayed.simmygameapi.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommand extends BaseCommand {

    public GameCommand(String label) {
        super(label);
    }

    // /game (this|<uuid>) (start|end)
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        if (strings.length > 0) {
            Game game;
            String uuidArg = strings[0];
            if (uuidArg.equals("this")) {
                if (!CommandUtil.isPlayer(sender))
                    return true;

                GamePlayer gamePlayer = GamePlayer.getFrom((Player) sender);
                if (gamePlayer == null || gamePlayer.getGame() == null) {
                    MessageUtil.sendErrorMessage(sender, "You must be in a game to use 'this'.");
                    return true;
                }

                game = gamePlayer.getGame();
            } else {
                game = CommandUtil.parseGame(sender, uuidArg);
                if (game == null)
                    return true;
            }

            if (strings.length > 1) {
                String taskArg = strings[1];
                switch (taskArg) {
                    case "start" -> {
                        if (!game.hasBegun()) {
                            MessageUtil.sendInfoMessage(sender, "This game has already started.");
                            return true;
                        }
                        MessageUtil.sendSuccessMessage(sender, "This game has been requested to force start.");
                        game.start(GameStartCountdownEvent.StartCause.MANUAL_REQUEST, true);
                        return true;
                    }
                    case "end" -> {
                        if (!game.hasEnded()) {
                            MessageUtil.sendInfoMessage(sender, "This game has already started.");
                            return true;
                        }
                        MessageUtil.sendSuccessMessage(sender, "This game has been requested to end.");
                        game.end();
                        return true;
                    }
                }
                MessageUtil.sendInfoMessage(sender, "&e/game (this|<uuid>|<portion 1 uuid>) &6(start|end)");
                return true;
            }
        }
        MessageUtil.sendInfoMessage(sender, "&e/game (this|<uuid>|<portion 1 uuid>) (start|end)");
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
        if (strings.length <= 1) {
            if (UUIDS_CACHE == null)
                UUIDS_CACHE = Game.getGames().keySet().stream()
                        .map(UUID::toString)
                        .toList();
            return UUIDS_CACHE;
        }
        if (strings.length == 2)
            return List.of("start", "end");
        return null;
    }

}

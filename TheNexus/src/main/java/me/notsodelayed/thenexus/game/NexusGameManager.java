package me.notsodelayed.thenexus.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.notsodelayed.simmygameapi.api.map.GameMap;
import me.notsodelayed.simmygameapi.api.map.MapChoice;
import me.notsodelayed.simmygameapi.util.Util;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.entity.team.NexusTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages {@link NexusGame} registrations.
 */
public class NexusGameManager {

    private static NexusGameManager nexusGameManager;
    private final Map<UUID, NexusGame<NexusTeam, NexusPlayer>> games = new HashMap<>();

    private NexusGameManager(TheNexus ignored) {}

    public static NexusGameManager get() {
        if (nexusGameManager == null)
            nexusGameManager = new NexusGameManager(TheNexus.instance);
        return nexusGameManager;
    }

    /**
     * @param minPlayers the minimum players
     * @param maxPlayers the maximum players
     * @param mapChoice the map choice
     * @return the newly created nexus game
     */
    public NexusGame<NexusTeam, NexusPlayer> createGame(int minPlayers, int maxPlayers, MapChoice mapChoice) {
        NexusGame<NexusTeam, NexusPlayer> nexusGame = new NexusGame<>(minPlayers, maxPlayers);
        nexusGame.getMapChoice().setMaps(mapChoice);
        games.put(nexusGame.getUuid(), nexusGame);
        return nexusGame;
    }

    /**
     * @param minPlayers the minimum players
     * @param maxPlayers the maximum players
     * @param queriedMap the queried map
     * @return the newly created nexus game
     */
    public NexusGame<NexusTeam, NexusPlayer> createGame(int minPlayers, int maxPlayers, @NotNull GameMap queriedMap) {
        NexusGame<NexusTeam, NexusPlayer> nexusGame = new NexusGame<>(minPlayers, maxPlayers);
        nexusGame.setQueriedGameMap(queriedMap);
        games.put(nexusGame.getUuid(), nexusGame);
        return nexusGame;
    }

    public void removeGame(NexusGame<NexusTeam, NexusPlayer> nexusGame) {
        games.remove(nexusGame.getUuid());
    }

    /**
     * @return a random game, otherwise null if there's none
     */
    @Nullable
    public NexusGame<NexusTeam, NexusPlayer> getRandomGame() {
        if (games.isEmpty())
            return null;
        List<NexusGame<NexusTeam, NexusPlayer>> gamesList = new ArrayList<>(games.values());
        int index = Util.getRandomInt(gamesList.size());
        return gamesList.get(index);
    }

    /**
     * @param uuid the string UUID, or the first portion of a UUID (xxxxxxxx-0000-0000-0000-000000000000)
     * @return the matched nexus game, otherwise null
     */
    @Nullable
    public NexusGame<NexusTeam, NexusPlayer> getGame(String uuid) {
        UUID parsedUuid = null;
        try {
            parsedUuid = UUID.fromString(uuid);
        } catch (IllegalArgumentException ignored) {}
        for (NexusGame<NexusTeam, NexusPlayer> game : games.values()) {
            if (parsedUuid != null) {
                if (game.getUuid().equals(parsedUuid))
                    return game;
            } else {
                if (game.getUuid().toString().split("-")[0].equals(uuid))
                    return game;
            }
        }
        return null;
    }

    /**
     * @param uuid the uuid
     * @return the matched nexus game, otherwise null
     */
    @Nullable
    public NexusGame<NexusTeam, NexusPlayer> getGame(UUID uuid) {
        for (NexusGame<NexusTeam, NexusPlayer> nexusGame : games.values()) {
            if (nexusGame.getUuid().equals(uuid))
                return nexusGame;
        }
        return null;
    }

    /**
     * @return the registered games, mapped by their respective UUIDs
     */
    @NotNull
    public Map<UUID, NexusGame<NexusTeam, NexusPlayer>> getGames() {
        return games;
    }

}

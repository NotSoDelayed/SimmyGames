package me.notsodelayed.simmygameapi.api.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;

public class Poll<T> {

    private final WeakHashMap<Player, T> playerVotes;
    private final Map<T, Integer> votes;

    public Poll() {
        playerVotes = new WeakHashMap<>();
        votes = new HashMap<>();
    }

    /**
     * @param player the caster
     * @param vote the vote
     * @return the new total vote count of the provided vote
     */
    public int castVote(Player player, T vote) {
        T previousVote = playerVotes.get(player);
        if (previousVote != null) {
            int amount = votes.getOrDefault(previousVote, 0);
            if (amount > 0) {
                if (amount - 1 == 0) {
                    // Prevent memory leak
                    votes.remove(previousVote);
                } else {
                    votes.put(previousVote, amount - 1);
                }
            }
        }
        playerVotes.put(player, vote);
        int current = votes.getOrDefault(vote, 0) + 1;
        votes.put(vote, current);
        return votes.get(vote);
    }

    /**
     * @return the map of computed votes, sorted from lowest to highest votes of {@link T}.
     */
    public LinkedHashMap<T, Integer> computeVotes() {
        LinkedHashMap<T, Integer> results = new LinkedHashMap<>();
        playerVotes.values().stream()
                .sorted(Comparator.comparingInt(vote -> votes.getOrDefault(vote, 0)))
                .forEachOrdered(vote -> results.put(vote, votes.getOrDefault(vote, 0)));
        return results;
    }

    public int getVoteAmount(T vote) {
        return votes.getOrDefault(vote, 0);
    }

}

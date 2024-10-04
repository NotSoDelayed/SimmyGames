package me.notsodelayed.thenexus.entity.team;

import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.game.team.GameTeam;

@SuppressWarnings("deprecation")
public class NexusTeam extends GameTeam {

    public NexusTeam(@NotNull NamedTextColor color) {
        super(color);
    }

}

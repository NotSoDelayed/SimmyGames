package me.notsodelayed.thenexus.map;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.game.map.GameMap;
import me.notsodelayed.simmygameapi.api.util.Node;
import me.notsodelayed.simmygameapi.util.DummyLocation;

public class NexusMap extends GameMap {

    private final DummyLocation ALPHA_NEXUS, BETA_NEXUS;
    private final Set<Node<?>> nodes = new HashSet<>();

    public NexusMap(@NotNull String id, @NotNull File mapDirectory) {
        super(id, mapDirectory);
        ALPHA_NEXUS = (DummyLocation) getCachedNodes().get("map.nexus.red");
        BETA_NEXUS = (DummyLocation) getCachedNodes().get("map.nexus.blue");
    }

    @Override
    public Set<Node<?>> getDataNodes() {
        if (nodes.isEmpty()) {
            nodes.add(
                    new Node<>("map").child(
                            new Node<>("nexus")
                                    .child(Node.simple(DummyLocation.class, "red"))
                                    .child(Node.simple(DummyLocation.class, "blue")),
                            new Node<>("spawnpoint")
                                    .child(Node.simple(DummyLocation.class, "red"))
                                    .child(Node.simple(DummyLocation.class, "blue"))
                            )
            );
        }
        return Collections.unmodifiableSet(nodes);
    }
}

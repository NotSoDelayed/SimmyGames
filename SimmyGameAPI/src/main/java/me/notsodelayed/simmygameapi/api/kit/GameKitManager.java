package me.notsodelayed.simmygameapi.api.kit;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

public class GameKitManager<K extends GameKit> {

    private final Set<K> kits = new HashSet<>();
    private @Nullable K defaultKit = null;

    public GameKitManager() {}

    public void registerKit(K kit) {
        kits.add(kit);
    }

    public void unregisterKit(K kit) {
        kits.remove(kit);
    }

    public @Nullable K getDefaultKit() {
        return defaultKit;
    }

    public void setDefaultKit(@Nullable K defaultKit) {
        this.defaultKit = defaultKit;
    }

    public Set<K> getKits() {
        return Set.copyOf(kits);
    }

    public Optional<K> getKitById(String kitId) {
        if (kits.isEmpty())
            return Optional.empty();
        return kits.stream()
                .filter(kit -> kit.getId().equals(kitId))
                .findFirst();
    }

}

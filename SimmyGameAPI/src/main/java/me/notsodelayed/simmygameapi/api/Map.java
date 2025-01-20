package me.notsodelayed.simmygameapi.api;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.util.FileUtil;

public abstract class Map {

    private final String id;
    private final String displayName;
    private final File mapDirectory;

    public Map(@NotNull String id, @Nullable String displayName, @NotNull File mapDirectory) {
        FileUtil.checkIsDirectoryOrThrow(mapDirectory);
        this.id = id;
        this.displayName = displayName;
        this.mapDirectory = mapDirectory;
    }

    public Map(String id, File mapDirectory) {
        this(id, StringUtils.capitalize(id), mapDirectory);
    }

    public String getId() {
        return id;
    }

    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    public File getDirectory() {
        return mapDirectory;
    }

}

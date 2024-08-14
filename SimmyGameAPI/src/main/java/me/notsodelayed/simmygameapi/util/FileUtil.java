package me.notsodelayed.simmygameapi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.simmygameapi.SimmyGameAPI;

/**
 * Utility class for {@link File}.
 */
public class FileUtil {

    /**
     * Ensures that this file exists.
     * @param file the file
     * @throws NullPointerException if the file does not exist
     */
    public static void checkExistsOrThrow(File file) throws NullPointerException {
        if (!file.exists())
            throw new NullPointerException((file.isDirectory() ? "directory " : "file ") + file.getName() + " does not exist");
    }

    /**
     * Ensures that this file is a directory.
     * @param file the file
     * @throws IllegalArgumentException if the file is not a directory
     */
    public static void checkIsDirectoryOrThrow(File file) throws IllegalArgumentException {
        if (!file.isDirectory())
            throw new IllegalArgumentException("file " + file.getName() + " is not a directory");
    }

    /**
     * @param file the non-directory file
     * @param extension the extension to check (i.e. yml, .yml, etc)
     * @throws IllegalArgumentException if the file is a directory, or does not match the provided extension
     */
    public static void checkFileExtensionOrThrow(File file, String extension) {
        if (!extension.startsWith("."))
            extension = "." + extension;
        if (file.isDirectory())
            throw new IllegalArgumentException("file " + file.getName() + " is a directory (expected " + extension + ")");
        if (!file.getName().endsWith(extension))
            throw new IllegalArgumentException("file " + file.getName() + " is not a " + extension + " file");
    }

    public static File getFileOfPlugin(JavaPlugin plugin) {
        try {
            Method method = (JavaPlugin.class).getDeclaredMethod("getFile");
            method.setAccessible(true);
            return (File) method.invoke(plugin);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Error while getting the file of a plugin.", e);
        }
    }

    /**
     * @param in the input stream
     * @param file the file
     * @throws IOException if an IO exception occurred
     */
    public static void saveFromInputStream(InputStream in, final File file) throws IOException {
        file.getParentFile().mkdirs();
        try (FileOutputStream out = new FileOutputStream(file)) {
            byte[] buffer = new byte[16 * 1024];
            int read;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
        }
    }

    public static CompletableFuture<Void> generateEmbeddedFiles(JavaPlugin plugin, String fileExtension, String... directoryPaths) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        LoggerUtil.debug("Running directoryAutoGenerateTask for " + plugin.getName());
        Logger logger = SimmyGameAPI.logger;
        for (String path : directoryPaths) {
            if (path.startsWith("/"))
                path = path.substring(1, path.length() - 1);
            if (path.endsWith("/"))
                path = path.substring(0, path.length() - 2);
            if (fileExtension.startsWith("."))
                fileExtension = fileExtension.substring(1);
            try {
                File pluginFile = getFileOfPlugin(plugin);
                ZipFile jar = new ZipFile(pluginFile);
                LoggerUtil.debug("Created ZipJar " + jar); // debug // debug
                LoggerUtil.debug("- L/R: " + path + "/" + fileExtension); // debug
                File contentDir = new File(plugin.getDataFolder(), path);
                if (contentDir.isDirectory() && contentDir.listFiles().length > 0) {
                    LoggerUtil.debug("Skipping: empty kit directory"); // debug
                    continue;
                }
                logger.info(String.format("Generating default-embedded %s...", path));
                Iterator<ZipEntry> iterator = (Iterator<ZipEntry>) jar.stream().iterator();
                while (iterator.hasNext()) {
                    ZipEntry entry = iterator.next();
                    if (entry.isDirectory())
                        continue;
                    if (entry.getName().startsWith(path + "/") && entry.getName().endsWith("." + fileExtension)) {
                        LoggerUtil.debug("Auto-generating " + entry.getName()); // debug
                        FileUtil.saveFromInputStream(jar.getInputStream(entry), new File(plugin.getDataFolder(), "kits" + File.separator + (entry.getName().split("/")[1])));
                    }
                    jar.close();
                }
            } catch (IOException ex) {
                logger.warning(String.format("Unable to generate default files for %s:", plugin.getName()));
                ex.printStackTrace(System.err);
            }
        }

        return future;
    }

}

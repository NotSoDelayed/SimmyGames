package me.notsodelayed.simmygameapi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

}

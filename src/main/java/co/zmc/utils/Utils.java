package co.zmc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import co.zmc.IndigoLauncher;

public class Utils {

    public static enum OS {
        WINDOWS, UNIX, MACOSX, OTHER,
    }

    public static String getDefInstallPath() {
        try {
            CodeSource codeSource = Utils.class.getProtectionDomain().getCodeSource();
            File jarFile;
            jarFile = new File(codeSource.getLocation().toURI().getPath());
            return jarFile.getParentFile().getPath();
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Failed to get path for current directory - falling back to user's home directory.");
        return System.getProperty("user.dir") + "//" + getTitle() + "_install";
    }

    private static String getTitle() {
        return IndigoLauncher.TITLE.toLowerCase().replaceAll(" ", "_");
    }

    public static String getDynamicStorageLocation() {
        switch (getCurrentOS()) {
            case WINDOWS:
                return System.getenv("APPDATA") + "/" + getTitle() + "/";
            case MACOSX:
                return System.getProperty("user.home") + "/Library/Application Support/" + getTitle() + "/";
            case UNIX:
                return System.getProperty("user.home") + "/." + getTitle() + "/";
            default:
                return getDefInstallPath() + "/temp/";
        }
    }

    public static String getJavaDelimiter() {
        switch (getCurrentOS()) {
            case WINDOWS:
                return ";";
            case UNIX:
                return ":";
            case MACOSX:
                return ":";
            default:
                return ";";
        }
    }

    public static OS getCurrentOS() {
        String osString = System.getProperty("os.name").toLowerCase();
        if (osString.contains("win")) {
            return OS.WINDOWS;
        } else if (osString.contains("nix") || osString.contains("nux")) {
            return OS.UNIX;
        } else if (osString.contains("mac")) {
            return OS.MACOSX;
        } else {
            return OS.OTHER;
        }
    }

    public static void removeMetaInf(String filePath) {
        File inputFile = new File(filePath, "minecraft.jar");
        File outputTmpFile = new File(filePath, "minecraft.jar.tmp");
        try {
            JarInputStream input = new JarInputStream(new FileInputStream(inputFile));
            JarOutputStream output = new JarOutputStream(new FileOutputStream(outputTmpFile));
            JarEntry entry;

            while ((entry = input.getNextJarEntry()) != null) {
                if (entry.getName().contains("META-INF")) {
                    continue;
                }
                output.putNextEntry(entry);
                byte buffer[] = new byte[1024];
                int amo;
                while ((amo = input.read(buffer, 0, 1024)) != -1) {
                    output.write(buffer, 0, amo);
                }
                output.closeEntry();
            }

            input.close();
            output.close();

            if (!inputFile.delete()) {
                System.out.println("Failed to delete Minecraft.jar.");
                return;
            }
            outputTmpFile.renameTo(inputFile);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

package co.zmc.projectindigo.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import co.zmc.projectindigo.IndigoLauncher;

public class Utils {

    public static enum OS {
        WINDOWS, UNIX, MACOSX, OTHER,
    }

    private static Logger logger = Logger.getLogger("launcher");

    public static String getDefInstallPath() {
        try {
            CodeSource codeSource = Utils.class.getProtectionDomain().getCodeSource();
            File jarFile;
            jarFile = new File(codeSource.getLocation().toURI().getPath());
            return jarFile.getParentFile().getPath();
        } catch (URISyntaxException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
        logger.log(Level.WARNING, "Failed to get path for current directory - falling back to user's home directory.");
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
                logger.log(Level.SEVERE, "Failed to delete Minecraft.jar.");
                return;
            }
            outputTmpFile.renameTo(inputFile);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public static String readURL(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null) reader.close();
        }
    }

    public static BufferedImage loadCachedImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
        }
        return null;
    }

    public static int[][] getDynamicTableCoords(int totalAreaWidth, int totalAreaHeight, int numData) {
        int numPerRow = 3;
        int[][] tableCoords = Utils.getTableCoords(numPerRow, totalAreaWidth, totalAreaHeight, numData);

        while (tableCoords[tableCoords.length - 1][1] + tableCoords[tableCoords.length - 1][3] > totalAreaHeight) {
            numPerRow++;
            tableCoords = Utils.getTableCoords(numPerRow, totalAreaWidth, totalAreaHeight, numData);
        }
        return tableCoords;
    }

    public static final int MAX_TILE_WIDTH = 250;

    private static int[][] getTableCoords(int numPerRow, int totalAreaWidth, int totalAreaHeight, int numData) {
        int[][] table = new int[numData][4];

        int tileWidth = (int) ((double) (totalAreaWidth / numPerRow) * 0.6D);
        if (tileWidth > MAX_TILE_WIDTH) {
            tileWidth = MAX_TILE_WIDTH;
        }
        int cellPaddingX = (totalAreaWidth - tileWidth) / numPerRow;
        int cellPaddingY = cellPaddingX / 3;
        int tileHeight = tileWidth;
        int numRows = numData / numPerRow;
        int modX = cellPaddingX / 5;
        for (int i = 0; i < numData; i++) {
            int row = (i / numPerRow);
            int col = (i % numPerRow);
            int numColOnCurrRow = (numRows == row ? numData % numPerRow : numPerRow);

            table[i] = new int[] { modX + (totalAreaWidth / 2) + (tileWidth * col) + (((cellPaddingX / 2) * col)) - (((tileWidth + (cellPaddingX / 2)) * numColOnCurrRow) / 2),
                    (tileHeight * row) + ((cellPaddingY * row) / 2), tileWidth, tileHeight };
        }
        return table;
    }

    public static String getClassContainer(Class<?> c) {
        if (c == null) { throw new NullPointerException("The Class passed to this method may not be null"); }
        try {
            while (c.isMemberClass() || c.isAnonymousClass()) {
                c = c.getEnclosingClass(); // Get the actual enclosing file
            }
            if (c.getProtectionDomain().getCodeSource() == null) {
                // This is a proxy or other dynamically generated class, and has
                // no physical container,
                // so just return null.
                return null;
            }
            String packageRoot;
            try {
                // This is the full path to THIS file, but we need to get the
                // package root.
                String thisClass = c.getResource(c.getSimpleName() + ".class").toString();
                packageRoot = replaceLast(thisClass, Pattern.quote(c.getName().replaceAll("\\.", "/") + ".class"), "");
                if (packageRoot.endsWith("!/")) {
                    packageRoot = replaceLast(packageRoot, "!/", "");
                }
            } catch (Exception e) {
                // Hmm, ok, try this then
                packageRoot = c.getProtectionDomain().getCodeSource().getLocation().toString();
            }
            packageRoot = URLDecoder.decode(packageRoot, "UTF-8");
            return packageRoot;
        } catch (Exception e) {
            throw new RuntimeException("While interrogating " + c.getName() + ", an unexpected exception was thrown.", e);
        }
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    public static String getRedirectedUrl(String url) throws MalformedURLException, IOException {
        HttpURLConnection connection;
        String finalUrl = url;
        do {
            connection = (HttpURLConnection) new URL(finalUrl).openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode >= 300 && responseCode < 400) {
                String redirectedUrl = connection.getHeaderField("Location");
                if (redirectedUrl == null) break;
                finalUrl = redirectedUrl;
            } else break;
        } while (connection.getResponseCode() != HttpURLConnection.HTTP_OK);
        connection.disconnect();
        return finalUrl.replaceAll(" ", "%20");
    }
}

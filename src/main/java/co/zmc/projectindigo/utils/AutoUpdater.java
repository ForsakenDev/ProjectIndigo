package co.zmc.projectindigo.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.Main;
import co.zmc.projectindigo.utils.Utils.OS;

public class AutoUpdater {
    private static Logger logger = Logger.getLogger("launcher");

    public static void main(String[] args) {
        if (shouldUpdate()) {
            if (downloadNew()) {
                logger.log(Level.INFO, "Download done.");
                FileUtils.deleteDirectory(new File(DirectoryLocations.DATA_DIR_LOCATION, "servers"));
                relaunch();
                System.exit(0);
                return;
            }
        }
        new IndigoLauncher((args.length == 1 ? args[0] : ""));
    }

    private static void relaunch() {
        String javaDir = System.getProperty("java.home") + "/bin/java";
        String classpath = System.getProperty("java.class.path");
        String className = Main.class.getCanonicalName();

        try {
            new ProcessBuilder(javaDir, "-cp", classpath, className).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean shouldUpdate() {
        try {
            URL url = new URL("https://raw.github.com/ZephyrMC-Dev/ProjectIndigo/master/pom.xml");

            URLConnection connection = url.openConnection();

            Document doc = parseXML(connection.getInputStream());
            NodeList descNodes = doc.getElementsByTagName("project");

            String version = (String) ((Element) descNodes.item(0)).getElementsByTagName("version").item(0).getChildNodes().item(0).getNodeValue();

            InputStream pomStream = AutoUpdater.class.getClassLoader().getResourceAsStream("META-INF/maven/co.zmc/projectindigo/pom.xml");
            if (pomStream != null) {
                doc = parseXML(pomStream);
                descNodes = doc.getElementsByTagName("project");

                String clientVersion = (String) ((Element) descNodes.item(0)).getElementsByTagName("version").item(0).getChildNodes().item(0).getNodeValue();

                return (!version.equals(clientVersion));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Document parseXML(InputStream stream) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory objDocumentBuilderFactory = null;
        DocumentBuilder objDocumentBuilder = null;
        objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
        return (Document) objDocumentBuilder.parse(stream);
    }

    private static boolean downloadNew() {
        try {
            String jarLocation = AutoUpdater.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            URL url = new URL("http://zephyrunleashed.com/indigo/jar/ProjectIndigo.jar");
            if (Utils.getCurrentOS() == OS.WINDOWS && jarLocation.contains(".exe")) {
                url = new URL("http://zephyrunleashed.com/indigo/exe/ProjectIndigo.zip");
                jarLocation = jarLocation.substring(0, jarLocation.indexOf(".exe") + 4);
            } else if (Utils.getCurrentOS() == OS.MACOSX && jarLocation.contains(".app")) {
                url = new URL("http://zephyrunleashed.com/indigo/app/ProjectIndigo.zip");
                jarLocation = jarLocation.substring(0, jarLocation.indexOf(".app") + 4);
            }
            URLConnection connection = url.openConnection();
            File file = new File(jarLocation);
            FileUtils.deleteDirectory(file);

            if (Utils.getCurrentOS() == OS.WINDOWS && jarLocation.contains(".exe")) {
                file = new File(jarLocation.replaceAll(".exe", ".zip").replaceAll("%20", " "));
            } else if (Utils.getCurrentOS() == OS.MACOSX && jarLocation.contains(".app")) {
                file = new File(jarLocation.replaceAll(".app", ".zip").replaceAll("%20", " "));
            }

            logger.log(Level.INFO, "Update detected. Attempting to download.");
            JOptionPane.showMessageDialog(null, "An update to the launcher was found! Attempting to download");
            FileUtils.deleteDirectory(new File(DirectoryLocations.BASE_DIR_LOCATION));
            InputStream input = connection.getInputStream();
            saveStreamToFileAndUnZip(input, file);
            input.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void goToPage() throws IOException {
        JOptionPane.showMessageDialog(null, "An update to the launcher was found! You need to download it to use this launcher");
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("http://zephyrunleashed.com/indigolauncher"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public static void saveStreamToFileAndUnZip(InputStream input, File file) throws IOException {
        OutputStream output = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = input.read(buffer)) > 0) {
            output.write(buffer, 0, read);
        }
        output.close();
        if (file.getAbsolutePath().contains(".zip")) {
            File parentDir = file.getParentFile();
            ZipInputStream zipIn = null;
            try {
                input = new FileInputStream(file);
                zipIn = new ZipInputStream(input);
                ZipEntry currentEntry = zipIn.getNextEntry();
                while (currentEntry != null) {
                    if (currentEntry.getName().contains("META-INF") || currentEntry.getName().contains("__MACOSX") || currentEntry.getName().contains(".DS_Store")) {
                        currentEntry = zipIn.getNextEntry();
                        continue;
                    }
                    if (currentEntry.isDirectory()) {
                        File tmp = new File(parentDir, currentEntry.getName());
                        if (!tmp.exists()) {
                            tmp.mkdir();
                        }
                        currentEntry = zipIn.getNextEntry();
                        continue;
                    }
                    FileOutputStream outStream = new FileOutputStream(new File(parentDir, currentEntry.getName()));
                    int readLen;
                    buffer = new byte[1024];
                    while ((readLen = zipIn.read(buffer, 0, buffer.length)) > 0) {
                        outStream.write(buffer, 0, readLen);
                    }
                    outStream.close();
                    currentEntry = zipIn.getNextEntry();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
            } finally {
                try {
                    zipIn.close();
                    input.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            file.delete();
        }
    }
}

/*
 * This file is part of Project Indigo.
 *
 * Copyright (c) 2013 ZephyrUnleashed LLC <http://www.zephyrunleashed.com/>
 * Project Indigo is licensed under the ZephyrUnleashed License Version 1.
 *
 * Project Indigo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the ZephyrUnleashed License Version 1.
 *
 * Project Indigo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the ZephyrUnleashed License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License.
 */
/*
 * This file is part of Indigo Launcher.
 *
 * Copyright (c) 2013 ZephyrUnleashed LLC <http://www.zephyrunleashed.com/>
 * Indigo Launcher is licensed under the ZephyrUnleashed License Version 1.
 *
 * Indigo Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the ZephyrUnleashed License Version 1.
 *
 * Indigo Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the ZephyrUnleashed License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License.
 */
/*
 * This file is part of ProjectIndigo.
 *
 * Copyright (c) 2013 ZephyrUnleashed LLC <http://www.zephyrunleashed.com/>
 * ProjectIndigo is licensed under the ZephyrUnleashed License Version 1.
 *
 * ProjectIndigo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the ZephyrUnleashed License Version 1.
 *
 * ProjectIndigo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the ZephyrUnleashed License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License.
 */
package co.zmc.projectindigo.utils;

import java.awt.Desktop;
import java.io.File;
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

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AutoUpdater {
    private static Logger logger = Logger.getLogger("launcher");

    public static void main(String[] args) {
        if (shouldUpdate()) {
            downloadNew();
            logger.log(Level.INFO, "Download done.");
        }
        System.exit(0);
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

                String clientVersion = (String) ((Element) descNodes.item(0)).getElementsByTagName("version").item(0).getChildNodes().item(0)
                        .getNodeValue();

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

    private static void downloadNew() {
        try {
            URL url = new URL("http://zephyrunleashed.com/indigo/latest.jar");
            URLConnection connection = url.openConnection();
            String jarLocation = AutoUpdater.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            File jarFile = new File(jarLocation);

            if (jarFile.isDirectory()) {
                logger.log(Level.INFO, "Not being run from jar file. Not downloading update.");
            } else {
                if (jarLocation.contains(".app") || jarLocation.contains(".exe")) {
                    logger.log(Level.INFO, "Update detected. Redirecting");
                    JOptionPane.showMessageDialog(null, "An update to the launcher was found! You need to download it to use this launcher");
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(new URI("http://zephyrunleashed.com/indigolauncher"));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    System.exit(1);
                } else {
                    logger.log(Level.INFO, "Update detected. Attempting to download.");
                    JOptionPane.showMessageDialog(null, "An update to the launcher was found! Attempting to download");
                }
                InputStream input = connection.getInputStream();
                OutputStream output = new FileOutputStream(jarFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = input.read(buffer)) > 0) {
                    output.write(buffer, 0, read);
                }
                output.close();
                input.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AutoUpdater {

    public static void main(String[] args) {
    	if (shouldUpdate()) {
        	System.out.println("Update detected. Attempting to download.");
        	downloadNew();
        	System.out.println("Download done.");
        } else {
        	System.out.println("No update detected. Moving on.");
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
            doc = parseXML(pomStream);
            descNodes = doc.getElementsByTagName("project");

            String clientVersion = (String) ((Element) descNodes.item(0)).getElementsByTagName("version").item(0).getChildNodes().item(0).getNodeValue();
            
            return (!version.equals(clientVersion));
            
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
				System.out.println("Not being run from jar file. Not downloading update.");
			} else {
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

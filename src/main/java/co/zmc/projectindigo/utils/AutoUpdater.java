package co.zmc.projectindigo.utils;

import java.io.IOException;
import java.io.InputStream;
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
        shouldUpdate();
    }

    public static boolean shouldUpdate() {
        try {
            URL url = new URL("https://raw.github.com/ZephyrMC-Dev/ProjectIndigo/master/pom.xml");

            URLConnection connection = url.openConnection();

            Document doc = parseXML(connection.getInputStream());
            NodeList descNodes = doc.getElementsByTagName("project");

            String version = (String) ((Element) descNodes.item(0)).getElementsByTagName("version").item(0).getChildNodes().item(0).getNodeValue();
            System.out.println(version);
            return true;
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
}

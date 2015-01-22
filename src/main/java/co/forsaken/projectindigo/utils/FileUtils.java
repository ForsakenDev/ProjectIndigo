package co.forsaken.projectindigo.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import co.forsaken.projectindigo.data.log.Logger;
import co.forsaken.projectindigo.utils.mojangtokens.ExtractRule;

public class FileUtils {

  public static void writeStringToFile(String str, File file) {
    BufferedWriter writer = null;
    try {
      file.delete();
      file.createNewFile();
      writer = new BufferedWriter(new FileWriter(file));
      writer.write(str);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (writer != null) writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static String formatPath(String inputPath) {
    return new File(inputPath).getAbsolutePath();
  }

  public static void writeStreamToFile(InputStream stream, File file) {
    try {
      OutputStream out = new FileOutputStream(file);

      byte[] buffer = new byte[1024];
      int read;
      while ((read = stream.read(buffer)) > 0) {
        out.write(buffer, 0, read);
      }
      out.close();
      stream.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static boolean deleteDirectory(File directory) {
    if (directory.exists()) {
      File[] files = directory.listFiles();
      if (null != files) {
        for (int i = 0; i < files.length; i++) {
          if (files[i].isDirectory()) {
            deleteDirectory(files[i]);
          } else {
            files[i].delete();
          }
        }
      }
    }
    return directory.delete();
  }

  public static Document getXML(InputStream stream) throws SAXException, IOException {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    try {
      return docFactory.newDocumentBuilder().parse(stream);
    } catch (ParserConfigurationException ignored) {
      Logger.logError(ignored.getMessage(), ignored);
    } catch (UnknownHostException e) {
      Logger.logError(e.getMessage(), e);
    }
    return null;
  }

  public static void unzip(File in, File out) {
    unzip(in, out, null);
  }

  public static void unzip(File in, File out, ExtractRule extractRule) {
    try {
      ZipFile zipFile = null;
      if (!out.exists()) {
        out.mkdirs();
      }
      zipFile = new ZipFile(in);
      Enumeration<?> e = zipFile.entries();
      while (e.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) e.nextElement();
        String entryName = entry.getName();
        if (entry.getName().endsWith("aux.class")) {
          entryName = "aux_class";
        }
        if (extractRule != null && extractRule.shouldExclude(entryName)) {
          continue;
        }
        if (entry.isDirectory()) {
          File folder = new File(out, entryName);
          folder.mkdirs();
        }
        File destinationFilePath = new File(out, entryName);
        destinationFilePath.getParentFile().mkdirs();
        if (!entry.isDirectory() && !entry.getName().equals(".minecraft")) {
          BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
          int b;
          byte buffer[] = new byte[1024];
          FileOutputStream fos = new FileOutputStream(destinationFilePath);
          BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
          while ((b = bis.read(buffer, 0, 1024)) != -1) {
            bos.write(buffer, 0, b);
          }
          bos.flush();
          bos.close();
          bis.close();
        }
      }
      zipFile.close();
    } catch (IOException e) {}
  }

  public static boolean copyFile(File from, File to) {
    return copyFile(from, to, false);
  }

  public static boolean copyFile(File from, File to, boolean withFilename) {
    if (!from.isFile()) { return false; }
    if (!from.exists()) { return false; }
    if (!withFilename) {
      to = new File(to, from.getName());
    }
    if (to.exists()) {
      to.delete();
    }

    try {
      to.createNewFile();
    } catch (IOException e) {
      return false;
    }

    FileChannel source = null;
    FileChannel destination = null;

    try {
      source = new FileInputStream(from).getChannel();
      destination = new FileOutputStream(to).getChannel();
      destination.transferFrom(source, 0, source.size());
    } catch (IOException e) {
      return false;
    } finally {
      try {
        if (source != null) {
          source.close();
        }
        if (destination != null) {
          destination.close();
        }
      } catch (IOException e) {
        return false;
      }
    }
    return true;
  }

}

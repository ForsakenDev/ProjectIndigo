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
 * License and see <http://spout.in/licensev1> for the full license,
 * including the MIT license.
 */
package co.zmc.projectindigo.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.SwingWorker;

import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.utils.Utils;

public class DownloadHandler extends SwingWorker<Boolean, Void> {
    protected String     _status;
    protected Server     _server;
    protected URL[]      _jarURLs;
    private final Logger logger              = Logger.getLogger("launcher");
    protected double     totalDownloadSize   = 0;
    protected double     totalDownloadedSize = 0;

    public DownloadHandler(Server server) {
        _server = server;
        _status = "";
    }

    @Override
    protected Boolean doInBackground() {
        logger.log(Level.INFO, "Checking if MC exists");
        // Downloading jars
        if (!loadJarURLs()) { return false; }
        if (!_server.getBinDir().exists()) {
            _server.getBinDir().mkdirs();
        }
        logger.log(Level.INFO, "Downloading Jars");
        if (!downloadJars()) {
            logger.log(Level.SEVERE, "Download Failed");
            return false;
        }
        // extracting files
        logger.log(Level.INFO, "Extracting Files");
        if (!(extractModpack() && extractNatives())) {
            logger.log(Level.SEVERE, "Extraction Failed");
            return false;
        }
        logger.log(Level.INFO, "Download complete");
        return true;
    }

    protected boolean loadJarURLs() {
        logger.log(Level.INFO, "Loading Jar URLs");
        String[] jarList = { "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
        _jarURLs = new URL[jarList.length + 3];
        try {
            _jarURLs[0] = new URL(_server.getDownloadURL());
            _jarURLs[1] = new URL("http://assets.minecraft.net/" + _server.getMCVersion().replace(".", "_") + "/minecraft.jar");
            for (int i = 0; i < jarList.length; i++) {
                _jarURLs[i + 2] = new URL("http://s3.amazonaws.com/MinecraftDownload/" + jarList[i]);
            }
            switch (Utils.getCurrentOS()) {
                case WINDOWS:
                    _jarURLs[_jarURLs.length - 1] = new URL("http://s3.amazonaws.com/MinecraftDownload/windows_natives.jar");
                    break;
                case MACOSX:
                    _jarURLs[_jarURLs.length - 1] = new URL("http://s3.amazonaws.com/MinecraftDownload/macosx_natives.jar");
                    break;
                case UNIX:
                    _jarURLs[_jarURLs.length - 1] = new URL("http://s3.amazonaws.com/MinecraftDownload/linux_natives.jar");
                    break;
                default:
                    return false;
            }
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return false;
        }
        return true;
    }

    protected boolean downloadJars() {
        int[] fileSizes = new int[_jarURLs.length];
        for (int i = 0; i < _jarURLs.length; i++) {
            try {
                fileSizes[i] = _jarURLs[i].openConnection().getContentLength();
                totalDownloadSize += fileSizes[i];
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
                return false;
            }
        }
        for (int i = 0; i < _jarURLs.length; i++) {
            int attempt = 0;
            final int attempts = 5;
            int lastfile = -1;
            boolean downloadSuccess = false;
            while (!downloadSuccess && (attempt < attempts)) {
                try {
                    if (lastfile == i) {
                        logger.log(Level.INFO, "Connecting.. Try " + attempt + " of " + attempts + " for: " + _jarURLs[i].toURI());
                    }
                    lastfile = i;
                    attempt++;
                    downloadSuccess = downloadFile(_jarURLs[i], (i == 0 ? _server.getBaseDir() : _server.getBinDir()), fileSizes[i]);
                } catch (Exception e) {
                    downloadSuccess = false;
                    logger.log(Level.WARNING, "Connection failed, trying again");
                }
            }
            if (!downloadSuccess) { return false; }
        }
        return true;
    }

    protected boolean downloadFile(URL url, File baseDir, int fileSize) throws IOException {
        URLConnection dlConnection = url.openConnection();
        if (dlConnection instanceof HttpURLConnection) {
            dlConnection.setRequestProperty("Cache-Control", "no-cache");
            dlConnection.connect();
        }
        String jarFileName = getFilename(url);
        if (new File(baseDir, jarFileName).exists()) {
            new File(baseDir, jarFileName).delete();
        }
        InputStream dlStream = dlConnection.getInputStream();
        FileOutputStream outStream = new FileOutputStream(new File(baseDir, jarFileName));
        // Downloading " + jarFileName
        byte[] buffer = new byte[24000];
        int readLen;
        int currentDLSize = 0;
        while ((readLen = dlStream.read(buffer, 0, buffer.length)) != -1) {
            outStream.write(buffer, 0, readLen);
            currentDLSize += readLen;
            totalDownloadedSize += readLen;
            int prog = (int) ((totalDownloadedSize / totalDownloadSize) * 100);
            if (prog > 100) {
                prog = 100;
            } else if (prog < 0) {
                prog = 0;
            }
            setProgress(prog);
        }
        dlStream.close();
        outStream.close();
        if (dlConnection instanceof HttpURLConnection && (currentDLSize == fileSize || fileSize <= 0)) { return true; }
        return false;

    }

    protected boolean extractNatives() {
        // Extracting natives..
        File nativesJar = new File(_server.getBinDir(), getFilename(_jarURLs[_jarURLs.length - 1]));
        File nativesDir = new File(_server.getBinDir(), "natives");
        if (!nativesDir.isDirectory()) {
            nativesDir.mkdirs();
        }
        FileInputStream input = null;
        ZipInputStream zipIn = null;
        try {
            input = new FileInputStream(nativesJar);
            zipIn = new ZipInputStream(input);
            ZipEntry currentEntry = zipIn.getNextEntry();
            while (currentEntry != null) {
                if (currentEntry.getName().contains("META-INF")) {
                    currentEntry = zipIn.getNextEntry();
                    continue;
                }
                // Extracting " + currentEntry
                FileOutputStream outStream = new FileOutputStream(new File(nativesDir, currentEntry.getName()));
                int readLen;
                byte[] buffer = new byte[1024];
                while ((readLen = zipIn.read(buffer, 0, buffer.length)) > 0) {
                    outStream.write(buffer, 0, readLen);
                }
                outStream.close();
                currentEntry = zipIn.getNextEntry();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return false;
        } finally {
            try {
                zipIn.close();
                input.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        nativesJar.delete();
        return true;
    }

    protected boolean extractModpack() {
        // Extracting modpack..
        File modpackZip = new File(_server.getBaseDir(), getFilename(_jarURLs[0]));
        File modpackDir = _server.getBaseDir();
        if (!modpackDir.isDirectory()) {
            modpackDir.mkdirs();
        }
        FileInputStream input = null;
        ZipInputStream zipIn = null;
        try {
            input = new FileInputStream(modpackZip);
            zipIn = new ZipInputStream(input);
            ZipEntry currentEntry = zipIn.getNextEntry();
            while (currentEntry != null) {
                if (currentEntry.getName().contains("META-INF")) {
                    currentEntry = zipIn.getNextEntry();
                    continue;
                }
                if (currentEntry.isDirectory()) {
                    File tmp = new File(modpackDir, currentEntry.getName());
                    if (!tmp.exists()) {
                        tmp.mkdir();
                    }
                    currentEntry = zipIn.getNextEntry();
                    continue;
                }
                // Extracting " + currentEntry
                FileOutputStream outStream = new FileOutputStream(new File(modpackDir, currentEntry.getName()));
                int readLen;
                byte[] buffer = new byte[1024];
                while ((readLen = zipIn.read(buffer, 0, buffer.length)) > 0) {
                    outStream.write(buffer, 0, readLen);
                }
                outStream.close();
                currentEntry = zipIn.getNextEntry();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return false;
        } finally {
            try {
                zipIn.close();
                input.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        modpackZip.delete();
        return true;
    }

    protected String getFilename(URL url) {
        String string = url.getFile();
        if (string.contains("?")) {
            string = string.substring(0, string.indexOf('?'));
        }
        return string.substring(string.lastIndexOf('/') + 1);
    }
}
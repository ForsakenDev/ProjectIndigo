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
package co.zmc.projectindigo.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.SwingWorker;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.components.ServerSection;
import co.zmc.projectindigo.mclaunch.MinecraftLauncher;
import co.zmc.projectindigo.utils.InputStreamLogger;
import co.zmc.projectindigo.utils.Utils;

public class DownloadHandler extends SwingWorker<Boolean, Void> {
    protected String        _status;
    protected ServerSection _serverSection;
    protected LoginResponse _response;
    protected Server        _server;
    protected URL[]         _jarURLs;
    private final Logger    logger              = Logger.getLogger("launcher");
    protected double        totalDownloadSize   = 0;
    protected double        totalDownloadedSize = 0;

    public DownloadHandler(Server server, ServerSection section, LoginResponse response) {
        _server = server;
        _serverSection = section;
        _response = response;
        _status = "";
    }

    @Override
    protected Boolean doInBackground() {
        _serverSection.setFormsEnabled(false);
        if (_server.shouldDownload()) { return load(); }

        return true;
    }

    protected void done() {
        launch();
    }

    public boolean load() {
        _server.getBinDir().mkdirs();

        _serverSection.stateChanged("Installing " + _server.getName() + "...", 0);
        if (!loadJarURLs()) { return false; }
        logger.log(Level.INFO, "Downloading Jars");
        if (!downloadJars()) {
            logger.log(Level.SEVERE, "Download Failed");
            return false;
        }
        _serverSection.stateChanged("Extracting files...", 0);
        logger.log(Level.INFO, "Extracting Files");
        if (!(extractFiles() && removeMetaInf())) {
            logger.log(Level.SEVERE, "Extraction Failed");
            return false;
        }

        return true;
    }

    public void launch() {
        logger.log(Level.INFO, "Download complete");
        _serverSection.stateChanged("Download complete", 100);
        _serverSection.setFormsEnabled(true);
        if (_server != null) {
            _serverSection.addServer(_server);
            Process pro;
            try {
                pro = MinecraftLauncher.launchMinecraft(_server, _response.getUsername(), _response.getSessionId(), "MinecraftForge.zip", "1024",
                        "128M");

                InputStreamLogger.start(pro.getInputStream());
                try {
                    Thread.sleep(3500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                IndigoLauncher._launcher.setVisible(false);
                IndigoLauncher._launcher.dispose();
                System.exit(0);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
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
            _serverSection.stateChanged("Downloading " + jarFileName + "...", prog);
        }
        dlStream.close();
        outStream.close();
        return (dlConnection instanceof HttpURLConnection && (currentDLSize == fileSize || fileSize <= 0));

    }

    protected boolean removeMetaInf() {
        double totalExtractSize = 0;
        double totalExtractedSize = 0;
        _serverSection.stateChanged("Removing META-INF...", 0);
        File outputTmpFile = new File(_server.getBinDir(), "minecraft.jar.tmp");
        File inputFile = new File(_server.getBinDir(), "minecraft.jar");
        try {
            FileInputStream input = new FileInputStream(inputFile);
            totalExtractSize += input.available();
            input.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return false;
        }

        try {
            JarInputStream input = new JarInputStream(new FileInputStream(inputFile));
            JarOutputStream output = new JarOutputStream(new FileOutputStream(outputTmpFile));
            JarEntry entry;

            while ((entry = input.getNextJarEntry()) != null) {
                if (entry.getName().contains("META-INF") || entry.getName().contains("__MACOSX")) {
                    continue;
                }
                output.putNextEntry(entry);
                byte buffer[] = new byte[1024];
                int readLen;
                while ((readLen = input.read(buffer, 0, 1024)) != -1) {
                    output.write(buffer, 0, readLen);
                    totalExtractedSize += readLen;
                    int prog = (int) ((totalExtractedSize / totalExtractSize) * 100);
                    if (prog > 100) {
                        prog = 100;
                    } else if (prog < 0) {
                        prog = 0;
                    }
                    _serverSection.stateChanged("Removing META-INF...", prog);
                }
                output.closeEntry();
            }

            input.close();
            output.close();

            if (!inputFile.delete()) {
                logger.log(Level.SEVERE, "Failed to delete Minecraft.jar.");
                return false;
            }
            outputTmpFile.renameTo(inputFile);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return true;
    }

    protected boolean extractFiles() {
        double totalExtractSize = 0;
        double totalExtractedSize = 0;
        File[][] files = new File[][] { { new File(_server.getBaseDir(), getFilename(_jarURLs[0])), _server.getBaseDir() },
                { new File(_server.getBinDir(), getFilename(_jarURLs[_jarURLs.length - 1])), new File(_server.getBinDir(), "natives") } };
        int[] fileSizes = new int[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                FileInputStream input = new FileInputStream(files[i][0]);
                fileSizes[i] = input.available();
                totalExtractSize += fileSizes[i];
                input.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
                return false;
            }
        }
        for (int i = 0; i < files.length; i++) {
            File[] file = files[i];
            if (!file[1].isDirectory()) {
                file[1].mkdirs();
            }
            FileInputStream input = null;
            ZipInputStream zipIn = null;
            try {
                input = new FileInputStream(file[0]);
                zipIn = new ZipInputStream(input);
                ZipEntry currentEntry = zipIn.getNextEntry();
                while (currentEntry != null) {
                    if (currentEntry.getName().contains("META-INF") || currentEntry.getName().contains("__MACOSX")) {
                        currentEntry = zipIn.getNextEntry();
                        continue;
                    }
                    if (currentEntry.isDirectory()) {
                        File tmp = new File(file[1], currentEntry.getName());
                        if (!tmp.exists()) {
                            tmp.mkdir();
                        }
                        currentEntry = zipIn.getNextEntry();
                        continue;
                    }
                    FileOutputStream outStream = new FileOutputStream(new File(file[1], currentEntry.getName()));
                    int readLen;
                    int currentDLSize = 0;
                    byte[] buffer = new byte[1024];
                    while ((readLen = zipIn.read(buffer, 0, buffer.length)) > 0) {
                        outStream.write(buffer, 0, readLen);
                        currentDLSize += readLen;
                        totalExtractedSize += readLen;
                        int prog = (int) ((totalExtractedSize / totalExtractSize) * 100);
                        if (prog > 100) {
                            prog = 100;
                        } else if (prog < 0) {
                            prog = 0;
                        }
                        _serverSection.stateChanged("Extracting " + file[0].getName() + "...", prog);
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
            file[0].delete();
        }
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
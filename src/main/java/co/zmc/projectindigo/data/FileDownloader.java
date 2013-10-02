package co.zmc.projectindigo.data;

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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import co.zmc.projectindigo.data.log.Logger;
import co.zmc.projectindigo.gui.ProgressPanel;
import co.zmc.projectindigo.utils.FileUtils;
import co.zmc.projectindigo.utils.Utils;

public class FileDownloader {

    protected Thread  _downloadThread;
    protected String  _rawDownloadURL;
    protected String  _baseDir  = "";
    protected boolean _extract  = false;
    protected File    _downloadedFile;
    protected int     _fileSize = -1;
    protected URL     _downloadURL;

    public FileDownloader(String downloadURL, String dir, boolean extract) {
        this(downloadURL, dir);
        _extract = extract;
    }

    public FileDownloader(String downloadURL, String dir) {
        _rawDownloadURL = downloadURL.replaceAll(" ", "%20");
        _baseDir = dir;
    }

    public String getRawDownloadURL() {
        return _rawDownloadURL;
    }

    public boolean shouldExtract() {
        return _extract;
    }

    public boolean shouldDownload() {
        return _rawDownloadURL != null && !_rawDownloadURL.isEmpty();
    }

    public Runnable loadFileSize(final Server server, final ProgressPanel panel) {
        return new Runnable() {
            public void run() {
                if (_rawDownloadURL != null && !_rawDownloadURL.isEmpty()) {
                    try {
                        _fileSize = getDownloadURL().openConnection().getContentLength();
                        server.addValidatedFile(panel, _fileSize, _extract);
                    } catch (IOException e) {
                        Logger.logError(e.getMessage(), e);
                    }
                }
            }
        };
    }

    public int getFileSize() {
        return _fileSize;
    }

    protected String getFilename() throws MalformedURLException {
        if (_rawDownloadURL != null && !_rawDownloadURL.isEmpty()) {
            if (_rawDownloadURL.contains("minecraftforge")) { return "MinecraftForge.zip"; }
            String string = getDownloadURL().getFile();
            if (string.contains("?")) {
                string = string.substring(0, string.indexOf('?'));
            }
            return string.substring(string.lastIndexOf('/') + 1);
        }
        return "";
    }

    protected final URL getDownloadURL() {
        if (_rawDownloadURL != null && !_rawDownloadURL.isEmpty()) {
            if (_downloadURL == null) {
                try {
                    _downloadURL = new URL(Utils.getRedirectedUrl(_rawDownloadURL));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return _downloadURL;
        }
        return null;
    }

    public Runnable download(final Server server, final ProgressPanel panel) throws IOException {
        return new Runnable() {
            public void run() {
                try {
                    if (_rawDownloadURL != null && !_rawDownloadURL.isEmpty()) {
                        downloadFile(server, panel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    protected String getFileExtension() {
        try {
            String[] filename = getFilename().split(".");
            return filename[filename.length - 1];
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "jar";
    }

    private boolean downloadFile(Server server, ProgressPanel panel) throws IOException {
        String jarFileName = getFilename();
        File downloadedFile = new File(_baseDir, jarFileName);
        Logger.logInfo("Downloading " + jarFileName + " to \"" + _baseDir + "\"");
        if (!new File(_baseDir).exists()) {
            new File(_baseDir).mkdir();
        }
        URLConnection dlConnection = getDownloadURL().openConnection();
        if (dlConnection instanceof HttpURLConnection) {
            dlConnection.setRequestProperty("Cache-Control", "no-cache");
            dlConnection.connect();
        }
        if (downloadedFile.exists()) {
            FileUtils.deleteDirectory(downloadedFile);
        }
        InputStream dlStream = dlConnection.getInputStream();
        FileOutputStream outStream = new FileOutputStream(downloadedFile);
        byte[] buffer = new byte[24000];
        int readLen;
        int currentDLSize = 0;
        while ((readLen = dlStream.read(buffer, 0, buffer.length)) != -1) {
            outStream.write(buffer, 0, readLen);
            currentDLSize += readLen;
            server.addDownloadSize(panel, readLen);
        }
        dlStream.close();
        outStream.close();

        if (dlConnection instanceof HttpURLConnection && (currentDLSize == getFileSize() || getFileSize() <= 0)) {
            Logger.logInfo("Finished downloading " + jarFileName);
            _downloadedFile = downloadedFile;
            if (shouldExtract()) {
                extract(server, panel);
            }
            server.addLoadedDownload();
            return true;
        }
        Logger.logInfo("Could not finish downloading " + jarFileName);
        return false;
    }

    public void extract(Server server, ProgressPanel panel) {
        if (_downloadedFile == null) { return; }
        Logger.logInfo("Extracting " + _downloadedFile.getName() + " to \"" + _baseDir + "\"");

        if (_downloadedFile.getName().contains("minecraft.jar")) {
            extractJar(server, panel, true);
        } else {
            extractZip(server, panel, true);
        }
        Logger.logInfo("Extracting " + _downloadedFile.getName() + " to \"" + _baseDir + "\"");

    }

    protected void extractZip(Server server, ProgressPanel panel, boolean overwrite) {
        FileInputStream input = null;
        ZipInputStream zipIn = null;
        try {
            input = new FileInputStream(_downloadedFile);
            zipIn = new ZipInputStream(input);
            ZipEntry currentEntry = zipIn.getNextEntry();
            while (currentEntry != null) {
                if (currentEntry.getName().contains("META-INF") || currentEntry.getName().contains("__MACOSX") || currentEntry.getName().contains(".DS_Store")) {
                    currentEntry = zipIn.getNextEntry();
                    continue;
                }
                if (currentEntry.isDirectory()) {
                    File tmp = new File(_baseDir, currentEntry.getName());
                    if (!tmp.exists()) {
                        tmp.mkdir();
                    }
                    currentEntry = zipIn.getNextEntry();
                    continue;
                }
                File newFile = new File(_baseDir, currentEntry.getName());
                if (newFile.exists()) {
                    FileUtils.deleteDirectory(newFile);
                }
                FileOutputStream outStream = new FileOutputStream(newFile);
                int readLen;
                byte[] buffer = new byte[1024];
                while ((readLen = zipIn.read(buffer, 0, buffer.length)) > 0) {
                    outStream.write(buffer, 0, readLen);
                    server.addDownloadSize(panel, readLen);
                }
                outStream.close();
                currentEntry = zipIn.getNextEntry();
            }
        } catch (IOException e) {
            Logger.logError(e.getMessage(), e);
            return;
        } finally {
            try {
                zipIn.close();
                input.close();
            } catch (IOException e) {
                Logger.logError(e.getMessage(), e);
            }
        }
        _downloadedFile.delete();
    }

    protected boolean extractJar(Server server, ProgressPanel panel, boolean overwrite) {
        File tmpFile = new File(_downloadedFile.getAbsolutePath() + ".tmp");
        try {
            JarInputStream input = new JarInputStream(new FileInputStream(_downloadedFile));
            JarOutputStream output = new JarOutputStream(new FileOutputStream(tmpFile));
            JarEntry entry;

            while ((entry = input.getNextJarEntry()) != null) {
                if (entry.getName().contains("META-INF") || entry.getName().contains("__MACOSX") || entry.getName().contains(".DS_Store")) {
                    continue;
                }
                output.putNextEntry(entry);
                byte buffer[] = new byte[1024];
                int readLen;
                while ((readLen = input.read(buffer, 0, 1024)) != -1) {
                    output.write(buffer, 0, readLen);
                    server.addDownloadSize(panel, readLen);
                }
                output.closeEntry();
            }

            input.close();
            output.close();

            if (!_downloadedFile.delete()) {
                Logger.logError("Failed to delete " + _downloadedFile.getName());
                return false;
            }
            tmpFile.renameTo(_downloadedFile);
        } catch (FileNotFoundException e) {
            Logger.logError(e.getMessage(), e);
            return false;
        } catch (IOException e) {
            Logger.logError(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public boolean delete() throws MalformedURLException {
        return FileUtils.deleteDirectory(new File(_baseDir, getFilename()));
    }
}

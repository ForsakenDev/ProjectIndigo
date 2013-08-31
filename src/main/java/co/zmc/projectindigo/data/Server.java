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
package co.zmc.projectindigo.data;

import java.io.File;

import org.json.simple.JSONObject;

import co.zmc.projectindigo.managers.DownloadHandler;
import co.zmc.projectindigo.utils.DirectoryLocations;

public class Server {
    private String _name;
    private String _ip;
    private int    _port;
    private String _logo;
    private String _downloadURL;
    private String _version;
    private String _mcVersion;

    private File   _baseDir;
    private File   _binDir;

    public Server(JSONObject server, int port) {
        _name = (String) server.get("name");
        _ip = (String) server.get("ip");
        _port = port;
        _logo = (String) server.get("logo");
        _downloadURL = (String) server.get("download_url");
        _version = (String) server.get("version");
        _mcVersion = (String) server.get("mc_version");

        _baseDir = new File(String.format(DirectoryLocations.SERVER_DIR_LOCATION, getIp()));
        _binDir = new File(String.format(DirectoryLocations.SERVER_MINECRAFT_BIN_DIR_LOCATION, getIp()));
        if (!isDownloaded()) {
            mkdir();
            download();
        }
    }

    public void mkdir() {
        if (!_baseDir.exists()) {
            _baseDir.mkdir();
        }
    }

    public boolean isDownloaded() {
        return _binDir.exists();
    }

    public File getBaseDir() {
        return _baseDir;
    }

    public File getBinDir() {
        return _binDir;
    }

    public void download() {
        DownloadHandler handler = new DownloadHandler(this);
        handler.execute();
    }

    public String getName() {
        return _name;
    }

    public String getIp() {
        return _ip;
    }

    public int getPort() {
        return _port;
    }

    public String getLogo() {
        return _logo;
    }

    public String getDownloadURL() {
        return _downloadURL;
    }

    public String getVersion() {
        return _version;
    }

    public String getMCVersion() {
        return _mcVersion;
    }

    @Override
    public String toString() {
        String data = "{";
        data += "\n    \"name\": \"" + getName() + "\",";
        data += "\n    \"ip\": \"" + getIp() + "\",";
        data += "\n    \"port\": \"" + getPort() + "\",";
        data += "\n    \"logo\": \"" + getLogo() + "\",";
        data += "\n    \"download_url\": \"" + getDownloadURL() + "\",";
        data += "\n    \"version\": \"" + getVersion() + "\",";
        data += "\n    \"mc_version\": \"" + getMCVersion() + "\",";
        data += "\n  }";
        return data;
    }

    public int getPlayersOnline() {
        return 83;
    }

    public int getTotalOnline() {
        return 1000;
    }
}

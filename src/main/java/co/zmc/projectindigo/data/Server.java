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
package co.zmc.projectindigo.data;

import org.json.simple.JSONObject;

public class Server {

    private final JSONObject _server;
    private String           _name;
    private String           _ip;
    private int              _port;
    private String           _logo;
    private String           _version;
    private String           _mcVersion;
    private ModPack          _modPack;

    public Server(JSONObject server) {
        _server = server;
        _name = (String) server.get("name");
        _ip = (String) server.get("ip");
        _port = Integer.parseInt(server.get("port").toString());
        _logo = (String) server.get("logo");
        _version = (String) server.get("version");
        _mcVersion = (String) server.get("mc_version");
        _modPack = new ModPack((String) server.get("pack_host"), (String) server.get("pack_name"), (String) server.get("pack_version"));
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

    public String getVersion() {
        return _version;
    }

    public String getMcVersion() {
        return _mcVersion;
    }

    public ModPack getModPack() {
        return _modPack;
    }

    @Override
    public String toString() {
        return _server.toJSONString();
    }
}

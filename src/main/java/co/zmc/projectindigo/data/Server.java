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

    private String  _name;
    private String  _ip;
    private int     _port;
    private String  _logo;
    private ModPack _modPack;

    public Server(JSONObject server, int port) {
        _name = (String) server.get("name");
        _ip = (String) server.get("ip");
        _port = port;
        _logo = (String) server.get("logo");
        _modPack = new ModPack((String) server.get("modpack_host"), (String) server.get("modpack_name"), (String) server.get("modpack_loc"),
                (String) server.get("modpack_version"));
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

    public ModPack getModPack() {
        return _modPack;
    }

    @Override
    public String toString() {
        String data = "{";
        data += "\n    \"name\": \"" + getName() + "\",";
        data += "\n    \"ip\": \"" + getIp() + "\",";
        data += "\n    \"port\": \"" + getPort() + "\",";
        data += "\n    \"logo\": \"" + getLogo() + "\",";
        data += "\n    \"modpack_host\": \"" + getModPack().getPackHost() + "\",";
        data += "\n    \"modpack_name\": \"" + getModPack().getPackName() + "\",";
        data += "\n    \"modpack_loc\": \"" + getModPack().getPackURL() + "\",";
        data += "\n    \"modpack_version\": \"" + getModPack().getPackVersion() + "\"";
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

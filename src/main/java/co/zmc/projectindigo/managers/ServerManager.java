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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.components.ServerSection;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.FileUtils;

public class ServerManager extends SwingWorker<Boolean, Void> {
    private static final File _saveFile         = new File(DirectoryLocations.DATA_DIR_LOCATION, "servers");
    private String            _status;
    private int               _percentComplete;
    private List<Server>      _servers          = new ArrayList<Server>();
    private ServerSection     _serverSection;
    private int               numToLoad         = 0;
    private int               currentParseIndex = 0;
    private JSONObject        servers           = null;
    private final Logger      logger            = Logger.getLogger("launcher");

    public ServerManager(ServerSection serverSection) {
        _serverSection = serverSection;
    }

    @Override
    protected Boolean doInBackground() {
        loadServers();
        return true;
    }

    public String getStatus() {
        return _status;
    }

    public int getPercentComplete() {
        return _percentComplete;
    }

    public void loadServers() {
        try {
            if (!_saveFile.exists()) {
                _saveFile.createNewFile();
            }
            servers = (JSONObject) new JSONParser().parse(new Scanner(_saveFile, "UTF-8").useDelimiter("\\A").next());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        numToLoad = servers.size();
        while (currentParseIndex < numToLoad) {
            Server s = parseNext();
            if (s != null) {
                addServer(s);
            }
            currentParseIndex++;
        }
    }

    public void addServer(Server server) {
        int index = -1;
        for (int i = 0; i < _servers.size(); i++) {
            Server s = _servers.get(i);
            if (s.getFullIp().equalsIgnoreCase(server.getFullIp())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            _servers.remove(index);
        }
        _servers.add(server);
        _serverSection.addServer(server);
    }

    public Server parseServer(JSONObject sData) throws NumberFormatException, ParseException {
        return parseServer(sData.toJSONString(), Integer.parseInt((String) sData.get("port")));
    }

    public Server parseServer(String json, int port) throws ParseException {
        JSONObject sData = (JSONObject) new JSONParser().parse(json);
        return new Server(_serverSection, sData, port);
    }

    private Server parseNext() {
        if (currentParseIndex < numToLoad) {
            try {
                return parseServer((JSONObject) servers.get(servers.keySet().toArray()[currentParseIndex]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Server getServer(String fullIp) {
        for (Server s : _servers) {
            if (s.getFullIp().equalsIgnoreCase(fullIp)) { return s; }
        }
        return null;
    }

    public List<Server> getServers() {
        return _servers;
    }

    public void saveServers() {
        String str = "{\n";
        for (int i = 0; i < _servers.size(); i++) {
            Server s = _servers.get(i);
            if (i > 0) {
                str += ", \n";
            }
            str += "  \"" + (i + 1) + "\": " + s.toString();
        }
        str += "\n}";
        FileUtils.writeStringToFile(str, _saveFile);
    }

    public Server shouldUpdate(Server server) {
        Socket s = new Socket();
        String serverURL = "";
        try {
            String channel = "projectindigo";
            String msg = "request_modpack_url";
            logger.log(Level.INFO, "Connecting to server " + server.getFullIp());
            s.connect(new InetSocketAddress(server.getIp(), server.getPort()));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            DataInputStream in = new DataInputStream(s.getInputStream());

            logger.log(Level.INFO, "Requesting server information");
            out.writeByte(0xFA);
            out.writeShort(channel.length());
            out.writeChars(channel);
            out.writeShort(msg.length());
            out.write(msg.getBytes());

            if (Integer.valueOf(in.read()) == 0xFA) {
                String readStr = "";
                int len = in.readShort();
                for (int i = 0; i < len; i++) {
                    readStr += in.readChar();
                }
                if (readStr.equalsIgnoreCase(channel)) {
                    readStr = "";
                    len = in.readShort();
                    for (int i = 0; i < len; i++) {
                        readStr += (char) in.read();
                    }
                    serverURL = readStr;
                }
            }
            in.close();
            out.flush();
            out.close();
            s.close();
        } catch (ConnectException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to the server... Are you sure the address is right?", "Connection failed",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!serverURL.isEmpty()) {
            logger.log(Level.INFO, "Reading server information");
            try {
                Server newServer = parseServer(serverURL, server.getPort());
                if (!server.getVersion().equals(newServer.getVersion())) {
                    return newServer;
                } else {
                    return null;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public void loadServer(String ip, int port) {
        Socket s = new Socket();
        String serverURL = "";
        try {
            String channel = "projectindigo";
            String msg = "request_modpack_url";
            logger.log(Level.INFO, "Connecting to server " + ip + ":" + port);
            s.connect(new InetSocketAddress(ip, port));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            DataInputStream in = new DataInputStream(s.getInputStream());

            logger.log(Level.INFO, "Requesting server information");
            out.writeByte(0xFA);
            out.writeShort(channel.length());
            out.writeChars(channel);
            out.writeShort(msg.length());
            out.write(msg.getBytes());

            if (Integer.valueOf(in.read()) == 0xFA) {
                String readStr = "";
                int len = in.readShort();
                for (int i = 0; i < len; i++) {
                    readStr += in.readChar();
                }
                if (readStr.equalsIgnoreCase(channel)) {
                    readStr = "";
                    len = in.readShort();
                    for (int i = 0; i < len; i++) {
                        readStr += (char) in.read();
                    }
                    serverURL = readStr;
                }
            }
            in.close();
            out.flush();
            out.close();
            s.close();
        } catch (ConnectException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to the server... Are you sure the address is right?", "Connection failed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!serverURL.isEmpty()) {
            logger.log(Level.INFO, "Reading server information");
            try {
                Server server = parseServer(serverURL, port);
                if (server != null) {
                    addServer(server);
                }
                saveServers();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}

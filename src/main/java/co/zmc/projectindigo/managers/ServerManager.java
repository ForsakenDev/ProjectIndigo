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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.SwingWorker;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.FileUtils;

public class ServerManager extends SwingWorker<Boolean, Void> {
    private static final File _saveFile = new File(DirectoryLocations.DATA_DIR_LOCATION, "servers");
    private String            _status;
    private int               _percentComplete;
    private List<Server>      _servers  = new ArrayList<Server>();

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
        JSONObject servers = null;
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
        if (servers != null) {
            for (Object i : servers.keySet()) {
                Server server = new Server((JSONObject) servers.get(i));
                _servers.add(server);
            }
        }
    }

    public void parseServer(String json) throws ParseException {
        JSONObject sData = (JSONObject) new JSONParser().parse(json);
        Server server = new Server(sData);
        _servers.add(server);
        saveServers();
    }

    public void saveServers() {
        String str = "{\n";
        for (int i = 0; i < _servers.size(); i++) {
            Server s = _servers.get(i);
            if (i > 0) {
                str += ", \n";
            }
            str += "\"" + (i + 1) + "\": " + s.toString();
        }
        str += "\n}";
        FileUtils.writeStringToFile(str, _saveFile);
    }

    public void loadServer(String ip, int port) {
        Socket s = new Socket();
        String serverURL = "";
        try {
            String channel = "projectindigo";
            String msg = "request_modpack_url";
            s.connect(new InetSocketAddress(ip, port));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            DataInputStream in = new DataInputStream(s.getInputStream());

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!serverURL.isEmpty()) {
            BufferedReader reader = null;
            try {
                URL url = new URL(serverURL);
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuffer buffer = new StringBuffer();
                int read;
                char[] chars = new char[1024];
                while ((read = reader.read(chars)) != -1)
                    buffer.append(chars, 0, read);
                parseServer(buffer.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}

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

public class LoginResponse {

    private boolean _validResponse = false;
    private String  _version;
    private String  _downloadTicket;
    private String  _username;
    private String  _sessionId;

    public LoginResponse(String response) {
        String[] responseValues = response.split(":");
        if (responseValues.length < 4) {
            throw new NullPointerException("Invalid login response from Minecraft");
        } else {
            _validResponse = true;
            _version = responseValues[0];
            _downloadTicket = responseValues[1];
            _username = responseValues[2];
            _sessionId = responseValues[3];
        }
    }

    public boolean isValidResponse() {
        return _validResponse;
    }

    public String getVersion() {
        return _version;
    }

    public String getDownloadTicket() {
        return _downloadTicket;
    }

    public String getUsername() {
        return _username;
    }

    public String getSessionId() {
        return _sessionId;
    }

}

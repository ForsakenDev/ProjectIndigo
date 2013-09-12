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
package co.zmc.projectindigo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputStreamLogger extends Thread {
    private final InputStream is;

    private InputStreamLogger(InputStream from) {
        is = from;
    }

    @Override
    public void run() {
        byte buffer[] = new byte[4096];
        String logBuffer = "";
        int newLineIndex;
        int nullIndex;
        try {
            while (is.read(buffer) > 0) {
                logBuffer += new String(buffer).replace("\r\n", "\n");
                nullIndex = logBuffer.indexOf(0);
                if (nullIndex != -1) {
                    logBuffer = logBuffer.substring(0, nullIndex);
                }
                while ((newLineIndex = logBuffer.indexOf("\n")) != -1) {
                    logBuffer = logBuffer.substring(newLineIndex + 1);
                }
                Arrays.fill(buffer, (byte) 0);
            }
        } catch (IOException e) {
            Logger.getLogger("launcher").log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void start(InputStream from) {
        InputStreamLogger processStreamRedirect = new InputStreamLogger(from);
        processStreamRedirect.start();
    }
}
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
package co.zmc.projectindigo.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import co.zmc.projectindigo.IndigoLauncher;

public class ResourceUtils {

    private static final String BASE_PATH           = "/co/zmc/projectindigo/resources";
    private static final URL    BASE_CHAR           = ResourceUtils.class.getResource(BASE_PATH + "/images/char.png");
    private static final URL    BG                  = ResourceUtils.class.getResource(BASE_PATH + "/images/bg.jpg");
    private static final URL    MAIN_BG             = ResourceUtils.class.getResource(BASE_PATH + "/images/main_bg.jpg");
    private static final URL    ICON_ACCOUNT        = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/account.png");
    private static final URL    ICON_ACCOUNT_HOVER  = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/account_hover.png");
    private static final URL    ICON_HOME           = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/home.png");
    private static final URL    ICON_HOME_HOVER     = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/home_hover.png");
    private static final URL    ICON_SERVERS        = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/list.png");
    private static final URL    ICON_SERVERS_HOVER  = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/list_hover.png");
    private static final URL    ICON_SETTINGS       = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/settings.png");
    private static final URL    ICON_SETTINGS_HOVER = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/settings_hover.png");
    private static final URL    SIDE_BAR_BG         = ResourceUtils.class.getResource(BASE_PATH + "/images/side_bar_bg.png");
    private static final URL    SELECTOR            = ResourceUtils.class.getResource(BASE_PATH + "/images/selector.png");

    public static URL getResource(String name) {
        if (name.equalsIgnoreCase("base_char")) {
            return BASE_CHAR;
        } else if (name.equalsIgnoreCase("bg")) {
            return BG;
        } else if (name.equalsIgnoreCase("main_bg")) {
            return MAIN_BG;
        } else if (name.equalsIgnoreCase("account")) {
            return ICON_ACCOUNT;
        } else if (name.equalsIgnoreCase("account_hover")) {
            return ICON_ACCOUNT_HOVER;
        } else if (name.equalsIgnoreCase("home")) {
            return ICON_HOME;
        } else if (name.equalsIgnoreCase("home_hover")) {
            return ICON_HOME_HOVER;
        } else if (name.equalsIgnoreCase("servers")) {
            return ICON_SERVERS;
        } else if (name.equalsIgnoreCase("servers_hover")) {
            return ICON_SERVERS_HOVER;
        } else if (name.equalsIgnoreCase("settings")) {
            return ICON_SETTINGS;
        } else if (name.equalsIgnoreCase("settings_hover")) {
            return ICON_SETTINGS_HOVER;
        } else if (name.equalsIgnoreCase("side_bar_bg")) {
            return SIDE_BAR_BG;
        } else if (name.equalsIgnoreCase("selector")) { return SELECTOR; }
        return null;
    }

    public static InputStream getResourceAsStream(String name) {
        if (name.equalsIgnoreCase("minecraft_font")) { return getResourceAsStream(BASE_PATH + "/fonts/minecraft.ttf", ""); }
        return null;
    }

    private static InputStream getResourceAsStream(String path, String t) {
        InputStream stream = IndigoLauncher.class.getResourceAsStream(path);
        String[] split = path.split("/");
        path = split[(split.length - 1)];
        if (stream == null) {
            File resource = new File(".\\src\\main\\resources\\" + path);
            if (resource.exists()) try {
                stream = new BufferedInputStream(new FileInputStream(resource));
            } catch (IOException ignore) {
            }
        }
        return stream;
    }
}

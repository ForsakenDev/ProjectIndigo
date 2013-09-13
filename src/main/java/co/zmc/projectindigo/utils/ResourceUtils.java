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
package co.zmc.projectindigo.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import co.zmc.projectindigo.IndigoLauncher;

public class ResourceUtils {

    private static final String BASE_PATH           = "/co/zmc/projectindigo/resources";
    private static final URL    SPLASH_SCREEN       = ResourceUtils.class.getResource(BASE_PATH + "/images/splash_screen.png");
    private static final URL    BASE_CHAR           = ResourceUtils.class.getResource(BASE_PATH + "/images/char.png");
    private static final URL    BG                  = ResourceUtils.class.getResource(BASE_PATH + "/images/bg.jpg");
    private static final URL    MAIN_BG             = ResourceUtils.class.getResource(BASE_PATH + "/images/main_bg.jpg");
    private static final URL    ICON_ACCOUNT        = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/account.png");
    private static final URL    ICON_ACCOUNT_HOVER  = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/account_hover.png");
    private static final URL    ICON_HOME           = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/home.png");
    private static final URL    ICON_HOME_HOVER     = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/home_hover.png");
    private static final URL    ICON_CLOUD          = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/cloud.png");
    private static final URL    ICON_CLOUD_HOVER    = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/cloud_hover.png");
    private static final URL    ICON_SERVERS        = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/list.png");
    private static final URL    ICON_SERVERS_HOVER  = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/list_hover.png");
    private static final URL    ICON_SETTINGS       = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/settings.png");
    private static final URL    ICON_SETTINGS_HOVER = ResourceUtils.class.getResource(BASE_PATH + "/images/icons/settings_hover.png");
    private static final URL    SIDE_BAR_BG         = ResourceUtils.class.getResource(BASE_PATH + "/images/side_bar_bg.png");
    private static final URL    SELECTOR            = ResourceUtils.class.getResource(BASE_PATH + "/images/selector.png");
    private static final URL    SERVER_DEFAULT      = ResourceUtils.class.getResource(BASE_PATH + "/images/servers/default.png");
    private static final URL    SERVER_OVERLAY      = ResourceUtils.class.getResource(BASE_PATH + "/images/servers/overlay.png");
    private static final URL    SERVER_INFO         = ResourceUtils.class.getResource(BASE_PATH + "/images/servers/info.png");
    private static final URL    SERVER_INFO_HOVER   = ResourceUtils.class.getResource(BASE_PATH + "/images/servers/info_hover.png");
    private static final URL    SERVER_EDIT         = ResourceUtils.class.getResource(BASE_PATH + "/images/servers/edit.png");
    private static final URL    SERVER_EDIT_HOVER   = ResourceUtils.class.getResource(BASE_PATH + "/images/servers/edit_hover.png");
    private static final URL    ADD_SERVER          = ResourceUtils.class.getResource(BASE_PATH + "/images/add.png");

    public static URL getResource(String name) {
        if (name.equalsIgnoreCase("splash_screen")) {
            return SPLASH_SCREEN;
        } else if (name.equalsIgnoreCase("base_char")) {
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
        } else if (name.equalsIgnoreCase("cloud")) {
            return ICON_CLOUD;
        } else if (name.equalsIgnoreCase("cloud_hover")) {
            return ICON_CLOUD_HOVER;
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
        } else if (name.equalsIgnoreCase("selector")) {
            return SELECTOR;
        } else if (name.equalsIgnoreCase("server_default")) {
            return SERVER_DEFAULT;
        } else if (name.equalsIgnoreCase("overlay")) {
            return SERVER_OVERLAY;
        } else if (name.equalsIgnoreCase("server_info")) {
            return SERVER_INFO;
        } else if (name.equalsIgnoreCase("server_info_hover")) {
            return SERVER_INFO_HOVER;
        } else if (name.equalsIgnoreCase("server_edit")) {
            return SERVER_EDIT;
        } else if (name.equalsIgnoreCase("server_edit_hover")) {
            return SERVER_EDIT_HOVER;
        } else if (name.equalsIgnoreCase("add")) { return ADD_SERVER; }
        return null;
    }

    public static InputStream getResourceAsStream(String name) {
        if (name.equalsIgnoreCase("minecraft_font")) {
            return getResourceAsStream(BASE_PATH + "/fonts/minecraft.ttf", "");
        } else if (name.equalsIgnoreCase("defaultServers")) { return getResourceAsStream(BASE_PATH + "/data/servers", ""); }
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

    public static BufferedImage loadCachedImage(String path, String cacheLocation, BufferedImage defaultImage) {
        String name = path.split("/")[path.split("/").length - 1];
        if (!name.contains(".png")) {
            name += ".png";
        }
        BufferedImage image = Utils.loadCachedImage(cacheLocation + name);
        if (image == null) {
            try {
                URLConnection conn = new URL(path).openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(false);
                System.setProperty("http.agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
                conn.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
                HttpURLConnection.setFollowRedirects(true);
                conn.setUseCaches(false);
                ((HttpURLConnection) conn).setInstanceFollowRedirects(true);
                int response = ((HttpURLConnection) conn).getResponseCode();
                if (response == 200) {
                    image = DrawingUtils.makeColorTransparent(ImageIO.read(conn.getInputStream()), Color.magenta);
                    if ((image.getWidth() != defaultImage.getWidth()) || (image.getHeight() != defaultImage.getHeight())) {
                        BufferedImage resized = new BufferedImage(defaultImage.getWidth(), defaultImage.getHeight(), image.getType());
                        Graphics2D g = resized.createGraphics();
                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g.drawImage(image, 0, 0, defaultImage.getWidth(), defaultImage.getHeight(), 0, 0, image.getWidth(), image.getHeight(), null);
                        g.dispose();
                        image = resized;
                    }
                }
                if (image != null) {
                    ImageIO.write(image, "png", new File(cacheLocation, name));
                    return image;
                }
                return defaultImage;
            } catch (Exception e) {
                return defaultImage;
            }
        } else {
            return image;
        }
    }
}

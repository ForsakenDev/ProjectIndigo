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

public class DirectoryLocations {

    public static final String BASE_DIR_LOCATION                 = Utils.getDynamicStorageLocation();
    public static final String DATA_DIR_LOCATION                 = BASE_DIR_LOCATION + "data/";
    public static final String ASSETS_DIR_LOCATION               = BASE_DIR_LOCATION + "assets/";
    public static final String IMAGE_DIR_LOCATION                = BASE_DIR_LOCATION + "images/";
    public static final String AVATAR_CACHE_DIR_LOCATION         = BASE_DIR_LOCATION + "images/avatars/";
    public static final String SERVER_CACHE_DIR_LOCATION         = BASE_DIR_LOCATION + "images/servers/";
    public static final String BACKGROUND_DIR_LOCATION           = IMAGE_DIR_LOCATION + "background/";
    public static final String SERVERS_BASE_DIR_LOCATION         = BASE_DIR_LOCATION + "servers/";
    public static final String SERVER_DIR_LOCATION               = SERVERS_BASE_DIR_LOCATION + "%s/";
    public static final String SERVER_MINECRAFT_DIR_LOCATION     = SERVER_DIR_LOCATION + "minecraft/";
    public static final String SERVER_MINECRAFT_BIN_DIR_LOCATION = SERVER_MINECRAFT_DIR_LOCATION + "bin/";
}

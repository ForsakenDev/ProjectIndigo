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
package co.zmc.projectindigo.managers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import co.zmc.projectindigo.data.UserPassword;
import co.zmc.projectindigo.gui.components.LoginSection;
import co.zmc.projectindigo.utils.DirectoryLocations;

public class UserManager {
    protected Map<String, UserPassword> usernames = new LinkedHashMap<String, UserPassword>();

    public UserManager(LoginSection section) {
        readSavedUsernames(section);
    }

    public final List<String> getSavedUsernames() {
        return new ArrayList<String>(usernames.keySet());
    }

    public final boolean hasSavedPassword(String user) {
        return (usernames.containsKey(user)) && (usernames.get(user) != null);
    }

    public final String getSavedPassword(String user) {
        UserPassword pass = usernames.get(user);
        if (!pass.isHash()) { return pass.getPassword(); }
        return null;
    }

    public final String getUsername(String account) {
        for (String key : usernames.keySet()) {
            if (key.equalsIgnoreCase(account)) {
                UserPassword pass = usernames.get(key);
                return pass.getUsername();
            }
        }
        return account;
    }

    public final boolean removeAccount(String account) {
        Iterator<Entry<String, UserPassword>> i = usernames.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, UserPassword> e = i.next();
            if (((String) e.getKey()).equalsIgnoreCase(account)) {
                i.remove();
                return true;
            }
        }
        return false;
    }

    public final String getAccountKey(String username) {
        for (String s : usernames.keySet()) {
            if (usernames.get(s).getUsername().equalsIgnoreCase(username)) { return s; }
        }
        return "";
    }

    public final String getAccountName(String username) {
        if (usernames.containsKey(username)) { return usernames.get(username).getUsername(); }
        return username;
    }

    public final void saveUsername(String key, String user, String pass) {
        if ((!hasSavedPassword(key)) && (pass != null) && (!pass.isEmpty())) {
            usernames.put(key, new UserPassword(pass, user));
        }
    }

    private void readSavedUsernames(LoginSection section) {
        section.setFormsEnabled(false);
        try {
            File lastLogin = new File(DirectoryLocations.DATA_DIR_LOCATION, "lastlogin");
            if (!lastLogin.exists()) { return; }
            section.stateChanged("Loading stored users...", 80);
            Cipher cipher = getCipher(2, "passwordfile");
            DataInputStream dis;
            if (cipher != null) dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
            else {
                dis = new DataInputStream(new FileInputStream(lastLogin));
            }
            try {
                int extra = 5;
                while (true) {
                    String key = dis.readUTF();
                    String user = dis.readUTF();
                    section.stateChanged("Loading " + user + "...", 80 + extra);
                    extra += 5;
                    boolean isHash = dis.readBoolean();
                    if (isHash) {
                        byte[] hash = new byte[32];
                        dis.read(hash);
                        usernames.put(key, new UserPassword(hash, user));
                    } else {
                        String pass = dis.readUTF();
                        usernames.put(key, new UserPassword(pass, user));
                    }
                }
            } catch (EOFException e) {
                dis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        section.stateChanged("Finished Loading Users", 100);
        section.setFormsEnabled(true);

    }

    public final void writeUsernameList() {
        DataOutputStream dos = null;
        try {
            File lastLogin = new File(DirectoryLocations.DATA_DIR_LOCATION, "lastlogin");
            Cipher cipher = getCipher(1, "passwordfile");
            if (cipher != null) {
                dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin), cipher));
            } else {
                dos = new DataOutputStream(new FileOutputStream(lastLogin, true));
            }
            for (String user : usernames.keySet()) {
                UserPassword pass = (UserPassword) usernames.get(user);
                if (pass.getUsername() == null) {
                    pass.setUsername(user);
                }
                dos.writeUTF(user);
                dos.writeUTF(pass.getUsername());
                dos.writeBoolean(pass.isHash());
                if (pass.isHash()) {
                    dos.write(pass.getPasswordHash());
                } else {
                    dos.writeUTF(pass.getPassword());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static final Cipher getCipher(int mode, String password) throws Exception {
        Random random = new Random(43287234L);
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

        SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(mode, pbeKey, pbeParamSpec);
        return cipher;
    }

}

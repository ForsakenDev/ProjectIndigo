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
package co.zmc.projectindigo.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.WindowConstants;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.managers.DownloadHandler;
import co.zmc.projectindigo.managers.LoginHandler;
import co.zmc.projectindigo.managers.UserManager;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.Utils;

@SuppressWarnings("serial")
public abstract class BaseFrame extends JFrame {
    protected final UserManager userManager = new UserManager();
    protected final Logger      logger      = Logger.getLogger("launcher");

    public BaseFrame(int width, int height) {
        setTitle(IndigoLauncher.TITLE);
        setFont(getMinecraftFont(14));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Dimension dim = new Dimension(width, height);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, width, height);
        setLocationRelativeTo(null);
        setupLook();
    }

    public abstract void setupLook();

    public final void doLogin(String user) {
        if (!userManager.hasSavedPassword(user)) { throw new NullPointerException("There is no saved password for the user '" + user + "'"); }
        doLogin(user, userManager.getSavedPassword(user), false);
    }

    public final void doLogin(String user, String pass, boolean saveUser) {
        if (pass == null) { throw new NullPointerException("The password was null when logging in as user: '" + user + "'"); }
        LoginHandler loginHandler = new LoginHandler(this, user, pass, saveUser);
        loginHandler.execute();
    }

    public void launchGame(LoginResponse response) {
        doUpdate(response);
    }

    private final void doUpdate(final LoginResponse response) {
        final String version = "1.5.2";
        final String installPath = String.format(DirectoryLocations.MINECRAFT_DIR_LOCATION, version);
        final String binPath = String.format(DirectoryLocations.BIN_DIR_LOCATION, version);
        if (new File(installPath, "version").exists()) {
            new File(installPath, "version").delete();
        }

        if (!new File(binPath + "/minecraft.jar").exists()) {
            final ProgressMonitor progMonitor = new ProgressMonitor(this, "Downloading minecraft...", "", 0, 100);
            final DownloadHandler updater = new DownloadHandler(version) {
                @Override
                public void done() {
                    progMonitor.close();
                    try {
                        if (get()) {
                            getLogger().log(Level.INFO, "Game update complete");
                            Utils.removeMetaInf(binPath);
                            launchMinecraft(response.getUsername(), response.getSessionId());
                        } else {
                            throw new NullPointerException("Error occurred during downloading the game");
                        }
                    } catch (CancellationException e) {
                        throw new NullPointerException("Game update canceled.");
                    } catch (InterruptedException e) {
                        throw new NullPointerException("Game update interrupted.");
                    } catch (ExecutionException e) {
                        throw new NullPointerException("Failed to download game.");
                    }
                }
            };

            updater.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (progMonitor.isCanceled()) {
                        updater.cancel(false);
                    }
                    if (!updater.isDone()) {
                        int prog = updater.getProgress();
                        if (prog < 0) {
                            prog = 0;
                        } else if (prog > 100) {
                            prog = 100;
                        }
                        progMonitor.setProgress(prog);
                        progMonitor.setNote(updater.getStatus());
                    }
                }
            });
            updater.execute();
        } else {
            launchMinecraft(response.getUsername(), response.getSessionId());
        }
    }

    private void launchMinecraft(String username, String sessionId) {
        System.exit(0);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    public final Font getMinecraftFont(int size) {
        return IndigoLauncher.getMinecraftFont(size);
    }

    public Logger getLogger() {
        return logger;
    }

    public UserManager getUserManager() {
        return userManager;
    }
}

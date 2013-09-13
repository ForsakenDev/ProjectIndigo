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
package net.minecraft;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("serial")
public class Launcher extends Applet implements AppletStub {
    private Applet                    wrappedApplet;
    private URL                       documentBase;
    private boolean                   active = false;
    private final Map<String, String> params;

    public Launcher(Applet applet, URL documentBase) {
        params = new TreeMap<String, String>();
        setLayout(new BorderLayout());
        add(applet, "Center");
        wrappedApplet = applet;
        this.documentBase = documentBase;
    }

    public void addParameter(String name, String value) {
        params.put(name, value);
    }

    public void replace(Applet applet) {
        wrappedApplet = applet;
        applet.setStub(this);
        applet.setSize(getWidth(), getHeight());
        setLayout(new BorderLayout());
        add(applet, "Center");
        applet.init();
        active = true;
        applet.start();
        validate();
    }

    @Override
    public String getParameter(String name) {
        String param = params.get(name);
        if (param != null) { return param; }
        try {
            if (super.getParameterInfo() != null) { return super.getParameter(name); }
        } catch (Exception ignore) {
        }
        return null;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void appletResize(int width, int height) {
        super.setSize(width, height);
        wrappedApplet.setSize(width, height);
    }

    @Override
    public void init() {
        if (wrappedApplet != null) {
            wrappedApplet.init();
        }
    }

    @Override
    public void start() {
        wrappedApplet.start();
        active = true;
    }

    @Override
    public void stop() {
        wrappedApplet.stop();
        active = false;
    }

    public void destroy() {
        wrappedApplet.destroy();
    }

    @Override
    public URL getCodeBase() {
        return wrappedApplet.getCodeBase();
    }

    @Override
    public URL getDocumentBase() {
        return documentBase;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        wrappedApplet.setVisible(b);
    }
}

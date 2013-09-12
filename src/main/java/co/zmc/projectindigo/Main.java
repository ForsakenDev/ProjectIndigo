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
package co.zmc.projectindigo;

import java.io.IOException;

import co.zmc.projectindigo.utils.AutoUpdater;
import co.zmc.projectindigo.utils.InputStreamLogger;

public class Main {

    public Main() {
        main(new String[0]);
    }

    public Main(String defaultUser) {
        main(new String[] { defaultUser });
    }

    public static void main(String[] args) {
    	
    	launchUpdater();
    	
    	if (args.length == 1) {
            new IndigoLauncher(args[0]);
        } else {
            new IndigoLauncher("");
        }
    }
    
    private static void launchUpdater() {
    	String javaDir = System.getProperty("java.home") + "/bin/java";
        String classpath = System.getProperty("java.class.path");
        String className = AutoUpdater.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(javaDir, "-cp", classpath, className);

		try {
			Process process = builder.start();
			InputStreamLogger.start(process.getInputStream());
	        process.waitFor();
	        System.out.println("AutoUpdater finished with " + process.exitValue());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}

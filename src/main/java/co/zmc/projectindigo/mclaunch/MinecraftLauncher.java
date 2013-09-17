package co.zmc.projectindigo.mclaunch;

import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Permission;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.Main;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.security.PolicyManager;
import co.zmc.projectindigo.utils.Utils;

public class MinecraftLauncher {
    private static Logger logger = Logger.getLogger("launcher");

    public static Process launchMinecraft(Server server, String username, String sessionId, String forgename, String rmax, String maxPermSize)
            throws IOException {
        String[] jarFiles = new String[] { "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
        StringBuilder cpb = new StringBuilder("");
        File instModsDir = new File(server.getBaseDir(), "instMods/");
        if (instModsDir.isDirectory()) {
            String[] files = instModsDir.list();
            Arrays.sort(files);
            for (String name : files) {
                if (!name.equals(forgename)) {
                    if (name.toLowerCase().contains("forge") && name.toLowerCase().contains("minecraft") && name.toLowerCase().endsWith(".zip")) {
                        if (new File(instModsDir, forgename).exists()) {
                            if (!new File(instModsDir, forgename).equals(new File(instModsDir, name))) {
                                new File(instModsDir, name).delete();
                            }
                        } else {
                            new File(instModsDir, name).renameTo(new File(instModsDir, forgename));
                        }
                    } else if (!name.equalsIgnoreCase(forgename) && (name.toLowerCase().endsWith(".zip") || name.toLowerCase().endsWith(".jar"))) {
                        cpb.append(Utils.getJavaDelimiter());
                        cpb.append(new File(instModsDir, name).getAbsolutePath());
                    }
                }
            }
        } else {
            logger.log(Level.INFO, "Not loading any instMods (minecraft jar mods), as the directory does not exist.");
        }

        cpb.append(Utils.getJavaDelimiter());
        cpb.append(new File(instModsDir, forgename).getAbsolutePath().replaceAll(" ", "\\\\ "));

        for (String jarFile : jarFiles) {
            cpb.append(Utils.getJavaDelimiter());
            cpb.append(new File(server.getBinDir(), jarFile).getAbsolutePath().replaceAll(" ", "\\ "));
        }

        List<String> arguments = new ArrayList<String>();

        String separator = System.getProperty("file.separator");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java"
                + (Utils.getCurrentOS() == Utils.OS.WINDOWS ? "w" : "");
        arguments.add(path);

        setMemory(arguments, "1024");

        arguments.add("-XX:+UseConcMarkSweepGC");
        arguments.add("-XX:+CMSIncrementalMode");
        arguments.add("-XX:+AggressiveOpts");
        arguments.add("-XX:+CMSClassUnloadingEnabled");
        arguments.add("-noverify");
        if (maxPermSize.equalsIgnoreCase("")) {
            arguments.add("-XX:PermSize=128m");
        } else {
            arguments.add("-XX:PermSize=" + maxPermSize);
        }

        arguments.add("-cp");
        arguments.add(System.getProperty("java.class.path") + cpb.toString().replaceAll(" ", "\\\\ "));

        arguments.add(MinecraftLauncher.class.getCanonicalName().replaceAll(" ", "\\\\ "));
        arguments.add(server.getBaseDir().getAbsolutePath() + "/minecraft");
        arguments.add(forgename);
        arguments.add(username);
        arguments.add(sessionId);
        arguments.add(server.getIp());
        arguments.add(server.getPort() + "");
        arguments.add(IndigoLauncher.TITLE);

        ProcessBuilder processBuilder = new ProcessBuilder(arguments);
        logger.log(Level.INFO, "Setting working dir to " + server.getBaseDir().getAbsolutePath() + "/minecraft");
        processBuilder.directory(new File(server.getBaseDir().getAbsolutePath() + "/minecraft"));
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(new File(server.getBaseDir().getAbsolutePath() + "/LOG.TXT"));
        return processBuilder.start();
    }

    private static void setMemory(List<String> arguments, String rmax) {
        boolean memorySet = false;
        try {
            int min = 256;
            if (rmax != null && Integer.parseInt(rmax) > 0) {
                arguments.add("-Xms" + min + "M");
                logger.log(Level.INFO, "Setting MinMemory to " + min);
                arguments.add("-Xmx" + rmax + "M");
                logger.log(Level.INFO, "Setting MaxMemory to " + rmax);
                memorySet = true;
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error parsing memory settings: ");
            e.printStackTrace();
        }
        if (!memorySet) {
            arguments.add("-Xms" + 256 + "M");
            logger.log(Level.INFO, "Defaulting MinMemory to " + 256);
            arguments.add("-Xmx" + 1024 + "M");
            logger.log(Level.INFO, "Defaulting MaxMemory to " + 1024);
        }
    }
    
    public static void main(String[] args) {
        if (args.length < 6) {
            new Main();
            return;
        }
        String basepath = args[0], forgename = args[1], username = args[2], sessionId = args[3], ip = args[4], port = args[5], title = args[6];
        try {
            logger.log(Level.INFO, "Loading jars...");
            String[] jarFiles = new String[] { "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
            ArrayList<File> classPathFiles = new ArrayList<File>();
            File tempDir = new File(new File(basepath).getParentFile(), "instMods/");
            if (tempDir.isDirectory()) {
                for (String name : tempDir.list()) {
                    if (!name.equalsIgnoreCase(forgename)) {
                        if (name.toLowerCase().endsWith(".zip") || name.toLowerCase().endsWith(".jar")) {
                            classPathFiles.add(new File(tempDir, name));
                        }
                    }
                }
            }

            classPathFiles.add(new File(tempDir, forgename));
            for (String jarFile : jarFiles) {
                classPathFiles.add(new File(new File(basepath, "bin"), jarFile));
            }

            URL[] urls = new URL[classPathFiles.size()];
            for (int i = 0; i < classPathFiles.size(); i++) {
                try {
                    urls[i] = classPathFiles.get(i).toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                logger.log(Level.INFO, "Added URL to classpath: " + urls[i].toString());
            }

            logger.log(Level.INFO, "Loading natives...");
            String nativesDir = new File(new File(basepath, "bin"), "natives").toString();
            System.setProperty("org.lwjgl.librarypath", nativesDir);
            System.setProperty("net.java.games.input.librarypath", nativesDir);
            System.setProperty("minecraft.applet.TargetDirectory", basepath);

            URLClassLoader cl = new URLClassLoader(urls, MinecraftLauncher.class.getClassLoader());
            logger.log(Level.INFO, "Loading minecraft class");

            PolicyManager policy = new PolicyManager();
            policy.copySecurityPolicy();
            
            policy.addAdditionalPerm("permission java.lang.RuntimePermission \"*\"");
            
            policy.addAdditionalPerm("permission java.io.FilePermission \"" + new File(basepath).getParentFile().getAbsolutePath().replaceAll("\\\\", "/") + "/-\", \"read, write, delete\"");
            policy.addAdditionalPerm("permission java.io.FilePermission \"" + nativesDir.replaceAll("\\\\", "/") + "/-\", \"read\"");
            policy.addAdditionalPerm("permission java.io.FilePermission \"" + System.getProperty("java.io.tmpdir").replaceAll("\\\\", "/") + "-\", \"read, write, delete\"");
            policy.addAdditionalPerm("permission java.io.FilePermission \"" + System.getProperty("java.io.tmpdir").replaceAll("\\\\", "/") + "\", \"read, write, delete\"");
            policy.addAdditionalPerm("permission java.io.FilePermission \"" + System.getProperty("java.home").replaceAll("\\\\", "/") + "/-\", \"read\"");
            policy.addAdditionalPerm("permission java.io.FilePermission \"" + System.getProperty("java.home").replaceAll("\\\\", "/").replaceAll(" ", "%20") + "/-\", \"read\"");
            policy.addAdditionalPerm("permission java.io.FilePermission \"" + IndigoLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("\\\\", "/") + "\", \"read\"");
            
            policy.writeAdditionalPerms(policy.getPolicyLocation());
            
            System.out.println("Setting security policy to " + policy.getPolicyLocation());
            System.setProperty("java.security.policy", policy.getPolicyLocation());
            Policy.getPolicy().refresh();
            
            final File[] files = new File(nativesDir).listFiles();
            SecurityManager manager = new SecurityManager(){
            	@Override
            	public void checkPermission(Permission perm) {
           			try {
           				super.checkPermission(perm);
           			} catch (SecurityException e) {
	           			if ((perm.getName().toLowerCase().contains(".ttf") || perm.getName().toLowerCase().contains(".ttc")) && perm.getActions().equals("read")) {
	           				return;
	           			}
           				
           				if (perm.getName().contains("loadLibrary.")) {
		            		System.out.println("LOADLIB: " + perm.getName());
		            		
	           				String libPath = perm.getName().replaceAll("loadLibrary.", "");
		            		
	           				File file = new File(libPath);
		            		
		            		System.out.println("Minecraft is attempting to load native : " + file.getAbsolutePath());
		            		
		            		for (File nv : files) {
		            			if (file.getAbsolutePath().equals(nv.getAbsolutePath())) {
		            				System.out.println("Native loading permitted.");
		            				return;
		            			}
		            		}
		            	}
	            		
           				/*
           				final Permission fPerm = perm;
           				
	            		SwingWorker worker = new SwingWorker<Boolean, Void>() {
        				    @Override
        				    public Boolean doInBackground() {
        				    	System.out.println("Asking user for perm " + fPerm.toString());
        				        return (JOptionPane.showConfirmDialog(null, "A mod is trying to access something stoopid:\n" + fPerm.toString() + "\nDo you want to allow it?\nWarning this could allow the mod to access sensitive info.") == JOptionPane.YES_OPTION);
        				    }
        				};
	            		
	            		worker.execute();
	            		
	            		boolean allowed = false;
	            		
        				try {
							System.out.println("Waiting for response...");
        					allowed = (Boolean) worker.get();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							e1.printStackTrace();
						}
	            		
        				if (!allowed) {
	            			System.out.println("Not allowing permission.");
	            			throw e;
						}	 
						*/           		
           				
           			}
            	}
            };
            
            
            System.setSecurityManager(manager);

            try {
                Class<?> MCAppletClass = cl.loadClass("net.minecraft.client.MinecraftApplet");
                Applet mcappl = (Applet) MCAppletClass.newInstance();
                MinecraftFrame mcWindow = new MinecraftFrame(title);
                mcWindow.start(mcappl, basepath, username, sessionId, ip, port);
            } catch (InstantiationException e) {
                logger.log(Level.INFO, "Applet wrapper failed! Falling back to compatibility mode.");
                e.printStackTrace();
            }
        } catch (Throwable t) {
            logger.log(Level.INFO, "Unhandled error launching minecraft");
            t.printStackTrace();
        }
    }
}

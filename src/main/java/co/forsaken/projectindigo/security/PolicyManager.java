package co.forsaken.projectindigo.security;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;
import java.security.Policy;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import co.forsaken.projectindigo.IndigoLauncher;
import co.forsaken.projectindigo.utils.DirectoryLocations;
import co.forsaken.projectindigo.utils.FileUtils;

public class PolicyManager {

  private ArrayList<String> additionalPerms = new ArrayList<String>();
  private String            policyLocation  = "";
  private Logger            logger          = Logger.getLogger("launcher");

  public boolean copySecurityPolicy() {
    InputStream policy = IndigoLauncher.class.getResourceAsStream("/co/forsaken/projectindigo/resources/security/security.policy");
    File newPolicyFile = new File(DirectoryLocations.BACKEND_DATA_DIR.format("security.policy"));

    policyLocation = newPolicyFile.getAbsolutePath();
    logger.log(Level.INFO, "Copying over new security policy.");
    FileUtils.writeStreamToFile(policy, new File(policyLocation));
    return true;
  }

  public void addAdditionalPerm(String perm) {
    additionalPerms.add("\ngrant{" + perm + ";};");
  }

  public void writeAdditionalPerms(String location) {
    try {
      FileWriter out = new FileWriter(location, true);
      out.write("\n//AUTO-GENERATED PERMS BEGIN\n");
      for (String perm : additionalPerms) {
        logger.log(Level.INFO, "Writing additional perm " + perm.replaceAll("\n", ""));
        out.write(perm);
      }
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getPolicyLocation() {
    return policyLocation;
  }

  public void enforceSecurityManager(String basepath, String nativesDir) {
    copySecurityPolicy();

    addAdditionalPerm("permission java.lang.RuntimePermission \"*\"");

    addAdditionalPerm("permission java.io.FilePermission \"" + new File(basepath).getParentFile().getAbsolutePath().replaceAll("\\\\", "/") + "/-\", \"read, write, delete\"");
    addAdditionalPerm("permission java.io.FilePermission \"" + nativesDir.replaceAll("\\\\", "/") + "/-\", \"read\"");
    addAdditionalPerm("permission java.io.FilePermission \"" + System.getProperty("java.io.tmpdir").replaceAll("\\\\", "/") + "-\", \"read, write, delete\"");
    addAdditionalPerm("permission java.io.FilePermission \"" + System.getProperty("java.io.tmpdir").replaceAll("\\\\", "/") + "\", \"read, write, delete\"");
    addAdditionalPerm("permission java.io.FilePermission \"" + System.getProperty("java.home").replaceAll("\\\\", "/") + "/-\", \"read\"");
    addAdditionalPerm("permission java.io.FilePermission \"" + System.getProperty("java.home").replaceAll("\\\\", "/").replaceAll(" ", "%20") + "/-\", \"read\"");
    addAdditionalPerm("permission java.io.FilePermission \"" + IndigoLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("\\\\", "/") + "\", \"read\"");

    writeAdditionalPerms(getPolicyLocation());

    System.out.println("Setting security policy to " + getPolicyLocation());
    System.setProperty("java.security.policy", getPolicyLocation());
    Policy.getPolicy().refresh();

    File[] natives = new File(nativesDir).listFiles();
    System.setSecurityManager(getSecurityManager(natives));
  }

  private SecurityManager getSecurityManager(final File[] natives) {

    SecurityManager manager = new SecurityManager() {
      @Override public void checkPermission(Permission perm) {
        try {
          super.checkPermission(perm);
        } catch (SecurityException e) {
          if ((perm.getName().toLowerCase().contains(".ttf") || perm.getName().toLowerCase().contains(".ttc")) && perm.getActions().equals("read")) { return; }

          if (perm.getName().contains("loadLibrary.")) {
            System.out.println("LOADLIB: " + perm.getName());

            String libPath = perm.getName().replaceAll("loadLibrary.", "");

            File file = new File(libPath);

            System.out.println("Minecraft is attempting to load native : " + file.getAbsolutePath());

            for (File nv : natives) {
              if (file.getAbsolutePath().equals(nv.getAbsolutePath())) {
                System.out.println("Native loading permitted.");
                return;
              }
            }
          }

          final Permission fPerm = perm;

          SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

            @Override public Boolean doInBackground() {
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

        }
      }
    };

    return manager;
  }

}

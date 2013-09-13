package co.zmc.projectindigo.security;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.FileUtils;

public class PolicyManager {

    private ArrayList<String> additionalPerms = new ArrayList<String>();
    private String            policyLocation  = "";
    private Logger            logger          = Logger.getLogger("launcher");

    public boolean copySecurityPolicy() {
        InputStream policy = IndigoLauncher.class.getResourceAsStream("/co/zmc/projectindigo/resources/security/security.policy");
        File newPolicyFile = new File(DirectoryLocations.DATA_DIR_LOCATION + "security.policy");

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

}

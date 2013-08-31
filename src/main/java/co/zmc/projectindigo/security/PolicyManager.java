package co.zmc.projectindigo.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.utils.DirectoryLocations;

public class PolicyManager {

	private ArrayList<String> additionalPerms = new ArrayList<String>();
	private String policyLocation = "";
	
    public boolean copySecurityPolicy() {
        InputStream policy = IndigoLauncher.class.getResourceAsStream("/co/zmc/projectindigo/resources/security/security.policy");
        File newPolicyFile = new File(DirectoryLocations.DATA_DIR_LOCATION + "security.policy");
        policyLocation = newPolicyFile.getAbsolutePath();
        System.out.println("Copying over new security policy.");
        try {
            OutputStream newOut = new FileOutputStream(newPolicyFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = policy.read(buffer)) > 0) {
                newOut.write(buffer, 0, read);
            }
            newOut.close();
            policy.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Success.");
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
                System.out.println("Writing additional perm " + perm.replaceAll("\n", ""));
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

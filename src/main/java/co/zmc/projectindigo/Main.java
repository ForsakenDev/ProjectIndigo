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
        if (!launchUpdater()) {
            System.exit(0);
        }

        if (args.length == 1) {
            new IndigoLauncher(args[0]);
        } else {
            new IndigoLauncher("");
        }
    }

    private static boolean launchUpdater() {
        String javaDir = System.getProperty("java.home") + "/bin/java";
        String classpath = System.getProperty("java.class.path");
        String className = AutoUpdater.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(javaDir, "-cp", classpath, className);

        try {
            Process process = builder.start();
            InputStreamLogger.start(process.getInputStream());
            process.waitFor();
            return process.exitValue() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}

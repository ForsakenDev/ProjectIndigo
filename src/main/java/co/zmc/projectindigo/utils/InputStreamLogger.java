package co.zmc.projectindigo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputStreamLogger extends Thread {
    private final InputStream is;

    private InputStreamLogger(InputStream from) {
        is = from;
    }

    @Override
    public void run() {
        byte buffer[] = new byte[4096];
        String logBuffer = "";
        int newLineIndex;
        int nullIndex;
        try {
            while (is.read(buffer) > 0) {
                logBuffer += new String(buffer).replace("\r\n", "\n");
                nullIndex = logBuffer.indexOf(0);
                if (nullIndex != -1) {
                    logBuffer = logBuffer.substring(0, nullIndex);
                }
                while ((newLineIndex = logBuffer.indexOf("\n")) != -1) {
                    logBuffer = logBuffer.substring(newLineIndex + 1);
                }
                Arrays.fill(buffer, (byte) 0);
            }
        } catch (IOException e) {
            Logger.getLogger("launcher").log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void start(InputStream from) {
        InputStreamLogger processStreamRedirect = new InputStreamLogger(from);
        processStreamRedirect.start();
    }
}
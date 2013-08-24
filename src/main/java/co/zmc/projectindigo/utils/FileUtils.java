package co.zmc.projectindigo.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    public static void writeStringToFile(String str, File file) {
        BufferedWriter writer = null;
        try {
            file.delete();
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

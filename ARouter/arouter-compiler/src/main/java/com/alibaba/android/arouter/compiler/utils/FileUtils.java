package com.alibaba.android.arouter.compiler.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 */
public class FileUtils {

    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        return dir.delete();
    }

    public static void writeFile(String content, String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(path, true);
            writer.write(content);
            writer.write("\n");

        } catch (Exception e) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        }

    }
}

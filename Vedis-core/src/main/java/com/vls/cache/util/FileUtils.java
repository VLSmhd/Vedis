package com.vls.cache.util;

import com.github.houbb.heaven.response.exception.CommonRuntimeException;
import com.github.houbb.heaven.util.common.ArgUtil;
import com.github.houbb.heaven.util.lang.StringUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static boolean createFile(String filePath) {
        if (StringUtil.isEmpty(filePath)) {
            return false;
        } else if (exists(filePath)) {
            return true;
        } else {
            File file = new File(filePath);
//            File dir = file.getParentFile();
//            if (notExists(dir)) {
//                boolean mkdirResult = dir.mkdirs();
//                if (!mkdirResult) {
//                    return false;
//                }
//            }
            try {
                return file.createNewFile();
            } catch (IOException var4) {
                throw new CommonRuntimeException(var4);
            }
        }
    }

    public static boolean exists(String filePath, LinkOption... options) {
        if (StringUtil.isEmpty(filePath)) {
            return false;
        } else {
            Path path = Paths.get(filePath);
            return Files.exists(path, options);
        }
    }

    public static void truncateFile(String filePath){
        File file  = new File(filePath);
        try {
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

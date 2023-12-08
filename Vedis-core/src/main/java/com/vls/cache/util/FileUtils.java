package com.vls.cache.util;

import com.github.houbb.heaven.response.exception.CommonRuntimeException;
import com.github.houbb.heaven.util.common.ArgUtil;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.StringUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.*;
import java.util.Collection;
import java.util.Iterator;

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


    public static void append(String filePath, Collection<String> collection) {
        write(filePath, (Iterable)collection, StandardOpenOption.APPEND);
    }

    public static void write(String filePath, Iterable<? extends CharSequence> lines, OpenOption... openOptions) {
        write(filePath, lines, "UTF-8", openOptions);
    }

    public static void write(String filePath, Iterable<? extends CharSequence> lines, String charset, OpenOption... openOptions) {
        try {
            ArgUtil.notNull(lines, "charSequences");
            CharsetEncoder encoder = Charset.forName(charset).newEncoder();
            Path path = Paths.get(filePath);
            Path pathParent = path.getParent();
            if (pathParent != null) {
                File parent = pathParent.toFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
            }

            OutputStream out = path.getFileSystem().provider().newOutputStream(path, openOptions);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, encoder));
            Throwable var9 = null;

            try {
                Iterator var10 = lines.iterator();

                while(var10.hasNext()) {
                    CharSequence line = (CharSequence)var10.next();
                    writer.append(line);
                    writer.newLine();
                }
            } catch (Throwable var20) {
                var9 = var20;
                throw var20;
            } finally {
                if (writer != null) {
                    if (var9 != null) {
                        try {
                            writer.close();
                        } catch (Throwable var19) {
                            var9.addSuppressed(var19);
                        }
                    } else {
                        writer.close();
                    }
                }

            }

        } catch (IOException var22) {
            throw new CommonRuntimeException(var22);
        }
    }

}

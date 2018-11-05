package com.github.kaelthasbmg.lucene.utils;

import com.github.kaelthasbmg.lucene.exceptions.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 文件操作工具类
 *
 * @author Carsymor
 */
public final class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 按照指定路径创建文件夹
     * @param filePath 文件夹绝对路径
     */
    public static void createFolder(String filePath) {
        if (filePath == null) {
            throw new InvalidParameterException("invalid filePath(filePath = null)");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = null;
    }

    /**
     * 清空并重建文件夹
     * @param filePath 文件夹绝对路径
     */
    public static void clearFolder(String filePath) {
        if (filePath == null) {
            throw new InvalidParameterException("invalid filePath(filePath = null)");
        }

        File file = new File(filePath);
        deleteFileOrFolder(file);

        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * 删除文件或文件夹
     * @param file 文件或文件夹对象
     */
    public static void deleteFileOrFolder(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();

                if (subFiles != null) {
                    for (File subFile : subFiles) {
                        deleteFileOrFolder(subFile);
                    }
                }
                file.delete();
            } else {
                file.delete();
            }
        }
    }

    /**
     * 获取文件内容
     * @param dataFilePath 文件绝对路径
     * @return 文件内容
     */
    public static String getContent(String dataFilePath) {
        File dataFile = new File(dataFilePath);
        return getContent(dataFile);
    }

    /**
     * 获取文件内容
     * @param dataFile 文件对象
     * @return 文件内容
     */
    public static String getContent(File dataFile) {
        if (!dataFile.exists()) {
            throw new InvalidParameterException("文件不存在，dataFilePath：" + dataFile.getAbsolutePath());
        }

        if (dataFile.isDirectory()) {
            throw new InvalidParameterException("指定路径为文件夹路径，dataFilePath：" + dataFile.getAbsolutePath());
        }

        FileInputStream inputStream = null;
        String content = null;
        try {
            inputStream = new FileInputStream(dataFile);
            int fileLength = inputStream.available();
            byte[] fileBytes = new byte[fileLength];

            inputStream.read(fileBytes);
            content = new String(fileBytes, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }

        return content;
    }

    /**
     * 判断文件路径是否存在
     * @param path 文件路径
     * @return 若文件路径存在，则返回true，否则返回false
     */
    public static boolean hasIndexInfo(String path) {
        File file = new File(path);
        boolean hasIndexInfo = file.exists();

        if (hasIndexInfo) {
            hasIndexInfo = file.isDirectory() && file.list().length > 0;
        }
        file = null;

        return hasIndexInfo;
    }
}

package com.xvvvan.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtil {
    public static String convertStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8);
    }
    public static byte[] readBytesFromFile(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        while ((len = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }

        bos.flush();
        byte[] fileBytes = bos.toByteArray();
        fis.close();
        bos.close();

        return fileBytes;
    }
    public static String readFile(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        String result = "";
        try {
            String tempString = null;
            reader = new BufferedReader(new FileReader(file));
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                result =result+tempString+"\n";
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return result;
    }

    public static boolean fileExists(String payloadStr) {
        Path path = Paths.get(payloadStr);
        return Files.exists(path) && !Files.isDirectory(path);

    }
    public static String calculateMD5(byte[] content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(content);

            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}

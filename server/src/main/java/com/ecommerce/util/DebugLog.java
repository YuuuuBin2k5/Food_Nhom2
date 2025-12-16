package com.ecommerce.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugLog {

    private static final Path LOG_PATH;

    static {
        String base = System.getProperty("catalina.base");
        if (base == null || base.isEmpty()) base = System.getProperty("user.dir");
        LOG_PATH = new File(base, "server-debug.log").toPath();
        try {
            if (!Files.exists(LOG_PATH)) Files.createFile(LOG_PATH);
        } catch (IOException ignored) {}
    }

    public static synchronized void append(String message) {
        try {
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            String line = time + " " + message + System.lineSeparator();
            Files.write(LOG_PATH, line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }
}

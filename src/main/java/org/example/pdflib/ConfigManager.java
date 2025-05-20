package org.example.pdflib;

import java.io.*;
import java.util.Properties;

/**
 * Prosty menedżer konfiguracji oparty na pliku properties.
 */
public class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";
    private static Properties props = new Properties();

    static {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
        } catch (IOException ignored) {
        }
    }

    public static String getReportPath() {
        return props.getProperty("report.path", "");
    }

    public static boolean isLoggingEnabled() {
        return Boolean.parseBoolean(props.getProperty("logging.enabled", "true"));
    }

    public static boolean isNotificationsEnabled() {
        return Boolean.parseBoolean(props.getProperty("notifications.enabled", "true"));
    }

    public static void setReportPath(String path) {
        props.setProperty("report.path", path);
        save();
    }

    public static void setLoggingEnabled(boolean on) {
        props.setProperty("logging.enabled", Boolean.toString(on));
        save();
    }

    public static void setNotificationsEnabled(boolean on) {
        props.setProperty("notifications.enabled", Boolean.toString(on));
        save();
    }

    private static void save() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            props.store(output, "Ustawienia aplikacji");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

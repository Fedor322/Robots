package gui.configuration;

import java.util.Properties;

public record WindowState(
        String id,
        int x,
        int y,
        int width,
        int height,
        int extendedState,
        boolean maximized,
        boolean minimized
) {
    public WindowState(String id, Properties properties) {
        this(
                id,
                loadInt(id + ".x", "0", properties),
                loadInt(id + ".y", "0", properties),
                loadInt(id + ".width", "800", properties),
                loadInt(id + ".height", "0", properties),
                loadInt(id + ".extendedState", "0", properties),
                loadMaximized(id, properties),
                loadMinimized(id, properties));
    }

    private static int loadInt(String key, String defaultValue, Properties properties) {
        return Integer.parseInt(properties.getProperty(key, defaultValue));
    }

    private static boolean loadBoolean(String key, Properties properties) {
        return Boolean.parseBoolean(properties.getProperty(key, "false"));
    }

    private static boolean loadMaximized(String id, Properties properties) {
        return loadBoolean(id + ".maximized", properties);
    }

    private static boolean loadMinimized(String id, Properties properties) {
        return loadBoolean(id + ".minimized", properties);
    }

    private void saveProperty(String key, String value, Properties properties) {
        properties.setProperty(id + "." + key, value);
    }
    public void saveX(Properties properties) {
        saveProperty("x", String.valueOf(x), properties);
    }

    public void saveY(Properties properties) {
        saveProperty("y", String.valueOf(y), properties);
    }

    public void saveWidth(Properties properties) {
        saveProperty("width", String.valueOf(width), properties);
    }

    public void saveHeight(Properties properties) {
        saveProperty("height", String.valueOf(height), properties);
    }

    public void saveExtendedState(Properties properties) {
        saveProperty("extendedState", String.valueOf(extendedState), properties);
    }

    public void saveMaximized(Properties properties) {
        saveProperty("maximized", String.valueOf(maximized), properties);
    }

    public void saveMinimized(Properties properties) {
        saveProperty("minimized", String.valueOf(minimized), properties);
    }
}


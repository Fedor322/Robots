package gui.configuration;

import log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class WindowPropertiesManager {
    private final Properties properties;

    public WindowPropertiesManager(Properties properties) {
        this.properties = properties;
    }

    public void load(String configPath) {
        File file = new File(configPath);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
                Logger.debug("Loaded properties from " + configPath);
                System.out.println("Loaded properties from " + configPath);
            } catch (IOException e) {
                Logger.error(e.getMessage());
            }
        } else {
            Logger.error("Config file not found: " + configPath);
            System.out.println("Config file not found: " + configPath);
        }
    }

    public void save(String configPath) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(configPath)) {
            properties.store(fileOutputStream, "Window states");
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }


    public void saveWindowState(String id, WindowState state) {
        properties.setProperty(id + ".x", String.valueOf(state.x()));
        properties.setProperty(id + ".y", String.valueOf(state.y()));
        properties.setProperty(id + ".width", String.valueOf(state.width()));
        properties.setProperty(id + ".height", String.valueOf(state.height()));
        properties.setProperty(id + ".extendedState", String.valueOf(state.extendedState()));
        properties.setProperty(id + ".maximized", String.valueOf(state.maximized()));
        properties.setProperty(id + ".minimized", String.valueOf(state.minimized()));
    }

    public WindowState loadWindowState(String id) {
        String xStr = properties.getProperty(id + ".x");
        String yStr = properties.getProperty(id + ".y");
        String widthStr = properties.getProperty(id + ".width");
        String heightStr = properties.getProperty(id + ".height");
        String extStr = properties.getProperty(id + ".extendedState");
        String maxStr = properties.getProperty(id + ".maximized");
        String minStr = properties.getProperty(id + ".minimized");
        if (xStr != null && yStr != null && widthStr != null && heightStr != null) {
            int extendedState = extStr != null ? Integer.parseInt(extStr) : 0;
            boolean maximized = Boolean.parseBoolean(maxStr);
            boolean minimized = Boolean.parseBoolean(minStr);
            return new WindowState(
                    Integer.parseInt(xStr),
                    Integer.parseInt(yStr),
                    Integer.parseInt(widthStr),
                    Integer.parseInt(heightStr),
                    extendedState,
                    maximized,
                    minimized
            );
        }
        return null;
    }
}

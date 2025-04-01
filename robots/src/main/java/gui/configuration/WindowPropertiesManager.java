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


    public void saveWindowState(String id, WindowState state) {state.saveX(properties);
        state.saveY(properties);
        state.saveWidth(properties);
        state.saveHeight(properties);
        state.saveExtendedState(properties);
        state.saveMaximized(properties);
        state.saveMinimized(properties);
    }


    public WindowState loadWindowState(String id) {
        return new WindowState(id, properties);
    }
}

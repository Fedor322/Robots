package gui.configuration;

import log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public final class WindowPropertiesManager {
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
        state.updateAllProperties();
    }


    public WindowState loadWindowState(String id) {
        return new WindowState(id, properties);
    }

    public Properties properties() {
        return properties;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (WindowPropertiesManager) obj;
        return Objects.equals(this.properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

    @Override
    public String toString() {
        return "WindowPropertiesManager[" +
                "properties=" + properties + ']';
    }

}

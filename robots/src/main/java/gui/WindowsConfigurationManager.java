package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class WindowsConfigurationManager {

    private static final String CONFIG_PATH = System.getProperty("user.home")
            + File.separator + "config.cfg";

    private final Properties properties = new Properties();


    public void saveConfiguration() {
        for (Frame frame : Frame.getFrames()) {
            if (frame.isShowing() && frame instanceof StorableWindow storableWindow) {
                saveConfigurationFrameComponent(frame, storableWindow.getId());
            }
            if (frame instanceof MainApplicationFrame mainApplicationFrame) {
                JDesktopPane jDesktopPane = mainApplicationFrame.getDesktopPane();
                for (JInternalFrame internalFrame : jDesktopPane.getAllFrames()) {
                    if (internalFrame.isShowing() && internalFrame instanceof StorableWindow internalStorable) {
                        saveConfigurationFrameComponent(internalFrame, internalStorable.getId());
                    }
                }

            }

        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(CONFIG_PATH)) {
            properties.store(fileOutputStream, "Window states");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadConfiguration() {
        File file = new File(CONFIG_PATH);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
                System.out.println("Loaded properties from " + CONFIG_PATH);
            } catch (IOException e) {
                Logger.debug(e.getMessage());
            }
        } else {
            System.out.println("Config file not found: " + CONFIG_PATH);
        }
    }

    public void saveConfigurationFrameComponent(Component frameComponent, String id) {
        properties.setProperty(id + ".x", String.valueOf(frameComponent.getX()));
        properties.setProperty(id + ".y", String.valueOf(frameComponent.getY()));
        properties.setProperty(id + ".width", String.valueOf(frameComponent.getWidth()));
        properties.setProperty(id + ".height", String.valueOf(frameComponent.getHeight()));
        if (frameComponent instanceof JFrame jFrame) {
            properties.setProperty(id + ".extendedState", String.valueOf(jFrame.getExtendedState()));
        }
        if (frameComponent instanceof JInternalFrame jInternalFrame) {
            properties.setProperty(id + ".max", String.valueOf(jInternalFrame.isMaximum()));
            properties.setProperty(id + ".icon", String.valueOf(jInternalFrame.isIcon()));
        }
    }


    public void getAllConfigurationFrameComponent() {
        for (Frame frame : Frame.getFrames()) {
            if (frame instanceof StorableWindow storableWindow) {
                getConfigurationFrameComponent(frame, storableWindow.getId());
            }
            if (frame instanceof MainApplicationFrame mainApplicationFrame) {
                JDesktopPane jDesktopPane = mainApplicationFrame.getDesktopPane();
                for (JInternalFrame internalFrame : jDesktopPane.getAllFrames()) {
                    if (internalFrame instanceof StorableWindow internalStorable) {
                        getConfigurationFrameComponent(internalFrame, internalStorable.getId());
                    }
                }
            }
        }

    }

    public void getConfigurationFrameComponent(Component frameComponent, String id) {

        String x = properties.getProperty(id + ".x");
        String y = properties.getProperty(id + ".y");
        String width = properties.getProperty(id + ".width");
        String height = properties.getProperty(id + ".height");
        if (x != null && y != null && width != null && height != null) {
            frameComponent.setBounds(
                    Integer.parseInt(x),
                    Integer.parseInt(y),
                    Integer.parseInt(width),
                    Integer.parseInt(height));
        }
        if (frameComponent instanceof JFrame jFrame) {
            String extendedState = properties.getProperty(id + ".extendedState");
            if (extendedState != null) {
                jFrame.setExtendedState(Integer.parseInt(extendedState));
            }
        }

        if (frameComponent instanceof JInternalFrame internalFrame) {
            String maxState = properties.getProperty(id + ".max");
            String iconState = properties.getProperty(id + ".icon");
            try {
                if (maxState != null) {
                    internalFrame.setMaximum(Boolean.parseBoolean(maxState));
                }
                if (iconState != null) {
                    internalFrame.setIcon(Boolean.parseBoolean(iconState));
                }
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }

        }


    }

}

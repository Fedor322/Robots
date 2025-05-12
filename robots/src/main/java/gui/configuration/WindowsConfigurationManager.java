package gui.configuration;

import log.Logger;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.Properties;

public class WindowsConfigurationManager {


    private static final String LOCAL_CONFIG_PATH = "config.properties";
    private static final String CONFIG_PATH = System.getProperty("user.home")
            + File.separator + LOCAL_CONFIG_PATH;

    private final WindowPropertiesManager windowPropertiesManager = new WindowPropertiesManager(new Properties());

    public void saveConfiguration() {

        for (StorableWindow storableWindow : WindowsRegistry.getWindows()) {
            if (storableWindow instanceof Component comp && comp.isShowing()) {
                saveConfigurationFrameComponent(comp, storableWindow.getId());
            }
            windowPropertiesManager.save(CONFIG_PATH);
            windowPropertiesManager.save(LOCAL_CONFIG_PATH);
        }
    }

    public void loadConfiguration() {
        windowPropertiesManager.load(LOCAL_CONFIG_PATH);
    }

    public void saveConfigurationFrameComponent(Component frameComponent, String id) {
        int extendedState = 0;
        boolean maximized = false;
        boolean minimized = false;
        if (frameComponent instanceof JFrame jFrame) {
            extendedState = jFrame.getExtendedState();
        }
        if (frameComponent instanceof JInternalFrame jInternalFrame) {
            maximized = jInternalFrame.isMaximum();
            minimized = jInternalFrame.isIcon();
        }
        WindowState state = new WindowState(
                id,
                frameComponent.getX(),
                frameComponent.getY(),
                frameComponent.getWidth(),
                frameComponent.getHeight(),
                extendedState,
                maximized,
                minimized,
                windowPropertiesManager.properties()
        );
        windowPropertiesManager.saveWindowState(id, state);
    }


    public void loadAllConfigurationFrameComponent() {
        for (StorableWindow window : WindowsRegistry.getWindows()) {
            if (window instanceof Component comp) {
                loadConfigurationFrameComponent(comp, window.getId());
            }
        }
    }

    public void loadConfigurationFrameComponent(Component frameComponent, String id) {
        WindowState windowState = windowPropertiesManager.loadWindowState(id);

        frameComponent.setBounds(
                windowState.getX(),
                windowState.getY(),
                windowState.getWidth(),
                windowState.getHeight()
        );
        if (frameComponent instanceof JFrame jFrame) {
            jFrame.setExtendedState(windowState.getExtendedState());
        }

        if (frameComponent instanceof JInternalFrame internalFrame) {
            try {
                internalFrame.setMaximum(windowState.isMaximized());
                internalFrame.setIcon(windowState.isMinimized());
            } catch (PropertyVetoException e) {
                Logger.error(e.getMessage());
            }
        }
    }
}

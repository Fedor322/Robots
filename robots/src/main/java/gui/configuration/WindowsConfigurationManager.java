package gui.configuration;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Properties;

public class WindowsConfigurationManager {

    private static final String CONFIG_PATH = System.getProperty("user.home")
            + File.separator + "config.properties";

    private final Properties properties = new Properties();
    private final WindowPropertiesManager windowPropertiesManager = new WindowPropertiesManager(new Properties());

    public void saveConfiguration() {
        for (Frame frame : Frame.getFrames()) {
            if (frame.isShowing()) {
                saveStorableWindow(frame);
                JDesktopPane jDesktopPane = getDesktopPane(frame);
                for (JInternalFrame internalFrame : Objects.requireNonNull(jDesktopPane).getAllFrames()) {
                    if (internalFrame.isShowing()) {
                        saveStorableWindow(internalFrame);
                    }
                }

            }
        }
        windowPropertiesManager.save(CONFIG_PATH);
    }

    public void loadConfiguration() {
        windowPropertiesManager.load(CONFIG_PATH);
    }

    private void saveStorableWindow(Component frameComponent) {
        if (implementsInterface(frameComponent.getClass(), "StorableWindow")) {
            try {
                Method getIdMethod = frameComponent.getClass().getMethod("getId");
                String id = (String) getIdMethod.invoke(frameComponent);
                saveConfigurationFrameComponent(frameComponent, id);
            } catch (Exception e) {
                Logger.error(e.getMessage());
            }
        }
    }

    private void loadStorableWindow(Component frameComponent) {
        if (implementsInterface(frameComponent.getClass(), "StorableWindow")) {
            try {
                Method getIdMethod = frameComponent.getClass().getMethod("getId");
                String id = (String) getIdMethod.invoke(frameComponent);
                loadConfigurationFrameComponent(frameComponent, id);
            } catch (Exception e) {
                Logger.error(e.getMessage());
            }
        }
    }

    private boolean implementsInterface(Class<?> clas, String interfaceName) {
        for (Class<?> currInterface : clas.getInterfaces()) {
            if (currInterface.getSimpleName().equals(interfaceName)) {
                return true;
            }
        }
        return false;
    }

    private JDesktopPane getDesktopPane(Component frameComponent) {
        try {
            Method getMethod = frameComponent.getClass().getMethod("getDesktopPane");
            return (JDesktopPane) getMethod.invoke(frameComponent);
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return null;
        }
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
                frameComponent.getX(),
                frameComponent.getY(),
                frameComponent.getWidth(),
                frameComponent.getHeight(),
                extendedState, maximized,
                minimized);
        windowPropertiesManager.saveWindowState(id, state);
    }


    public void loadAllConfigurationFrameComponent() {
        for (Frame frame : Frame.getFrames()) {
            loadStorableWindow(frame);
            JDesktopPane jDesktopPane = getDesktopPane(frame);
            for (JInternalFrame internalFrame : jDesktopPane.getAllFrames()) {
                if (internalFrame instanceof StorableWindow internalStorable) {
                    loadConfigurationFrameComponent(internalFrame, internalStorable.getId());
                }
            }
        }
    }

    public void loadConfigurationFrameComponent(Component frameComponent, String id) {
        WindowState windowState = windowPropertiesManager.loadWindowState(id);
        if (windowState == null) {
            Logger.error("Не удалось загрузить состояние для " + id);
            return;
        }
        frameComponent.setBounds(
                windowState.x(),
                windowState.y(),
                windowState.width(),
                windowState.height()
        );
        if (frameComponent instanceof JFrame jFrame) {
            jFrame.setExtendedState(windowState.extendedState());
        }

        if (frameComponent instanceof JInternalFrame internalFrame) {
            try {
                internalFrame.setMaximum(windowState.maximized());
                internalFrame.setIcon(windowState.minimized());
            } catch (PropertyVetoException e) {
                Logger.error(e.getMessage());
            }
        }


    }

}

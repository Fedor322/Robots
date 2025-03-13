package gui;


import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import java.awt.*;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConfigurationWindow {
    private static final String CONFIG_FILE = System.getProperty("user.home")
            + File.separator + "windows.properties";

    public void saveConfiguration(Component frameComponent, ConfigWindowType nameWindow) {
        int extendedState = 0;
        boolean isMaximum = false;
        boolean isIconfied = false;
        if (frameComponent instanceof JFrame) {
            extendedState = ((JFrame) frameComponent).getExtendedState();
        }
        if (frameComponent instanceof JInternalFrame internalFrame) {
            isMaximum = internalFrame.isMaximum();
            isIconfied = internalFrame.isIcon();
        }
        FrameWindowState state = new FrameWindowState(
                new Rectangle(
                        frameComponent.getX(),
                        frameComponent.getY(),
                        frameComponent.getWidth(),
                        frameComponent.getHeight()
                ),
                extendedState,
                isMaximum,
                isIconfied
        );
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.home") + nameWindow.getFileName()))) {
            outputStream.writeObject(state);
        } catch (IOException e) {
            System.out.println("Не удалось сохранить");
        }
    }

    public void getConfiguration(Component frameComponent, ConfigWindowType windowName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(windowName.getFileName()))) {
            FrameWindowState state = (FrameWindowState) inputStream.readObject();
            frameComponent.setBounds(state.rectangle().x, state.rectangle().y, state.rectangle().width, state.rectangle().height);
            if (frameComponent instanceof JFrame) {
                ((JFrame) frameComponent).setExtendedState(state.extendedState());
            }
            if (frameComponent instanceof JInternalFrame internal) {
                try {
                    internal.setMaximum(state.isMaximized());
                    internal.setIcon(state.isIconifized());
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Конфигурация" + windowName + " окна не найдена");
        }
    }
}

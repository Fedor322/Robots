package gui;


import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import java.awt.Component;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConfigurationWindow {

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
                frameComponent.getX(),
                frameComponent.getY(),
                frameComponent.getWidth(),
                frameComponent.getHeight(),
                extendedState,
                isMaximum,
                isIconfied
        );
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(nameWindow.getFileName()))) {
            outputStream.writeObject(state);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось сохранить");
        }
    }

    public void getConfiguration(Component frameComponent, ConfigWindowType windowName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(windowName.getFileName()))) {
            FrameWindowState state = (FrameWindowState) inputStream.readObject();
            frameComponent.setBounds(state.x(), state.y(), state.width(), state.height());
            if (frameComponent instanceof JFrame) {
                ((JFrame) frameComponent).setExtendedState(state.extendedState());
            }
            if (frameComponent instanceof JInternalFrame internal) {
                try {
                    internal.setMaximum(state.isMaximized());
                    internal.setIcon(state.isIconified());
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Конфигурация" + windowName + " окна не найдена");
        }
    }
}

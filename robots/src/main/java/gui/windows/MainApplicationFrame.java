package gui.windows;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.*;


import gui.GridController;
import gui.RobotServer;
import gui.configuration.StorableWindow;
import gui.configuration.WindowsConfigurationManager;
import gui.configuration.WindowsRegistry;
import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 */
public class MainApplicationFrame extends JFrame implements StorableWindow {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowsConfigurationManager windowsConfigurationManager = new WindowsConfigurationManager();
    private GridController gridController;
    private RobotServer robotServer;

    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }
    public WindowsConfigurationManager getWindowsConfigurationManager() {return windowsConfigurationManager;}
    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);
        desktopPane.setUI(null);
        setContentPane(desktopPane);
        gridController = new GridController(40, 40);
        windowsConfigurationManager.loadConfiguration();

        addWindow(createLogWindow());
        GameWindow gameWindow = createGameWindow();

        addWindow(gameWindow);
        addWindow(createGridObstaclesWindow());
        setJMenuBar(generateMenuBar());

        startRobotServer(gameWindow);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitFromApplication();
            }
        });
        WindowsRegistry.register(this);
    }



    private void startRobotServer(GameWindow gameWindow) {
        robotServer = new RobotServer(9836,gameWindow.getVisualizer());
        robotServer.start();
        Logger.debug("Сервер запущен на " + 9836);


    }


    @Override
    public String getId() {
        return "MainApplicationFrame";
    }

    private JInternalFrame createGridObstaclesWindow() {
        GridObstaclesWindow gridObstaclesWindow = new GridObstaclesWindow();
        gridObstaclesWindow.setSize(400,400);
        return gridObstaclesWindow;
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected GameWindow createGameWindow() {
        GameWindow gameWindow = new GameWindow(gridController);
        gameWindow.setSize(400, 400);
        return gameWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createExitItem());
        menuBar.add(createLookAndFeel());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createExitItem() {
        JMenu fileMenu = new JMenu("Система");
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener((event) -> exitFromApplication());
        fileMenu.add(exitItem);
        return fileMenu;
    }




    private void exitFromApplication() {
        int resultExit = JOptionPane.showConfirmDialog(
                this, "Вы точно хотите выйти?",
                "Потверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (resultExit == JOptionPane.YES_OPTION) {

            if (robotServer != null) {
                robotServer.stop();
                Logger.debug("Сервер остановлен");
            }
            windowsConfigurationManager.saveConfiguration();
            gridController.saveObstacles();
            System.exit(0);
        }
    }

    private JMenu createLookAndFeel() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");
        lookAndFeelMenu.add(createSystemLookAndFeelItem());
        lookAndFeelMenu.add(createCrossPlatformLookAndFeelItem());
        return lookAndFeelMenu;
    }

    private JMenuItem createSystemLookAndFeelItem() {
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeel;
    }

    private JMenuItem createCrossPlatformLookAndFeelItem() {
        JMenuItem crossPlatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossPlatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossPlatformLookAndFeel;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");
        testMenu.add(createExitItem());
        testMenu.add(createAddLogMessageItem());
        return testMenu;
    }

    private JMenuItem createAddLogMessageItem() {
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        return addLogMessageItem;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }
}

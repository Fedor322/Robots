package gui.windows;

import gui.GameVisualizer;
import gui.GridController;
import gui.configuration.StorableWindow;
import gui.configuration.WindowPropertiesManager;
import gui.configuration.WindowState;
import gui.configuration.WindowsRegistry;
import gui.customui.CustomInternalFrameUi;

import javax.swing.*;
import java.awt.BorderLayout;


public class GameWindow extends JInternalFrame implements StorableWindow {
    private final GameVisualizer m_visualizer;
    private final GridController gridController;
    public GameWindow(GridController gridController) {
        super("Поле", true, true, true, true);
        this.gridController = gridController;
        this.m_visualizer = new GameVisualizer(this.gridController);
        setUI(new CustomInternalFrameUi(this));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        setJMenuBar(createMenuBar());
        pack();
        WindowsRegistry.register(this);
    }

    public JMenuBar createMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(createGridMenu());
        return jMenuBar;
    }


    private JMenu createGridMenu() {
        JMenu jMenu = new JMenu("Препятствия");
        JMenuItem loadMenuItem = new JMenuItem("Загрузить");
        loadMenuItem.addActionListener(e -> gridController.loadObstacles());
        jMenu.add(loadMenuItem);
        return jMenu;
    }

    @Override
    public String getId() {
        return "GameWindow";
    }
}

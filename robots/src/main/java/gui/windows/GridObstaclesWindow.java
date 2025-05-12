package gui.windows;

import gui.GridController;
import gui.GridVisualizer;
import gui.configuration.StorableWindow;
import gui.configuration.WindowsRegistry;
import gui.customui.CustomInternalFrameUi;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TimerTask;

public class GridObstaclesWindow extends JInternalFrame implements StorableWindow {

    private final GridController gridController;
    private final GridVisualizer gridVisualizer;
    private final Timer timer = new Timer("timer", true);

    public GridObstaclesWindow() {
        super("Сетка", true, true, true, true);

        this.gridController = new GridController(40, 40);
        this.gridVisualizer = new GridVisualizer(gridController);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                gridController.addObstacle(
                        gridController.getGridCoordinates(
                                e.getPoint().x,
                                e.getPoint().y - 40)
                );
                gridVisualizer.repaint();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gridVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        setUI(new CustomInternalFrameUi(this));
        setJMenuBar(createMenuBar());
        pack();
        WindowsRegistry.register(this);
    }


    protected void onRedrawEvent() {
        EventQueue.invokeLater(gridVisualizer::repaint);
    }

    public JMenuBar createMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(createGridMenu());
        return jMenuBar;
    }

    private JMenu createGridMenu() {
        JMenu jMenu = new JMenu("Препятствия");
        JMenuItem saveMenuItem = new JMenuItem("Сохранить");
        JMenuItem loadMenuItem = new JMenuItem("Загрузить");
        saveMenuItem.addActionListener(e -> gridController.saveObstacles());
        loadMenuItem.addActionListener(e -> gridController.loadObstacles());
        jMenu.add(saveMenuItem);
        jMenu.add(loadMenuItem);
        return jMenu;
    }

    @Override
    public String getId() {
        return "GridWindow";
    }

}

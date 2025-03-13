package gui;

import gui.configuration.StorableWindow;
import gui.customui.CustomInternalFrameUi;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;


public class GameWindow extends JInternalFrame implements StorableWindow {
    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super("Поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        setUI(new CustomInternalFrameUi(this));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public String getId() {
        return "GameWindow";
    }



}

package gui;

import java.awt.BorderLayout;

import javax.swing.*;

public class GameWindow extends JInternalFrame {
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


}

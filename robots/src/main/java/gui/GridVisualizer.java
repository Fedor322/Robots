package gui;

import javax.swing.*;
import java.awt.*;

public class GridVisualizer extends JPanel {

    private final GridController gridController;

    public GridVisualizer(GridController gridController) {
        this.gridController = gridController;
        setDoubleBuffered(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int[][] obstacles = gridController.getObstacles();
        int rows = obstacles.length;
        int cols = obstacles[0].length;
        int cellWidth = gridController.getCellWidth();
        int cellHeight = gridController.getCellHeight();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * (cellHeight);
                int y = row * (cellWidth);
                if (obstacles[row][col] == 1) {
                    g2d.setColor(Color.RED);
                    g2d.fillRect(x, y, cellWidth, cellHeight);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, cellWidth, cellHeight);
                }
            }
        }
    }
}

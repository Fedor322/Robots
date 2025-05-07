package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = initTimer();
    private final GridController gridController;
    private List<Point> currentPath = new ArrayList<>();
    private int currentIndex;
    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }


    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 200;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.05;
    private static final double maxAngularVelocity = 0.01;

    public GameVisualizer(GridController gridController) {
        this.gridController = gridController;
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);
        updatePath();
    }

    protected void setTargetPosition(Point p) {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }



    private void updatePath() {
        Point start = gridController.getGridCoordinates(m_robotPositionX,m_robotPositionY);
        Point target = gridController.getGridCoordinates(m_targetPositionX,m_targetPositionY);
        currentPath = gridController.findPath(start, target);
        for (Point point: currentPath) {
            System.out.println(point);
        }
        currentIndex = 0;
    }


    protected void onModelUpdateEvent() {
        double distanceToEnd = distance(m_targetPositionX, m_targetPositionY,m_robotPositionX,m_robotPositionY);
        if (distanceToEnd < 10.0) {
            currentPath.clear();
            return;
        }
        if (currentPath.isEmpty() || currentIndex >= currentPath.size() ) {
            updatePath();
            if (currentPath.isEmpty()) {
                return;
            }
        }
        Point next = gridController.gridToNormalCoordinates(currentPath.get(currentIndex));
        System.out.println(currentPath.get(currentIndex));
        System.out.println(next);
        double distance = distance(next.x, next.y,
                m_robotPositionX, m_robotPositionY);
        System.out.println(distance);
        if (distance < 10) {
            currentIndex++;
            if (currentIndex >= currentPath.size()) {
                return;
            }
            next = gridController.gridToNormalCoordinates(currentPath.get(currentIndex));
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, next.x, next.y);
        System.out.println(angleToTarget);
        double angularVelocity = 0;
        double angleDifference = asNormalizedRadians(angleToTarget - m_robotDirection);
        if (angleDifference > Math.PI) {
            angleDifference -= 2 * Math.PI;
        }

        if (angleDifference > 0.05) {
            angularVelocity = maxAngularVelocity;
        } else if (angleDifference < -0.05) {
            angularVelocity = -maxAngularVelocity;
        }


        moveRobot(velocity, angularVelocity, 10);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min)
            return min;
        return Math.min(value, max);
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX)) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY)) {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int[][] obstacles = gridController.getObstacles();
        int rows = obstacles.length;
        int cols = obstacles[0].length;
        int cellWidth = getWidth() / cols;
        int cellHeight = getHeight() / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * cellWidth;
                int y = row * cellHeight;
                if (obstacles[row][col] == 1) {
                    g2d.setColor(Color.RED);
                    g2d.fillRect(x, y, cellWidth, cellHeight);
                } else {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(x, y, cellWidth, cellHeight);
                }
            }
        }

        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = round(m_robotPositionX);
        int robotCenterY = round(m_robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}

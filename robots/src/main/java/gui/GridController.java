package gui;

import log.Logger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class GridController {

    private final double cellWidth = 20.0;
    private final double cellHeight = 20.0;

    private final int rows;
    private final int cols;
    private final int[][] obstacles;


    public GridController(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.obstacles = new int[rows][cols];
        for (int[] row : obstacles) {
            Arrays.fill(row, 0);
        }
        loadObstacles();
    }

    public void loadObstacles() {
        File file = new File("matrix.txt");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            int row = 0;
            int currValue;
            while ((line = bufferedReader.readLine()) != null && row < rows) {
                String[] values = line.split(" ");
                for (int col = 0; col < cols; col++) {
                    try {
                        currValue = Integer.parseInt(values[col]);
                        if (currValue == 0 || currValue == 1) {
                            obstacles[row][col] = currValue;
                        } else {
                            System.out.println("Не то число.Либо 1 или 2");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("это не число");
                    }
                }
                row++;
            }
            Logger.debug("Препятствия загружены");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveObstacles() {
        File file = new File("matrix.txt");
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    bufferedWriter.write(String.valueOf(obstacles[row][col]));
                    if (col < cols - 1) {
                        bufferedWriter.write(" ");
                    }
                }
                bufferedWriter.newLine();
            }
            System.out.println("Препятствия сохранены");
        } catch (IOException e) {
            System.out.println("Не удалось сохранить препятствия в файл");
        }
    }

    private List<Point> getPathToGoal(Node node) {
        List<Point> path = new ArrayList<>();
        while (node != null) {
            path.add(node.point);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }


    public List<Point> findPath(Point startPoint, Point endPoint) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set<Point> closeSet = new HashSet<>();
        Map<Point, Node> openMap = new HashMap<>();
        Node start = new Node(startPoint, heuristicDistance(startPoint, endPoint));
        queue.add(start);
        openMap.put(start.point, start);
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            openMap.remove(current.point);
            if (current.point.equals(endPoint)) return getPathToGoal(current);
            closeSet.add(current.point);
            for (Point neighbour : getNeighbours(current.point)) {
                if (closeSet.contains(neighbour)) continue;
                double currG = current.gCost;
                if (Math.abs(current.point.x - neighbour.x) == 1 && Math.abs(current.point.y - neighbour.y) == 1) {
                    currG += 14;
                } else currG += 10;
                Node neighbourNode = openMap.get(neighbour);

                if (neighbourNode == null || currG < neighbourNode.gCost) {
                    if (neighbourNode != null) {
                        queue.remove(neighbourNode);
                    }
                    Node newNode =  new Node(neighbour, current, currG, heuristicDistance(neighbour, endPoint));
                    queue.add(newNode);
                    openMap.put(neighbour, newNode);
                }
            }
        }
        return Collections.emptyList();
    }

    public Point gridToNormalCoordinates(Point point) {

        int normalX = Math.max(0, Math.min(point.x-1,this.cols -1 ));
        int normalY = Math.max(0, Math.min(point.y-1,this.rows -1 ));

        int x = (int) ((normalX) * cellWidth + cellWidth / 2);
        int y = (int) ((normalY) * cellHeight + cellHeight / 2);

        return new Point(x, y);
    }

    public Point getGridCoordinates(double x, double y) {
        int gridX = (int) (x / cellWidth);
        int gridY = (int) (y / cellHeight);

        gridX = Math.max(0, Math.min(gridX,this.cols -1 ));
        gridY = Math.max(0, Math.min(gridY,this.rows -1 ));
        return new Point(gridX +1 , gridY +1 );

    }

    private double heuristicDistance(Point startX, Point endX) {
        int dx = Math.abs(startX.x - endX.x);
        int dy = Math.abs(startX.y - endX.y);

        return 10 * Math.abs(dx - dy) + 14 * Math.min(dx, dy);
    }


    List<Point> getNeighbours(Point point) {
        List<Point> neighbours = new ArrayList<>();
        int[][] directions = {
                {1, 0}, {0, -1}, {-1, 0}, {0, 1},
                {1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
        for (int[] direction : directions) {
            int newCol = point.x + direction[0];
            int newRow = point.y + direction[1];
            if (newRow >= 1 &&
                    newRow <= rows &&
                    newCol >= 1 &&
                    newCol <= cols &&
                    obstacles[newRow-1][newCol-1] == 0
            ) {
                boolean isDiagonal = (direction[0] != 0 && direction[1] != 0);
                if (isDiagonal) {

                    if (obstacles[point.y-1][newCol-1] == 0 &&
                            obstacles[newRow-1][point.x-1] == 0) {
                        neighbours.add(new Point(newCol, newRow));
                    }
                } else {
                    neighbours.add(new Point(newCol, newRow));
                }
            }
        }
        return neighbours;
    }


    public int[][] getObstacles() {
        return obstacles;
    }

}

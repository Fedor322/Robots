package gui;

import java.awt.*;

public class Node implements Comparable<Node> {
    public Point point;
    public Node parent;
    public double gCost;
    public double hCost;
    public double fCost;

    public Node(Point point,Node parent, double gCost, double hCost) {
        this.point = point;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost+ hCost;
    }

    public Node(Point point, double hCost) {
        this(point,null,0,hCost);
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.fCost,other.fCost);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return point.equals(node.point);
    }

    @Override
    public int hashCode() {
        return point.hashCode();
    }

}

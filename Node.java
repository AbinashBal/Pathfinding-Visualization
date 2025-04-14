/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PFVApp;

/**
 *
 * @author abhil
 */
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author abhil
 */
enum NodeType {
    EMPTY, START, END, WALL, PATH, VISITED
}

class Node implements Comparable<Node> {
    int row, col;
    NodeType type = NodeType.EMPTY;

    int gCost = Integer.MAX_VALUE; // Distance from start
    Node previous = null;

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public void draw(Graphics g, int size) {
        switch (type) {
            case START -> g.setColor(Color.GREEN);
            case END -> g.setColor(Color.RED);
            case WALL -> g.setColor(Color.BLACK);
            case PATH -> g.setColor(Color.YELLOW);
            case VISITED -> g.setColor(Color.CYAN);
            default -> g.setColor(Color.WHITE);
        }

        g.fillRect(col * size, row * size, size, size);
        g.setColor(Color.GRAY);
        g.drawRect(col * size, row * size, size, size);
    }

    @Override
    public int compareTo(Node other) {
    return Integer.compare(this.getFCost(), other.getFCost());
    }

    
    int hCost = 0;

    public int getFCost() {
    return gCost + hCost;
    }
    
    

}

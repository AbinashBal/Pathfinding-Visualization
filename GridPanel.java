/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PFVApp;

/**
 *
 * @author abhil
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
class GridPanel extends JPanel implements MouseListener, MouseMotionListener {

    private final int rows, cols;
    private final Node[][] grid;
    private final int cellSize = 25;

    private Node startNode = null;
    private Node endNode = null;

    public GridPanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Node[rows][cols];
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        
        initGrid();

        setFocusable(true);
        requestFocusInWindow();   // make sure it actually gets key events

        addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE -> runDijkstra();
                case KeyEvent.VK_A     -> runAStar();
                case KeyEvent.VK_C     -> clearPath();
                case KeyEvent.VK_R     -> resetGrid();
            }
        }
    });
}

    //Initializes the grid
    private void initGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Node(i, j);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j].draw(g, cellSize);
            }
        }
    }

    // TODO: Implement mouse events to set start, end, and barriers
    // Call this to clear only VISITED and PATH nodes
    public void clearPath() {
        for (Node[] row : grid) {
            for (Node n : row) {
                if (n.type == NodeType.VISITED || n.type == NodeType.PATH) {
                    n.setType(NodeType.EMPTY);
                }
                n.gCost = Integer.MAX_VALUE;
                n.hCost = 0;
                n.previous = null;
            }
        }
        repaint();
    }

    // Call this to clear everything (walls, start, end, paths)
    public void resetGrid() {
        for (Node[] row : grid) {
            for (Node n : row) {
                n.setType(NodeType.EMPTY);
                n.gCost = Integer.MAX_VALUE;
                n.hCost = 0;
                n.previous = null;
            }
        }
        startNode = null;
        endNode = null;
        repaint();
    }
    
    @Override public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
    int row = e.getY() / cellSize;
    int col = e.getX() / cellSize;

    if (row < 0 || row >= rows || col < 0 || col >= cols) {
        return; // Ignore clicks outside the grid
    }

    if (SwingUtilities.isLeftMouseButton(e)) {
        if (startNode == null) {
            startNode = grid[row][col];
            startNode.setType(NodeType.START);
        } else if (endNode == null) {
            endNode = grid[row][col];
            endNode.setType(NodeType.END);
        } else {
            grid[row][col].setType(NodeType.WALL);
        }
    }

    repaint();
}
    private int delay = 30;           // ms between repaint steps

    /** Call this from outside to change animation speed. */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    private void sleep() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void runDijkstra() {
    if (startNode == null || endNode == null) return;

    // Reset costs
    for (Node[] row : grid)
        for (Node n : row) {
            n.gCost = Integer.MAX_VALUE;
            n.previous = null;
            if (n.type == NodeType.VISITED || n.type == NodeType.PATH)
                n.setType(NodeType.EMPTY);
        }

    PriorityQueue<Node> pq = new PriorityQueue<>();
    startNode.gCost = 0;
    pq.add(startNode);

    new Thread(() -> {
        while (!pq.isEmpty()) {
            Node current = pq.poll();

            // As soon as we hit the end, draw path and EXIT
            if (current == endNode) {
                reconstructPath(endNode);
                return;          // <— this returns from run(), killing the thread
            }

            // Otherwise mark visited and continue
            if (current != startNode) {
                current.setType(NodeType.VISITED);
                repaint();      // only repaint when state actually changes
                sleep();
            }

            for (Node nb : getNeighbors(current)) {
                if (nb.type == NodeType.WALL) continue;
                int cost = current.gCost + 1;
                if (cost < nb.gCost) {
                    nb.gCost = cost;
                    nb.previous = current;
                    // refresh in the queue
                    pq.remove(nb);
                    pq.add(nb);
                }
            }
        }
    }).start();
}


    public void runAStar() {
    if (startNode == null || endNode == null) return;

    // Reset everything
    for (Node[] row : grid)
        for (Node n : row) {
            n.gCost = Integer.MAX_VALUE;
            n.hCost = 0;
            n.previous = null;
            if (n.type == NodeType.VISITED || n.type == NodeType.PATH)
                n.setType(NodeType.EMPTY);
        }

    PriorityQueue<Node> open = new PriorityQueue<>();
    startNode.gCost = 0;
    startNode.hCost = heuristic(startNode, endNode);
    open.add(startNode);

    new Thread(() -> {
        while (!open.isEmpty()) {
            Node current = open.poll();

            // Stop immediately when we reach the end
            if (current == endNode) {
                reconstructPath(endNode);
                return;
            }

            // Otherwise mark visited
            if (current != startNode) {
                current.setType(NodeType.VISITED);
                repaint();
                sleep();
            }

            for (Node nb : getNeighbors(current)) {
                if (nb.type == NodeType.WALL) continue;
                int tentativeG = current.gCost + 1;
                if (tentativeG < nb.gCost) {
                    nb.gCost = tentativeG;
                    nb.hCost = heuristic(nb, endNode);
                    nb.previous = current;
                    open.remove(nb);
                    open.add(nb);
                }
            }
        }
    }).start();
}



    //Call this to reconstruct the path After the animation ends.
    private void reconstructPath(Node end) {
    Node current = end.previous;
    while (current != null && current != startNode) {
        current.setType(NodeType.PATH);
        current = current.previous;
        repaint();
        sleep();
    }
}
    //Not used
    private void sleep(int ms) {
    try {
        Thread.sleep(ms);
    } catch (InterruptedException e) {
        System.err.print(e);
    }
}

    private Iterable<Node> getNeighbors(Node node) {
    List<Node> neighbors = new ArrayList<>();
    int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};

    for (int[] dir : directions) {
        int newRow = node.row + dir[0];
        int newCol = node.col + dir[1];

        if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
            neighbors.add(grid[newRow][newCol]);
        }
    }

    return neighbors;
}
   
    private int heuristic(Node a, Node b) {
    return Math.abs(a.row - b.row) + Math.abs(a.col - b.col); // Manhattan distance
}
public void generateMaze() {
        // 1) Fill everything with walls
        for (Node[] row : grid) {
            for (Node n : row) {
                n.setType(NodeType.WALL);
                n.gCost = Integer.MAX_VALUE;
                n.hCost = 0;
                n.previous = null;
            }
        }

        // 2) Carve passages starting from (1,1)
        carveMaze(1, 1);

        // 3) Clear any stray VISITED/PATH states and repaint
        repaint();
    }

    private void carveMaze(int r, int c) {
        grid[r][c].setType(NodeType.EMPTY);

        // 4-direction offsets, two cells at a time
        List<int[]> dirs = Arrays.asList(
            new int[]{ 2,  0},
            new int[]{-2,  0},
            new int[]{ 0,  2},
            new int[]{ 0, -2}
        );
        Collections.shuffle(dirs);

        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];

            // check bounds (leave a 1‑cell border)
            if (nr > 0 && nr < rows - 1 && nc > 0 && nc < cols - 1
                    && grid[nr][nc].type == NodeType.WALL) {

                // knock down the wall between
                grid[r + d[0]/2][c + d[1]/2].setType(NodeType.EMPTY);
                carveMaze(nr, nc);
            }
        }
    }

    

    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

    
}


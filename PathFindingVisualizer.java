/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PFVApp;

/**
 *
 * @author abinash
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class PathFindingVisualizer extends JFrame {

    private final GridPanel gridPanel;

    public PathFindingVisualizer() {
        super("Pathfinding Visualizer – A* & Dijkstra");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 950);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ─── Center: Grid ─────────────────────────────────────────────
        gridPanel = new GridPanel(29, 29); // Odd number to have only one node wall for the border.
       // ensure GridPanel reports a reasonable preferred size:
        gridPanel.setPreferredSize(new Dimension(750, 750));

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.add(gridPanel, new GridBagConstraints());
        add(centerWrapper, BorderLayout.CENTER);

        // ─── North: Controls ───────────────────────────────────────────
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        // 1) Algorithm selector
        JLabel algoLabel = new JLabel("Algorithm:");
        algoLabel.setForeground(Color.red);
        String[] algos = {"Dijkstra", "A*"};
        JComboBox<String> algoCombo = new JComboBox<>(algos);
        algoCombo.setBackground(Color.white);
        algoCombo.setFocusable(false);

        // 2) Speed slider (0 = instant, 200 = slow)
        JLabel speedLabel = new JLabel("Speed (ms): 30");
        JSlider speedSlider = new JSlider(0, 150, 30);
        speedSlider.setMajorTickSpacing(50);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        speedSlider.setForeground(Color.blue);
        speedSlider.setPaintTrack(false);
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int val = speedSlider.getValue();
                speedLabel.setText("Speed (ms): " + val);
                gridPanel.setDelay(val);
            }
        });

        // 3) Single Run button
        JButton runBtn = new JButton("Run");
        runBtn.setBackground(Color.white);
        runBtn.addActionListener(e -> {
            gridPanel.requestFocusInWindow();
            String choice = (String) algoCombo.getSelectedItem();
            if ("A*".equals(choice)) {
                gridPanel.runAStar();
            } else {
                gridPanel.runDijkstra();
            }
        });

        // 4) Clear & Reset buttons (as before)
        JButton clearBtn = new JButton("Clear Path");
        clearBtn.setBackground(Color.white);
        clearBtn.addActionListener(e -> {
            gridPanel.requestFocusInWindow();
            gridPanel.clearPath();
        });
        JButton resetBtn = new JButton("Reset Grid");
        resetBtn.setBackground(Color.white);
        resetBtn.addActionListener(e -> {
            gridPanel.requestFocusInWindow();
            gridPanel.resetGrid();
        });
        JButton mazeBtn = new JButton("Generate Maze");
        mazeBtn.addActionListener(e -> {
            gridPanel.requestFocusInWindow();
            gridPanel.generateMaze();
        });
        controlPanel.add(mazeBtn);


        // Assemble controls
        controlPanel.add(algoLabel);
        controlPanel.add(algoCombo);
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);
        controlPanel.add(runBtn);
        controlPanel.add(clearBtn);
        controlPanel.add(resetBtn);
        //controlPanel.add(mazeBtn);

        add(controlPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PathFindingVisualizer::new);
    }
}

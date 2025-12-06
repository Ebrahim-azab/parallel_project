import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

public class PathfindingGUI extends JFrame {
    private static final int ROWS = 30;
    private static final int COLS = 40;
    private static final int CELL_SIZE = 20;
    
    private int[][] grid = new int[ROWS][COLS];
    private Point start = new Point(2, 2);
    private Point end = new Point(ROWS - 3, COLS - 3);
    private List<Point> currentPath = null;
    
    private GridPanel gridPanel;
    private JRadioButton rbStart, rbEnd, rbWall, rbEraser;
    private JLabel statusLabel;
    private JTextField txtTaskCount;
    private JComboBox<String> cmbAlgo;

    public PathfindingGUI() {
        super("Path Finding");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        clearGrid();

        JPanel controlPanel = new JPanel();
        
        ButtonGroup toolGroup = new ButtonGroup();
        rbStart = new JRadioButton("Set Start");
        rbEnd = new JRadioButton("Set End");
        rbWall = new JRadioButton("Draw Wall", true);
        rbEraser = new JRadioButton("Eraser");
        
        toolGroup.add(rbStart);
        toolGroup.add(rbEnd);
        toolGroup.add(rbWall);
        toolGroup.add(rbEraser);
        
        controlPanel.add(rbStart);
        controlPanel.add(rbEnd);
        controlPanel.add(rbWall);
        controlPanel.add(rbEraser);
        
        JButton btnBFS = new JButton("Run BFS");
        btnBFS.addActionListener(e -> runAlgorithm("BFS"));
        controlPanel.add(btnBFS);
        
        JButton btnDijkstra = new JButton("Run Dijkstra");
        btnDijkstra.addActionListener(e -> runAlgorithm("Dijkstra"));
        controlPanel.add(btnDijkstra);
        
        JButton btnRandom = new JButton("Random Walls");
        btnRandom.addActionListener(e -> randomizeGrid());
        controlPanel.add(btnRandom);
        
        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            clearGrid();
            currentPath = null;
            gridPanel.repaint();
            statusLabel.setText("Grid reset.");
        });
        controlPanel.add(btnReset);

        add(controlPanel, BorderLayout.NORTH);

        gridPanel = new GridPanel();
        add(gridPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JPanel benchmarkPanel = new JPanel();
        benchmarkPanel.setBorder(BorderFactory.createTitledBorder("Parallel Benchmark"));
        benchmarkPanel.add(new JLabel("Tasks:"));
        txtTaskCount = new JTextField("50", 5);
        benchmarkPanel.add(txtTaskCount);
        
        benchmarkPanel.add(new JLabel("Algo:"));
        cmbAlgo = new JComboBox<>(new String[]{"BFS", "Dijkstra"});
        benchmarkPanel.add(cmbAlgo);
        
        JButton btnBenchmark = new JButton("Run Parallel Benchmark");
        btnBenchmark.addActionListener(e -> runBenchmark());
        benchmarkPanel.add(btnBenchmark);
        
        bottomPanel.add(benchmarkPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void clearGrid() {
        for(int i=0; i<ROWS; i++) {
            for(int j=0; j<COLS; j++) {
                grid[i][j] = 0;
            }
        }
    }

    private void randomizeGrid() {
        Random rnd = new Random();
        for(int i=0; i<ROWS; i++) {
            for(int j=0; j<COLS; j++) {
                if ((i == start.x && j == start.y) || (i == end.x && j == end.y)) {
                    grid[i][j] = 0;
                } else {
                    grid[i][j] = rnd.nextDouble() < 0.3 ? 1 : 0;
                }
            }
        }
        currentPath = null;
        gridPanel.repaint();
        statusLabel.setText("Randomized walls.");
    }

    private void runAlgorithm(String algo) {
        statusLabel.setText("Running " + algo + "...");
        currentPath = null;
        gridPanel.repaint();
        
        new Thread(() -> {
            try {
                PathResult result;
                if ("BFS".equals(algo)) {
                    result = PathfindingEngine.bfs(grid, start, end);
                } else {
                    result = PathfindingEngine.dijkstra(grid, start, end);
                }
                
                SwingUtilities.invokeLater(() -> {
                    if (result != null && result.path != null && !result.path.isEmpty()) {
                        currentPath = result.path;
                        statusLabel.setText(String.format("%s finished. Path length: %d. Visited: %d. Time: %.2f ms", 
                                algo, result.path.size(), result.visitedNodes, result.durationNs / 1_000_000.0));
                    } else {
                        statusLabel.setText(algo + " finished. No path found.");
                    }
                    gridPanel.repaint();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Error: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private void runBenchmark() {
        int taskCount;
        try {
            taskCount = Integer.parseInt(txtTaskCount.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid task count.");
            return;
        }

        String selectedAlgo = (String) cmbAlgo.getSelectedItem();
        statusLabel.setText("Running " + selectedAlgo + " benchmark with " + taskCount + " tasks...");
        
        new Thread(() -> {
            try {
                Random rnd = new Random();
                List<PathfindingTask> tasks = new ArrayList<>();
                int tries = 0;
                while (tasks.size() < taskCount && tries < taskCount * 100) {
                    tries++;
                    int sr = rnd.nextInt(ROWS), sc = rnd.nextInt(COLS);
                    int er = rnd.nextInt(ROWS), ec = rnd.nextInt(COLS);
                    if (grid[sr][sc] == 1 || grid[er][ec] == 1) continue;
                    if (sr == er && sc == ec) continue;
                    tasks.add(new PathfindingTask(grid, new Point(sr, sc), new Point(er, ec), selectedAlgo));
                }
                
                if (tasks.size() < taskCount) {
                    final int finalCount = tasks.size();
                    SwingUtilities.invokeLater(() -> statusLabel.setText("Could only generate " + finalCount + " valid tasks (grid too dense?)."));
                }

                long t0 = System.nanoTime();
                for (PathfindingTask t : tasks) {
                    t.call();
                }
                long tSeq = System.nanoTime() - t0;

                PathfindingExecutor exec = new PathfindingExecutor(Runtime.getRuntime().availableProcessors());
                List<Callable<PathResult>> callables = new ArrayList<>(tasks);
                long t1 = System.nanoTime();
                exec.submitAll(callables);
                long tPar = System.nanoTime() - t1;
                exec.shutdownGraceful();

                double seqMs = tSeq / 1_000_000.0;
                double parMs = tPar / 1_000_000.0;
                double speedup = (double) tSeq / tPar;

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText(String.format("Benchmark (%d tasks, %s): Seq=%.2fms, Par=%.2fms, Speedup=%.2fx", 
                            tasks.size(), selectedAlgo, seqMs, parMs, speedup));
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Benchmark Error: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    private class GridPanel extends JPanel {
        public GridPanel() {
            setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleMouse(e);
                }
                @Override
                public void mouseDragged(MouseEvent e) {
                    handleMouse(e);
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        private void handleMouse(MouseEvent e) {
            int c = e.getX() / CELL_SIZE;
            int r = e.getY() / CELL_SIZE;
            
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                if (rbStart.isSelected()) {
                    start = new Point(r, c);
                    grid[r][c] = 0;
                } else if (rbEnd.isSelected()) {
                    end = new Point(r, c);
                    grid[r][c] = 0;
                } else if (rbWall.isSelected()) {
                    if (!isStart(r, c) && !isEnd(r, c)) {
                        grid[r][c] = 1;
                    }
                } else if (rbEraser.isSelected()) {
                    grid[r][c] = 0;
                }
                repaint();
            }
        }

        private boolean isStart(int r, int c) {
            return start.x == r && start.y == c;
        }

        private boolean isEnd(int r, int c) {
            return end.x == r && end.y == c;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    int x = c * CELL_SIZE;
                    int y = r * CELL_SIZE;
                    
                    if (grid[r][c] == 1) {
                        g.setColor(Color.DARK_GRAY);
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                    
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }

            if (currentPath != null) {
                g.setColor(new Color(0, 200, 0, 128));
                for (Point p : currentPath) {
                    g.fillRect(p.y * CELL_SIZE, p.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }

            g.setColor(Color.BLUE);
            g.fillRect(start.y * CELL_SIZE, start.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            
            g.setColor(Color.RED);
            g.fillRect(end.y * CELL_SIZE, end.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }
}

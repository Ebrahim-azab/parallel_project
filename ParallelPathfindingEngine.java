public class ParallelPathfindingEngine {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new PathfindingGUI().setVisible(true);
        });

        // Original Benchmark Code (Commented out)
        /*
        try {
            System.out.println("ParallelPathfindingEngine - robust run");
            final int ROWS = 50, COLS = 50;
            final double WALL_DENSITY = 0.22;
            final long SEED = 42L;
            final int TASKS = 100;
            BenchmarkRunner runner = new BenchmarkRunner(ROWS, COLS, WALL_DENSITY, SEED);

            System.out.println("=== BFS ===");
            BenchmarkResult bfsSeq = runner.runBenchmark(TASKS, "BFS", 1);
            bfsSeq.printReport();
            int cores = Math.max(2, Runtime.getRuntime().availableProcessors());
            BenchmarkResult bfsPar = runner.runBenchmark(TASKS, "BFS", cores);
            bfsPar.printReport();
            System.out.printf("BFS Speedup (seq/par): %.2fx (threads=%d)%n%n", (double)bfsSeq.wallClockNs / bfsPar.wallClockNs, cores);

            System.out.println("=== Dijkstra ===");
            BenchmarkResult dijSeq = runner.runBenchmark(TASKS, "Dijkstra", 1);
            dijSeq.printReport();
            BenchmarkResult dijPar = runner.runBenchmark(TASKS, "Dijkstra", cores);
            dijPar.printReport();
            System.out.printf("Dijkstra Speedup (seq/par): %.2fx (threads=%d)%n%n", (double)dijSeq.wallClockNs / dijPar.wallClockNs, cores);

            System.out.println("Done.");
        } catch (Throwable ex) {
            System.err.println("Fatal exception in main: " + ex.getMessage());
            ex.printStackTrace();
        }
        */
    }
}
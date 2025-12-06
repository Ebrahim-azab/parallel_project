import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class BenchmarkRunner {
    final int rows,cols; final double wallDensity; final long seed;
    public BenchmarkRunner(int rows,int cols,double wallDensity,long seed){ this.rows=rows;this.cols=cols;this.wallDensity=wallDensity;this.seed=seed; }
    int[][] generateGrid(Random rnd){
        int[][] grid = new int[rows][cols];
        for (int i=0;i<rows;i++) for (int j=0;j<cols;j++) grid[i][j] = (rnd.nextDouble() < wallDensity) ? 1 : 0;
        return grid;
    }
    List<PathfindingTask> generateTasks(int N,int[][] grid,String algo,Random rnd){
        List<PathfindingTask> list = new ArrayList<>();
        int tries=0;
        while (list.size()<N && tries < N*20) {
            tries++;
            int sr = rnd.nextInt(rows), sc = rnd.nextInt(cols);
            int er = rnd.nextInt(rows), ec = rnd.nextInt(cols);
            if (grid[sr][sc]==1 || grid[er][ec]==1) continue;
            if (sr==er && sc==ec) continue;
            list.add(new PathfindingTask(grid, new Point(sr,sc), new Point(er,ec), algo));
        }
        return list;
    }
    public BenchmarkResult runBenchmark(int tasksCount, String algorithm, int threadCount) throws InterruptedException {
        Random rnd = new Random(seed);
        int[][] grid = generateGrid(rnd);
        List<PathfindingTask> tasks = generateTasks(tasksCount, grid, algorithm, rnd);
        List<Callable<PathResult>> callables = new ArrayList<>();
        for (PathfindingTask t : tasks) callables.add(t);
        PathfindingExecutor exec = new PathfindingExecutor(threadCount);
        long t0 = System.nanoTime();
        List<PathResult> results = exec.submitAll(callables);
        long t1 = System.nanoTime();
        exec.shutdownGraceful();
        int succeeded=0; long totalVisited=0, totalPathLen=0, sumAlgoNs=0;
        for (PathResult r : results) {
            if (r.path != null && !r.path.isEmpty()) { succeeded++; totalPathLen += r.path.size(); }
            totalVisited += r.visitedNodes;
            sumAlgoNs += r.durationNs;
        }
        return new BenchmarkResult(threadCount, tasks.size(), (t1 - t0), succeeded, totalVisited, totalPathLen, sumAlgoNs);
    }
}

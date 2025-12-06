import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class PathfindingTask implements Callable<PathResult> {
    final int[][] grid; final Point start,end; final String algorithm;
    public PathfindingTask(int[][] grid, Point s, Point e, String algo){ this.grid=grid; this.start=s; this.end=e; this.algorithm=algo; }
    public PathResult call() {
        try {
            if ("Dijkstra".equalsIgnoreCase(algorithm)) return PathfindingEngine.dijkstra(grid, start, end);
            else return PathfindingEngine.bfs(grid, start, end);
        } catch (Throwable ex) {
            System.err.println("Exception inside task.call(): " + ex.getMessage());
            ex.printStackTrace();
            return new PathResult(new ArrayList<Point>(),0,0);
        }
    }
}

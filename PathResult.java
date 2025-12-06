import java.awt.Point;
import java.util.List;

public class PathResult {
    final List<Point> path;
    final int visitedNodes;
    final long durationNs;
    public PathResult(List<Point> p, int v, long d) { path = p; visitedNodes = v; durationNs = d; }
}

import java.awt.Point;
import java.util.*;

public class PathfindingEngine {
    public static PathResult bfs(int[][] grid, Point start, Point end) {
        long t0 = System.nanoTime();
        int rows = grid.length, cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Point[][] parent = new Point[rows][cols];
        ArrayDeque<Point> q = new ArrayDeque<>();
        q.add(new Point(start));
        visited[start.x][start.y] = true;
        int visitedCount = 0;
        Point found = null;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        try {
            while (!q.isEmpty()) {
                Point cur = q.poll();
                visitedCount++;
                if (cur.equals(end)) { found = cur; break; }
                for (int[] d : dirs) {
                    int nr = cur.x + d[0], nc = cur.y + d[1];
                    if (nr<0||nc<0||nr>=rows||nc>=cols) continue;
                    if (visited[nr][nc]) continue;
                    if (grid[nr][nc]==1) continue;
                    visited[nr][nc] = true;
                    parent[nr][nc] = cur;
                    q.add(new Point(nr,nc));
                }
            }
        } catch (Throwable ex) {
            System.err.println("Exception inside BFS: " + ex.getMessage());
            ex.printStackTrace();
        }
        List<Point> path = buildPathFromParent(parent, start, end, found);
        long t1 = System.nanoTime();
        return new PathResult(path, visitedCount, t1 - t0);
    }

    public static PathResult dijkstra(int[][] grid, Point start, Point end) {
        long t0 = System.nanoTime();
        int rows = grid.length, cols = grid[0].length;
        int[][] dist = new int[rows][cols];
        Point[][] parent = new Point[rows][cols];
        boolean[][] seen = new boolean[rows][cols];
        for (int i=0;i<rows;i++) Arrays.fill(dist[i], Integer.MAX_VALUE);
        Comparator<Node> cmp = new Comparator<Node>() {
            public int compare(Node a, Node b) { return Integer.compare(a.g, b.g); }
        };
        PriorityQueue<Node> pq = new PriorityQueue<>(cmp);
        pq.add(new Node(start.x,start.y,0,null));
        dist[start.x][start.y] = 0;
        int visitedCount = 0;
        Point found = null;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        try {
            while (!pq.isEmpty()) {
                Node cur = pq.poll();
                if (seen[cur.r][cur.c]) continue;
                seen[cur.r][cur.c] = true;
                visitedCount++;
                if (cur.r==end.x && cur.c==end.y) { found = new Point(cur.r, cur.c); break; }
                for (int[] d : dirs) {
                    int nr = cur.r + d[0], nc = cur.c + d[1];
                    if (nr<0||nc<0||nr>=rows||nc>=cols) continue;
                    if (grid[nr][nc]==1) continue;
                    int ng = cur.g + 1;
                    if (ng < dist[nr][nc]) {
                        dist[nr][nc] = ng;
                        parent[nr][nc] = new Point(cur.r, cur.c);
                        pq.add(new Node(nr,nc,ng,new Point(cur.r,cur.c)));
                    }
                }
            }
        } catch (Throwable ex) {
            System.err.println("Exception inside Dijkstra: " + ex.getMessage());
            ex.printStackTrace();
        }
        List<Point> path = buildPathFromParent(parent, start, end, found);
        long t1 = System.nanoTime();
        return new PathResult(path, visitedCount, t1 - t0);
    }

    static class Node { int r,c,g; Point parent; Node(int r,int c,int g,Point p){this.r=r;this.c=c;this.g=g;this.parent=p;} }
    private static List<Point> buildPathFromParent(Point[][] parent, Point start, Point end, Point found) {
        List<Point> path = new ArrayList<>();
        if (found==null) return path;
        Point cur = found;
        while (cur!=null && !cur.equals(start)) {
            path.add(cur);
            Point p = parent[cur.x][cur.y];
            if (p==null) break;
            cur = p;
        }
        if (cur!=null && cur.equals(start)) path.add(start);
        Collections.reverse(path);
        return path;
    }
}

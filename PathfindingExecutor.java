import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PathfindingExecutor {
    final ExecutorService pool;
    public PathfindingExecutor(int threads){ pool = Executors.newFixedThreadPool(Math.max(1, threads)); }
    public List<PathResult> submitAll(List<Callable<PathResult>> tasks) throws InterruptedException {
        List<Future<PathResult>> futures;
        try { futures = pool.invokeAll(tasks); }
        catch (InterruptedException ex) { throw ex; }
        List<PathResult> results = new ArrayList<>();
        for (Future<PathResult> f : futures) {
            try { results.add(f.get()); }
        
            catch (ExecutionException ex) {
                System.err.println("ExecutionException in submitAll(): " + ex.getMessage());
                ex.printStackTrace();
                results.add(new PathResult(new ArrayList<>(),0,0));
            } catch (InterruptedException ex) { throw ex; }
        }
        return results;
    }
    public void shutdownGraceful() {
        pool.shutdown();
        try { if (!pool.awaitTermination(2000, TimeUnit.MILLISECONDS)) pool.shutdownNow(); } catch (InterruptedException ignored) { pool.shutdownNow(); }
    }
}

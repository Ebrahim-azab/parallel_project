public class BenchmarkResult {
    final int threads, tasks; final long wallClockNs; final int succeeded; final long totalVisited, totalPathLen, sumAlgoNs;
    public BenchmarkResult(int threads,int tasks,long wallClockNs,int succeeded,long totalVisited,long totalPathLen,long sumAlgoNs){
        this.threads=threads;this.tasks=tasks;this.wallClockNs=wallClockNs;this.succeeded=succeeded;this.totalVisited=totalVisited;this.totalPathLen=totalPathLen;this.sumAlgoNs=sumAlgoNs;
    }
    public void printReport(){
        System.out.println("---- Benchmark (threads=" + threads + ", tasks=" + tasks + ") ----");
        System.out.printf("Wall-clock time: %.3f ms%n", wallClockNs / 1_000_000.0);
        System.out.printf("Tasks succeeded (found path): %d / %d%n", succeeded, tasks);
        System.out.printf("Total visited nodes (sum): %d%n", totalVisited);
        System.out.printf("Total path length sum: %d%n", totalPathLen);
        System.out.printf("Sum of per-task CPU times (algo reported): %.3f ms%n", sumAlgoNs / 1_000_000.0);
        System.out.println();
    }
}

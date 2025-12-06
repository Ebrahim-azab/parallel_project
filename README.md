# parallel_project
# 🧭 Pathfinding Visualization & Parallel Processing Engine  
A Java-based project that visualizes BFS & Dijkstra algorithms in a grid and provides a full parallel pathfinding engine using `ExecutorService` for performance comparison between Sequential and Parallel execution.

---

## 🚀 Features

### 🎨 **1. Pathfinding Visualizer (GUI)**
Implemented in **PathfindingGUI.java**  
The GUI allows you to:
- Choose algorithm: **BFS** or **Dijkstra**
- Set **start** and **end** points on a grid
- Add and remove **walls**
- Visualize the algorithm step-by-step  
- Reset the grid anytime

---

### ⚙️ **2. Pathfinding Engine (Worker Thread)**  
Implemented in **PathfindingTask.java**

- Uses **Callable** (not Runnable) to return the computed path  
- Fully separated from GUI  
- No rendering, only pure computation  
- Supports BFS & Dijkstra  
- Returns the shortest path as a `List<Point>`

This separation follows best practice (Logic vs. Rendering).

---

### 🧵 **3. Parallel vs Sequential Experiment**
Implemented in **PathfindingParallelDemo.java**

This file demonstrates real **Parallel Processing** using:

- `ExecutorService`
- `FixedThreadPool`
- `Future<List<Point>>`

The experiment runs:

1. **100 random pathfinding tasks sequentially**  
   - Using a single thread

2. **100 random pathfinding tasks in parallel**  
   - Using 4 or 8 threads depending on CPU

Then prints the time cost:


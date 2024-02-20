import java.util.*;

class Edge implements Comparable<Edge> {
    int src, dest, weight;

    public Edge(int src, int dest, int weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }
}

class Graph {
    private int V;
    private List<Edge> edges;

    public Graph(int V) {
        this.V = V;  //vertices
        edges = new ArrayList<>();  //store edges
    }

    public void addEdge(int src, int dest, int weight) {
        edges.add(new Edge(src, dest, weight));  //to add edges
    }

    public List<Edge> kruskalMST() {
       //minimim heap initaialized
        PriorityQueue<Edge> pq = new PriorityQueue<>(edges);

        // Initialized the parent array
        int[] parent = new int[V];
        for (int i = 0; i < V; i++) {
            parent[i] = i;
        }

        List<Edge> result = new ArrayList<>();

        // Kruskal's algorithm
        while (!pq.isEmpty() && result.size() < V - 1) {
            Edge edge = pq.poll();
            int srcParent = find(parent, edge.src);
            int destParent = find(parent, edge.dest);

            // if this edgde isnot equal to create a cyxle,then add to result 
            if (srcParent != destParent) {
                result.add(edge);
                union(parent, srcParent, destParent);
            }
        }

        return result;
    }


    //to find a parent of a node
    private int find(int[] parent, int node) {
        if (parent[node] != node) {
            parent[node] = find(parent, parent[node]);
        }
        return parent[node];
    }


   // Method to perform the union operation in the Union-Find data structure(union-combining two disjoint to make a single one)
    private void union(int[] parent, int x, int y) {
        // Find the parent nodes of vertices x and y using the find method
        int xSet = find(parent, x); //find parent vertices of x and y
        int ySet = find(parent, y);
        parent[xSet] = ySet;
    }
}

class KruskalMinimumHeap {
    public static void main(String[] args) {
        int V = 4; //no. of vertices
        Graph graph = new Graph(V);

        //edges are added with src and destination with weight to complete a graph
        graph.addEdge(0, 1, 9);
        graph.addEdge(0, 2, 2);
        graph.addEdge(0, 3, 7);
        graph.addEdge(1, 2, 4);
        graph.addEdge(2, 3, 5);
        

        //calling the kruskal algorithm to find the min spanning tree
        List<Edge> mst = graph.kruskalMST();

        System.out.println("Edges in the Minimum Spanning Tree:");
        for (Edge edge : mst) {
            System.out.println(edge.src + " - " + edge.dest + " : " + edge.weight);
        }
    }
}

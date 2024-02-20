import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class AntColony {
    private double[][] pheromones; // Matrix to store pheromone levels on edges between nodes
    private double[][] distances; // Matrix to store distances between nodes
    private int nAnts; // Number of ants in the colony
    private double decay; // Rate at which pheromones evaporate
    private double alpha; // Parameter controlling the influence of pheromones on edge selection
    private double beta; // Parameter controlling the influence of distances on edge selection

    public AntColony(double[][] distances, int nAnts, double decay, double alpha, double beta) {
        this.distances = distances;
        this.nAnts = nAnts;
        this.decay = decay;
        this.alpha = alpha;
        this.beta = beta;

        int nNodes = distances.length;
        this.pheromones = new double[nNodes][nNodes]; // Initialize pheromone matrix with all values set to 1.0
        for (int i = 0; i < nNodes; i++) {
            Arrays.fill(pheromones[i], 1.0);
        }
    }

    // Method to find the optimal tour using ant colony optimization
    public List<Integer> findOptimalTour() {
        int nNodes = distances.length;
        List<Integer> bestTour = null; // Initialize the best tour
        double bestTourLength = Double.POSITIVE_INFINITY; // Initialize the length of the best tour as positive infinity

        for (int iteration = 0; iteration < 100; iteration++) { // Perform a fixed number of iterations
            List<List<Integer>> antTours = generateAntTours(); // Generate tours for each ant
            updatePheromones(antTours); // Update pheromone levels based on the ant tours

            // Update the best tour if a shorter tour is found
            for (List<Integer> tour : antTours) {
                double tourLength = calculateTourLength(tour);
                if (tourLength < bestTourLength) {
                    bestTourLength = tourLength;
                    bestTour = new ArrayList<>(tour);
                }
            }

            // Decay pheromone levels for all edges
            for (int i = 0; i < nNodes; i++) {
                for (int j = 0; j < nNodes; j++) {
                    pheromones[i][j] *= decay;
                }
            }
        }

        return bestTour; // Return the best tour found
    }

    // Method to generate tours for each ant in the colony
    private List<List<Integer>> generateAntTours() {
        int nNodes = distances.length;
        List<List<Integer>> antTours = new ArrayList<>();

        for (int ant = 0; ant < nAnts; ant++) {
            List<Integer> tour = new ArrayList<>();
            boolean[] visited = new boolean[nNodes];
            int startNode = new Random().nextInt(nNodes); // Choose a random starting node for each ant

            tour.add(startNode); // Add the starting node to the tour
            visited[startNode] = true;

            // Construct the tour by selecting the next node based on probabilities
            for (int step = 1; step < nNodes; step++) {
                int nextNode = selectNextNode(tour, visited);
                tour.add(nextNode);
                visited[nextNode] = true;
            }

            antTours.add(tour); // Add the completed tour for the ant
        }

        return antTours; // Return all generated tours
    }

    // Method to select the next node in the tour based on probabilities
    private int selectNextNode(List<Integer> tour, boolean[] visited) {
        int currentNode = tour.get(tour.size() - 1);
        int nNodes = distances.length;
        double[] probabilities = new double[nNodes];
        double sum = 0;

        // Calculate probabilities for selecting each unvisited neighboring node
        for (int nextNode = 0; nextNode < nNodes; nextNode++) {
            if (!visited[nextNode]) {
                double pheromone = Math.pow(pheromones[currentNode][nextNode], alpha);
                double distance = Math.pow(1.0 / distances[currentNode][nextNode], beta);
                probabilities[nextNode] = pheromone * distance;
                sum += probabilities[nextNode];
            }
        }

        // Perform roulette wheel selection to choose the next node
        double rouletteWheel = new Random().nextDouble() * sum;
        double cumulativeProbability = 0;

        for (int nextNode = 0; nextNode < nNodes; nextNode++) {
            if (!visited[nextNode]) {
                cumulativeProbability += probabilities[nextNode];
                if (cumulativeProbability >= rouletteWheel) {
                    return nextNode;
                }
            }
        }
        return -1; // If no node is selected, return -1
    }

    // Method to update pheromone levels based on the ant tours
    private void updatePheromones(List<List<Integer>> antTours) {
        int nNodes = distances.length;

        // Evaporate pheromones on all edges
        for (int i = 0; i < nNodes; i++) {
            for (int j = 0; j < nNodes; j++) {
                pheromones[i][j] *= (1 - decay);
            }
        }

        // Update pheromones based on the tours taken by each ant
        for (List<Integer> tour : antTours) {
            double tourLength = calculateTourLength(tour);

            for (int i = 0; i < nNodes - 1; i++) {
                int fromNode = tour.get(i);
                int toNode = tour.get(i + 1);
                pheromones[fromNode][toNode] += 1.0 / tourLength;
                pheromones[toNode][fromNode] += 1.0 / tourLength;
            }
        }
    }

    // Method to calculate the length of a tour
    private double calculateTourLength(List<Integer> tour) {
        double length = 0;

        for (int i = 0; i < tour.size() - 1; i++) {
            int fromNode = tour.get(i);
            int toNode = tour.get(i + 1);
            length += distances[fromNode][toNode];
        }

        return length;
    }

    public static void main(String[] args) {
        double[][] distances = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };

        int nAnts = 5;
        double decay = 0.1;
        double alpha = 1;
        double beta = 2;

        AntColony antColony = new AntColony(distances, nAnts, decay, alpha, beta);
        List<Integer> optimalTour = antColony.findOptimalTour();

        System.out.println("Optimal Tour: " + optimalTour);
    }
}

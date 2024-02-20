import java.util.PriorityQueue;

class ScoreTracker {
    // Two heaps to store larger and smaller scores
    private PriorityQueue<Double> minHeap; // Stores the larger half of the scores
    private PriorityQueue<Double> maxHeap; // Stores the smaller half of the scores

    // to initialize the heaps
    public ScoreTracker() {
        minHeap = new PriorityQueue<>();
        maxHeap = new PriorityQueue<>();
    }

    // Method to add a new score to the data stream
    public void addScore(double score) {
        // Adding the score to the appropriate heap based on its value
        if (minHeap.isEmpty() || score <= minHeap.peek()) {
            minHeap.offer(score);
        } else {
            maxHeap.offer(score);
        }

        // Balancing the heaps to ensure the size difference is atmost 1
        balanceHeaps();
    }

    private void balanceHeaps() {
        //If size of minheap is greater than maxheap by more than 1,it will move the top element of minheap to maxheap
        if (minHeap.size() > maxHeap.size() + 1) {
            maxHeap.offer(minHeap.poll());  // poll will remove the top element from minHeap and offer it to maxHeap
        } 
        //If size of maxheap is greater than minheap,it will move the top element of maxheap to minheap
        else if (maxHeap.size() > minHeap.size()) {
            minHeap.offer(maxHeap.poll());  
        }
    }

    public double getMedianScore() {
        // If minHeap is empty no scores are available
        if (minHeap.isEmpty()) {
            throw new IllegalStateException("No scores available");
        }

        // If the sizes of the heaps are equal, return the average of the tops of both heaps
        if (minHeap.size() == maxHeap.size()) {
            return (minHeap.peek() + maxHeap.peek()) / 2;
        } 
        // If the sizes of the heaps are different, return the top of the minHeap
        else {
            return minHeap.peek();
        }
    }

    public static void main(String[] args) {
    
        ScoreTracker scoreTracker = new ScoreTracker();
        
        scoreTracker.addScore(85.5);
        scoreTracker.addScore(92.3);
        scoreTracker.addScore(77.8);
        scoreTracker.addScore(90.1);
        
        double median1 = scoreTracker.getMedianScore();
        System.out.println("Median 1: " + median1);

      
        scoreTracker.addScore(81.2);
        scoreTracker.addScore(88.7);
        
        double median2 = scoreTracker.getMedianScore();
        System.out.println("Median 2: " + median2);
    }
}

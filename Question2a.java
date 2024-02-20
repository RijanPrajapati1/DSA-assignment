class MinMovesToEqualizeDresses {

    public static int minMovesToEqualizeDresses(int[] dresses) {
        int n = dresses.length;

        // Calculate the total number of dresses
        int totalDresses = 0;
        for (int dress : dresses) {
            totalDresses += dress;
        }

        // Check if it's possible to equalize the dresses
        if (totalDresses % n != 0) {
            return -1;
        }

        // Calculate the target number of dresses for each machine
        int targetDresses = totalDresses / n;

        int moves = 0;
        int currentSum = 0;

        // Iterate through the machines and calculate moves
        for (int i = 0; i < n; i++) {
            // Calculate the difference between the current machine's dresses and the target
            int diff = dresses[i] - targetDresses;
            currentSum += diff;

            // Update the moves with the absolute value of the current sum
            moves += Math.abs(currentSum);
        }

        return moves;
    }

    public static void main(String[] args) {
        // Example usage:
        int[] inputDresses = {1, 0, 5};
        int outputMoves = minMovesToEqualizeDresses(inputDresses);
        System.out.println(outputMoves);
    }
}

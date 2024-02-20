//MinimumCostDecoration


public class Question1a {
    public int minCost(int[][] costs) {
        if (costs == null) {
            return 0;
        }

        int venues = costs.length;
        int themes = costs[0].length;

        // Initialize a 2D array to store the minimum cost
        int[][] dp = new int[venues][themes];

        // Copy costs of decorating the first venue to the first row of dp
        System.arraycopy(costs[0], 0, dp[0], 0, themes);

        // Iterate over venues starting from the second venue
        for (int venue = 1; venue < venues; venue++) {
            // Iterate over themes for the current venue
            for (int theme = 0; theme < themes; theme++) {
                // Find the minimum cost for decorating the current venue with the current theme
                int minCost = Integer.MAX_VALUE;  //check the maximum
                for (int prevTheme = 0; prevTheme < themes; prevTheme++) {
                    if (theme != prevTheme) {
                        minCost = Math.min(minCost, dp[venue - 1][prevTheme] + costs[venue][theme]);
                    }
                }
                dp[venue][theme] = minCost;
            }
        }

        // Find the minimum cost of decorating the last venue
        int minCost = Integer.MAX_VALUE;
        int[] lastRow = dp[venues - 1];
        for (int i = 0; i < lastRow.length; i++) {
            int themeCost = lastRow[i];
            minCost = Math.min(minCost, themeCost);
        }

        return minCost; // Return the minimum cost
    }

    public static void main(String[] args) {
        Question1a mincostdecoration = new Question1a();
        int[][] costs = {
                {1, 3, 2},
                {4, 6, 8},
                {3, 1, 5}
        };
        System.out.println("The minimum cost for given input: " + mincostdecoration.minCost(costs)); // Output: 7
    }
}

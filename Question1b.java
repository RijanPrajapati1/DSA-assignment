class EngineBuilder {

    public static int minTimeToBuildEngines(int[] engines, int splitCost) {
        // Calling the initial parameters
        return calculateMinimumTime(engines, 0, engines.length, 1, splitCost);
    }

    //method to recursively calculate the minimum time
    public static int calculateMinimumTime(int[] engines, int start, int end, int engineers, int splitCost) {  //start and end is the index of the first engine in the current range and after last engine
        // No engines left to build    
        if (start >= end) {
            return 0;
        }
        //Only one engine left to build
        if (start + 1 == end) {
            return engines[start];
        }

        // Initializing the minimum time to the maximum possible value
        int minTime = Integer.MAX_VALUE;

        for (int i = start + 1; i <= end; i++) {
            int splitTime = engineers * splitCost;  //splittime calculation through enginer and splitcost
          
            int time = Math.max(maxTime(engines, start, i), calculateMinimumTime(engines, i, end, engineers * 2, splitCost)) + splitTime; //for mintime calculation
            
            minTime = Math.min(minTime, time);  //updates min time 
        }

        return minTime;
    }

    public static int maxTime(int[] engines, int start, int end) {
        // Initializing the maximum time to the minimum possible value
        int max = Integer.MIN_VALUE;
       
        //moves to every to find
        for (int i = start; i < end; i++) {
            // Update the maximum time if a higher value is found
            max = Math.max(max, engines[i]);
        }
        return max; //return max time
    }

    public static void main(String[] args) {
        //input 
        int[] engines = {1, 2, 3};
        int splitCost = 1;
        //min time printed and called
        System.out.println("the minimum time to build engine :" + " " + minTimeToBuildEngines(engines, splitCost)); // Output: 4
    }
}

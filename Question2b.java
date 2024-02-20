import java.util.HashSet;
import java.util.Set;

public class Question2b {
    public static int[] eventuallyKnownSecret(int n, int[][] intervals, int firstPerson) {
        // Using set to initialize a first person
        Set<Integer> known = new HashSet<>();
        known.add(firstPerson);

        // Iterate through each interval
        for (int i = 0; i < intervals.length; i++) {
            int start = intervals[i][0];
            int end = intervals[i][1];
            // .add is used to add the person who recieve secret during interval
            int person = start;
            while (person <= end) {
                known.add(person);
                person++;
            }
        }

        // converting set to array
        int[] result = new int[known.size()];
        int index = 0;
        for (Integer person : known) {   
            result[index++] = person;
        }
        return result;
    }

    public static void main(String[] args) {
        int n = 5;
        int[][] intervals = {{0, 2}, {1, 3}, {2, 4}};
        int firstPerson = 0;
        int[] result = eventuallyKnownSecret(n, intervals, firstPerson);
        for (int person : result) {
            System.out.print(person + " ");
        }
    }
}

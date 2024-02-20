import java.util.LinkedList;
import java.util.List;

class Question4b {
    public static class Node {
        int data;
        Node left, right;

        Node(int data) {
            this.data = data;
            this.left = this.right = null;
        }
    }
    //making bst method 
    Node createBST(Node root, int data) {
      //if null create new
        if (root == null)
            return new Node(data);
        
        // If the data is less than the root's data, insert it in the left subtree
        if (data < root.data) {
            root.left = createBST(root.left, data);
        } 
        // If the data is greater than the root's data, insert it in the right subtree
        else if (data > root.data) {
            root.right = createBST(root.right, data);
        } 
        // If the data is equal to the root's data, print a message for duplicate entry
        else {
            System.out.println("Duplicate entry of " + data);
        }
        return root;
    }

    // to find k closest values target value in a BST
    private void findClosestValues(Node root, double target, int k, LinkedList<Integer> closest) {
        // If the root is null, return
        if (root == null)
            return ;

        //explore the left subtree
        findClosestValues(root.left, target, k, closest);

        
        if (closest.size() == k) {
           // If the distance from the target to the current node is closer than the closest node in the list,
            // we replace the farthest closest node in the list with the current node.
            // Otherwise, we don't make any changes to the list.
            if (Math.abs(target - closest.peekFirst()) > Math.abs(target - root.data)) {
                closest.removeFirst();
            } else {
                return;
            }
        }
        // Add the current node's data to the closest list
        closest.add(root.data);

        //explore the right subtree
        findClosestValues(root.right, target, k, closest);
    }

    // to find k closest values to a target value in a BST
    public List<Integer> findClosest(Node root, double target, int k) {
        LinkedList<Integer> closest = new LinkedList<>();
        findClosestValues(root, target, k, closest);
        return closest;
    }

    public static void main(String[] args) {    
        Question4b tree = new Question4b();
        Node root = null;

        //inserted value in bst
        int[] values = { 4, 2, 5, 1, 3 };

        // Creating the BST of values inputed
        for (int value : values) {
            root = tree.createBST(root, value);
        }

        double target = 3.8;
        int k = 2;

        // Finding k closest values to the target in the BST
        List<Integer> closestValues = tree.findClosest(root, target, k);
        System.out.println(closestValues);
    }
}

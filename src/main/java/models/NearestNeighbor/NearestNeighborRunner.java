package models.NearestNeighbor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NearestNeighborRunner {

    public static void classifyData(ArrayList<ObjectNN> neighbors, int k){
        // Go through each node
        for (ObjectNN vertex : neighbors) { // O(n)
            ObjectNN.focusedNode = vertex;
            ArrayList<ObjectNN> listToOrder = new ArrayList<>(neighbors);
            Collections.sort(listToOrder);

            // Get neighbors within distance
            HashMap<String, Integer> map = new HashMap<>();
            for(int i=1; i < k+1; i++){ // ignore first since its self, and find k total
                map.merge(listToOrder.get(i).classification, 1, Integer::sum);
            }

            int max = Collections.max(map.values());
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == max) {
                    ObjectNN.focusedNode.classification = entry.getKey(); // Ignores ties
                    break;
                }
            }

            System.out.println("The class of the unknown object is: " + ObjectNN.focusedNode.classification);
            System.out.println("xVAl " + ObjectNN.focusedNode.vector.x + "yVAl " + ObjectNN.focusedNode.vector.y);
        }
    }
}

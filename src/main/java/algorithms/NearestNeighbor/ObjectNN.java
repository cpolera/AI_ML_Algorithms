package algorithms.NearestNeighbor;

import common.Vector;

/**
 * Object that will be classified
 *
 * Comparable is so that Collection.sort() will work to sort by distances from target
 */
public class ObjectNN {

    public static int idIncr = 0;

    public Vector vector;
    public String classification;
    public double distanceFromTarget;
    public int id;

    public ObjectNN(double x, double y, String classification) {
        this.id = idIncr;
        idIncr++;
        vector = new Vector(x, y);
        this.classification = classification;
    }

    public double calcDistance(ObjectNN other) {
        double deltaX = vector.y - other.vector.y;
        double deltaY = vector.x - other.vector.x;
        this.distanceFromTarget = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return distanceFromTarget;
    }
}

package algorithms.NearestNeighbor;

import common.Vector;

/**
 * Object that will be classified
 *
 * Comparable is so that Collection.sort() will work to sort by distances from target
 */
public class ObjectNN implements Comparable {

    public Vector vector;
    public String classPlaceholder;
    public double distanceFromTarget;

    public ObjectNN(double x, double y, String classPlaceholder) {
        vector = new Vector(x, y);
        this.classPlaceholder = classPlaceholder;
    }

    public double calcDistance(ObjectNN other) {
        double deltaX = vector.y - other.vector.y;
        double deltaY = vector.x - other.vector.x;
        this.distanceFromTarget = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return distanceFromTarget;
    }

    public int comparison(ObjectNN objNN) {
        return Double.compare(this.distanceFromTarget, objNN.distanceFromTarget);
    }

    @Override
    public int compareTo(Object o) {
        return comparison((ObjectNN) o);
    }
}

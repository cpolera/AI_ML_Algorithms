package models.NearestNeighbor;

import common.Vector;

/**
 * Object that will be classified
 *
 * Comparable is so that Collection.sort() will work to sort by distances from target
 */
public class ObjectNN implements Comparable {

    public static int idIncr = 0;
    public static ObjectNN focusedNode;

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

    public ObjectNN(Vector vector, String classification) {
        this.id = idIncr;
        idIncr++;
        this.vector = vector;
        this.classification = classification;
    }

    public double calcDistance(ObjectNN other) {
        double deltaX = vector.y - other.vector.y;
        double deltaY = vector.x - other.vector.x;
        this.distanceFromTarget = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return distanceFromTarget;
    }

    public int comparison(ObjectNN objNN) {
        return Double.compare(calcDistance(focusedNode), objNN.calcDistance(focusedNode));
//        return Double.compare(this.distanceFromTarget, objNN.distanceFromTarget);
    }

    @Override
    public int compareTo(Object o) {
        return comparison((ObjectNN) o);
    }
}

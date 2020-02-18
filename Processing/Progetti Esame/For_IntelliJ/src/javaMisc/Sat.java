package javaMisc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sat {
    static class Vector {
        double x, y;

        Vector(double x, double y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Returns a new vector which is orthogonal to the current vector
         */
        Vector orthogonal() {
            return new Vector(y, -x);
        }
    }

    public static Boolean hasCollided(Vector[] poly1, Vector[] poly2, Double maxDist) {
        // Do an optimization check using the maxDist
        if (maxDist != null) {
            if (Math.pow(poly1[1].x - poly2[0].x, 2) + Math.pow(poly1[1].y - poly2[0].y, 2) <= Math.pow(maxDist, 2)) {
                // Collision is possible so run SAT on the polys
                return runSAT(poly1, poly2);
            } else {
                return false;
            }
        } else {
            // No maxDist so run SAT on the polys
            return runSAT(poly1, poly2);
        }
    }

    private static Boolean runSAT(Vector[] poly1, Vector[] poly2) {
        // Implements the actual SAT algorithm
        ArrayList<Vector> edges = polyToEdges(poly1);
        edges.addAll(polyToEdges(poly2));
        Vector[] axes = new Vector[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            axes[i] = edges.get(i).orthogonal();
        }

        for (Vector axis : axes) {
            if (!overlap(project(poly1, axis), project(poly2, axis))) {
                // The polys don't overlap on this axis so they can't be touching
                return false;
            }
        }

        // The polys overlap on all axes so they must be touching
        return true;
    }

    /**
     * Returns a vector going from point1 to point2
     */
    private static Vector edgeVector(Vector point1, Vector point2) {
        return new Vector(point2.x - point1.x, point2.y - point1.y);
    }

    /**
     * Returns an array of the edges of the poly as vectors
     */
    private static ArrayList<Vector> polyToEdges(Vector[] poly) {
        ArrayList<Vector> vectors = new ArrayList<>(poly.length);
        for (int i = 0; i < poly.length; i++) {
            vectors.add(edgeVector(poly[i], poly[(i + 1) % poly.length]));
        }
        return vectors;
    }

    /**
     * Returns the dot (or scalar) product of the two vectors
     */
    private static double dotProduct(Vector vector1, Vector vector2) {
        return vector1.x * vector2.x + vector1.y * vector2.y;
    }

    /**
     * Returns a vector showing how much of the poly lies along the axis
     */
    private static Vector project(Vector[] poly, Vector axis) {
        List<Double> dots = new ArrayList<>();
        for (Vector vector : poly) {
            dots.add(dotProduct(vector, axis));
        }
        return new Vector(Collections.min(dots), Collections.max(dots));
    }

    /**
     * Returns a boolean indicating if the two projections overlap
     */
    private static boolean overlap(Vector projection1, Vector projection2) {
        return projection1.x <= projection2.y &&
                projection2.x <= projection1.y;
    }
}
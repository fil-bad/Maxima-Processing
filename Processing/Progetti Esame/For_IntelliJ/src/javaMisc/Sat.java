package javaMisc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Sat {

    public static Boolean haveCollided(Polygon poly1, Polygon poly2) {

        Vertex[] vert_poly1 = poly1.getVertices(); // this two lines were added due to compatibility reasons
        Vertex[] vert_poly2 = poly2.getVertices();
        // Do an optimization check using the maxDist
        // No maxDist so run SAT on the polys (we deleted this "optimization", we preferred to run every case)
        return runSAT(vert_poly1, vert_poly2);
    }

    private static Boolean runSAT(Vertex[] poly1, Vertex[] poly2) {
        // Implements the actual SAT algorithm
        ArrayList<Vertex> edges = polyToEdges(poly1);
        edges.addAll(polyToEdges(poly2));
        Vertex[] axes = new Vertex[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            axes[i] = edges.get(i).orthogonal();
        }

        for (Vertex axis : axes) {
            if (!overlap(project(poly1, axis), project(poly2, axis))) {
                // The polys don't overlap on this axis so they can't be touching
                return false;
            }
        }
        // todo: perhaps here we have to insert the containment part
        // The polys overlap on all axes so they must be touching
        return true;
    }

    /**
     * Returns a vector going from point1 to point2
     */
    private static Vertex edgeVector(Vertex point1, Vertex point2) {
        return new Vertex(point2.getX() - point1.getX(), point2.getY() - point1.getY());
    }

    /**
     * Returns an array of the edges of the poly as vectors
     */
    private static ArrayList<Vertex> polyToEdges(Vertex[] poly) {
        ArrayList<Vertex> vertices = new ArrayList<>(poly.length);
        for (int i = 0; i < poly.length; i++) {
            vertices.add(edgeVector(poly[i], poly[(i + 1) % poly.length]));
        }
        return vertices;
    }

    /**
     * Returns the dot (or scalar) product of the two vectors
     */
    private static double dotProduct(Vertex vertex1, Vertex vertex2) {
        return vertex1.getX() * vertex2.getX() + vertex1.getY() * vertex2.getY();
    }

    /**
     * Returns a vector showing how much of the poly lies along the axis
     */
    private static Vertex project(Vertex[] poly, Vertex axis) {
        List<Double> dots = new ArrayList<>();
        for (Vertex vertex : poly) {
            dots.add(dotProduct(vertex, axis));
        }
        return new Vertex(Collections.min(dots), Collections.max(dots));
    }

    /**
     * Returns a boolean indicating if the two projections overlap
     */
    private static boolean overlap(Vertex projection1, Vertex projection2) {
        return projection1.getX() <= projection2.getY() &&
                projection2.getX() <= projection1.getY();
    }

    public static void main(String[] args) {
        // creating two polygons
        Polygon a = new Polygon(new Vertex(10,10), new Vertex(10,100), new Vertex(100,100),new Vertex(100,10));
        Polygon b = new Polygon(new Vertex(20,20), new Vertex(20,120), new Vertex(120,120),new Vertex(120,20));
        // they are two squares which overlaps
        a.printVertices();
        b.printVertices();
        System.out.println("Do a & b collide? " + haveCollided(a,b));
        // now the entire polygon c is contained in a
        Polygon c = new Polygon(new Vertex(30,30), new Vertex(30,90), new Vertex(90,90),new Vertex(90,30));
        c.printVertices();
        System.out.println("Do a & c collide? " + haveCollided(a,c));
    }

}
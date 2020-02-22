package graph;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import quadtree.QuadTree;

public class QTGraph {

    private SimpleWeightedGraph<QuadTree, DefaultEdge> qtGraph;

    public QTGraph() {
        this.qtGraph = new SimpleWeightedGraph<>(DefaultEdge.class);
    }

}

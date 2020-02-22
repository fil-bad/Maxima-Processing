package graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import quadtree.QuadTree;

import java.util.Stack;

public class QTGraph {

    private SimpleWeightedGraph<QuadTree, DefaultWeightedEdge> qtGraph;

    public QTGraph(Stack<QuadTree> quadTreeStack) {

        this.qtGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);


    }

    public void printNodes() {
    }

}

package graph;

import com.sun.javafx.geom.Edge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import quadtree.Boundary;
import quadtree.QuadTree;
import quadtree.Side;


import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class QTGraph {

    private SimpleWeightedGraph<QuadTree, DefaultWeightedEdge> qtGraph;

    public QTGraph(Stack<QuadTree> quadTreeStack) {

        this.qtGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        // genero i vertici da collegare
        for (int i = quadTreeStack.size() - 1; i != -1; i--) {
            QuadTree node = quadTreeStack.get(i);
            // System.out.println(node.dataNode());
            this.qtGraph.addVertex(node);
        } // todo: vedere qui, lo stack non è inverso e non si fa tutti e 4 i nodi prima di passare al successivo
        //collega i vertici secondo adiacenza
        QuadTree node = null;
        for (int i = quadTreeStack.size() - 1; i != -1; i--) {
            QuadTree n = quadTreeStack.get(i);
            if (!n.isFreeSpace()) {
                break;
            }
            for (Side s : Side.values()) {
                node = n.FSMneighbors(s);
                if (node == null) {
                    break;
                }
                if (node.isFreeSpace()) {
                    System.out.print(node.dataNode() + "\t");
                    System.out.println(n.dataNode());
                    DefaultWeightedEdge e = this.qtGraph.addEdge(n, node);
                    this.qtGraph.setEdgeWeight(e, 1); //todo: distanza dai centri dei nodi
                }
            }

        }
    }

    public static void main(String[] args) {
        QuadTree qt = new QuadTree(new Boundary(-100, -100, 100, 100));
        qt.split();
        qt.getNode('1').split();
        qt.getNode('2').split();
        qt.getNode('2').getNode('1').split();
        qt.getNode('3').split();
        qt.getNode('3').getNode('0').split();
        qt.getNode('3').getNode('2').split();

        Stack<QuadTree> qtStack = QuadTree.qt2leaves(qt);

        QTGraph graph = new QTGraph(qtStack);
        //graph.printNodes();

        graph.printEdges();


    }

    public void printNodes() {
        Iterator<QuadTree> iterator = new DepthFirstIterator<>(this.qtGraph);
        QuadTree node = null;
        System.out.println("### PRINTING VERTICES OF GRAPH ###");
        while (iterator.hasNext()) {
            node = iterator.next();
            System.out.println(node.dataNode());
        }
    }

    public void printEdges() {

        Set<DefaultWeightedEdge> edgeSet = this.qtGraph.edgeSet();


        Iterator<DefaultWeightedEdge> iterator = edgeSet.iterator();
        DefaultWeightedEdge edge = null;
        System.out.println("### PRINTING EDGES OF GRAPH ###");
        while (iterator.hasNext()) {
            edge = iterator.next();
            System.out.println(edge.toString());
        }
    }

    private void addPath() {
        //todo: dovrà fare l'aggiunta di percorsi possibili secondo logica, usando addEdges()
    }
}

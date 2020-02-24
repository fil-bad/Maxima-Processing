package graph;

import com.sun.javafx.geom.Edge;
import geometry.Vertex;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import processing.core.PApplet;
import quadtree.Boundary;
import quadtree.QuadTree;
import quadtree.Side;


import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class QTGraph {

    private SimpleWeightedGraph<QuadTree, DefaultWeightedEdge> qtGraph;
    private QuadTree root;
    private DijkstraShortestPath<QuadTree, DefaultWeightedEdge> path = null;

    public QTGraph(QuadTree qt) {
        this(QuadTree.qt2leaves(qt));
    }

    public QTGraph(Stack<QuadTree> qtStack) {

        QuadTree tallest = qtStack.get(0);
        while (!tallest.isRoot())
            tallest = tallest.getDad();
        this.root = tallest;

        this.qtGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        // genero i vertici da collegare

        for (int i = qtStack.size() - 1; i != -1; i--) {
            QuadTree node = qtStack.get(i);
            // System.out.println(node.dataNode());
            this.qtGraph.addVertex(node);
        }
        //collega i vertici secondo adiacenza

        QuadTree node = null;
        while (!qtStack.isEmpty()) {
            QuadTree n = qtStack.pop();
            if (!n.isFreeSpace()) {
                break;
            }

            for (Side s : Side.values()) {
                node = n.FSMneighbors(s);
                if (node == null) {
                    continue;
                }
                if (node.isFreeSpace()) {
                    DefaultWeightedEdge e = this.qtGraph.addEdge(n, node);
                    if (e != null) {// arco non ancora esistente

                        double weight = Math.sqrt(n.getBoundary().getX() * node.getBoundary().getX() +
                                node.getBoundary().getY() * node.getBoundary().getY());
                        this.qtGraph.setEdgeWeight(e, weight); //todo: distanza dai centri dei nodi
                    }
                }
            }
        }

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
        DefaultWeightedEdge edge;
        System.out.println("### PRINTING EDGES OF GRAPH ###");
        while (iterator.hasNext()) {
            edge = iterator.next();
            System.out.println(edge.toString());
            System.out.println("##[SRC]##\t" + qtGraph.getEdgeSource(edge).dataNode());
            System.out.println("##[TAR]##\t" + qtGraph.getEdgeTarget(edge).dataNode() + "\n");
        }
    }

    public void printGraph(PApplet win, float r) {
        win.pushStyle();
        Set<DefaultWeightedEdge> edgeSet = this.qtGraph.edgeSet();

        Iterator<DefaultWeightedEdge> edgeIterator = edgeSet.iterator();
        DefaultWeightedEdge edge;
        //### PRINTING EDGES OF GRAPH ###
        win.strokeWeight(r / 8);
        win.stroke(0, 125, 175);
        Boundary src, tg;
        while (edgeIterator.hasNext()) {
            edge = edgeIterator.next();
            src = qtGraph.getEdgeSource(edge).getBoundary();
            tg = qtGraph.getEdgeTarget(edge).getBoundary();
            win.line((float) src.getX(), (float) src.getY(), (float) tg.getX(), (float) tg.getY());
        }

        Iterator<QuadTree> vertexIterator = new DepthFirstIterator<>(this.qtGraph);
        QuadTree node;
        //### PRINTING VERTICES OF GRAPH ###
        win.fill(255);
        win.stroke(0);
        win.strokeWeight(1);
        win.pushMatrix();
        win.translate(0, 0, 1);
        while (vertexIterator.hasNext()) {
            node = vertexIterator.next();
            win.circle((float) node.getBoundary().getX(), (float) node.getBoundary().getY(), (float) Math.min(r, node.getBoundary().getMinExtension()));
        }
        win.popMatrix();
        win.popStyle();
    }

    public void printNodeEdges(QuadTree n) {
        Set<DefaultWeightedEdge> edgeSet = this.qtGraph.edgesOf(n);

        Iterator<DefaultWeightedEdge> iterator = edgeSet.iterator();
        DefaultWeightedEdge edge;
        System.out.println("### PRINTING EDGES OF Node ### (" + edgeSet.size() + ")");
        System.out.println(n.dataNode());
        while (iterator.hasNext()) {
            edge = iterator.next();
            System.out.println(edge.toString());
            System.out.println("##[SRC]##\t" + qtGraph.getEdgeSource(edge).dataNode());
            System.out.println("##[TAR]##\t" + qtGraph.getEdgeTarget(edge).dataNode() + "\n");
        }
    }

    private void findPath(Vertex start, Vertex end) {


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

        graph.printNodeEdges(qt.nearestPoint(-50, 50));
        //graph.printNodes();

        //graph.printEdges();


    }

}

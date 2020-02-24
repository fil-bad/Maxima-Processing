package graph;

import com.sun.javafx.geom.Edge;
import geometry.Vertex;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import processing.core.PApplet;
import processingElement.Obstacle;
import quadtree.Boundary;
import quadtree.QuadTree;
import quadtree.Side;


import java.util.*;

public class QTGraph {

    private SimpleWeightedGraph<QuadTree, DefaultWeightedEdge> qtGraph;
    private QuadTree root;
    private DijkstraShortestPath<QuadTree, DefaultWeightedEdge> path = null;
    private ArrayList<Vertex> node2visit = null;

    public QTGraph(QuadTree qt, float rRobot, Obstacle[] obs) {
        this(QuadTree.qt2leaves(qt), rRobot, obs);
    }

    protected QTGraph(Stack<QuadTree> qtStack, float rRobot, Obstacle[] obs) {
        // ### CREO IL GRAFO ###
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
                    //Todo: se un collegamento diagonale, verifico che nel raggio rRobot, non ci siano ostacoli
                    DefaultWeightedEdge e = this.qtGraph.addEdge(n, node);
                    if (e != null) {// arco non ancora esistente

                        double weight = Math.sqrt(n.getBoundary().getX() * node.getBoundary().getX() +
                                node.getBoundary().getY() * node.getBoundary().getY());
                        this.qtGraph.setEdgeWeight(e, weight); //todo: distanza dai centri dei nodi
                    }
                }
            }
        }
        // ### CREO LA STRUTTRA PER I PATH ###
        this.path = new DijkstraShortestPath<>(this.qtGraph);

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

        QTGraph graph = new QTGraph(qtStack, 10, null);

        graph.printNodeEdges(qt.nearestPoint(-50, 50));
        //graph.printNodes();

        //graph.printEdges();


    }

    public void printPath(PApplet win, float r) {
// todo: fare il print e il calcolo di dijkstra DENTRO il print del grafo, per non farlo crashare
        if (this.node2visit == null) calcVert2Visit(new Vertex(-300, 20), new Vertex(350, 150));
        //todo: sistemare il caso iniziale dove non è dichiarato
        win.pushStyle();
        win.strokeWeight(r);
        win.stroke(200, 0, 0);

        ArrayList<Vertex> vertexSet = this.node2visit;
        Vertex src, tg;
        for (int i = 0; i < vertexSet.size() - 1; i++) {
            src = vertexSet.get(i);
            tg = vertexSet.get(i + 1);
            win.line((float) src.getX(), (float) src.getY(), (float) tg.getX(), (float) tg.getY());
        }
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

    private GraphPath<QuadTree, DefaultWeightedEdge> findPath(Vertex start, Vertex end) {
        return path.getPath(root.nearestPoint(start), root.nearestPoint(end));
    }

    public void calcVert2Visit(Vertex start, Vertex end) {
        ArrayList<Vertex> list = new ArrayList<Vertex>(0);
        list.add(start);

        GraphPath<QuadTree, DefaultWeightedEdge> graphPath = this.findPath(start, end);
        List<QuadTree> vert_list = graphPath.getVertexList();

        for (QuadTree q : vert_list) {
            list.add(q.getBoundary().getVertex());
        }
        list.add(end);

        this.node2visit = list;
    }

}

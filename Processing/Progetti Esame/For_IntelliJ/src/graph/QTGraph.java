package graph;

import com.sun.javafx.geom.Edge;
import geometry.Polygon;
import geometry.Sat;
import geometry.Vertex;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import processing.core.PApplet;
import processingElement.Box;
import processingElement.Obstacle;
import quadtree.Boundary;
import quadtree.QuadTree;
import quadtree.Side;


import java.util.*;

public class QTGraph {

    private SimpleWeightedGraph<QuadTree, DefaultWeightedEdge> qtGraph;
    private QuadTree root;
    // private DijkstraShortestPath<QuadTree, DefaultWeightedEdge> path;
    private Vector<Vertex> node2visit = null;

    public QTGraph(QuadTree qt, float rRobot, Obstacle[] obs) {
        this(null, QuadTree.qt2leaves(qt), rRobot, obs);
    }

    public QTGraph(PApplet win, QuadTree qt, float rRobot, Obstacle[] obs) {
        this(win, QuadTree.qt2leaves(qt), rRobot, obs);
    }

    public QTGraph(QuadTree qt) {
        this(null, QuadTree.qt2leaves(qt), 0, null);
    }

    protected QTGraph(PApplet win, Stack<QuadTree> qtStack, float rRobot, Obstacle[] obs) {
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
                    DefaultWeightedEdge e = null;
                    boolean add = true;
                    if (obs != null) {
                        switch (s) {
                            case U:
                            case D:
                            case R:
                            case L:
                                add = true;
                                break;
                            case LD:
                            case LU:
                            case RD:
                            case RU:
                                for (Obstacle ob : obs) {
                                    Vertex mid = middleBoundary(n, node);
                                    if (mid != null) {
                                        if (win != null) {
                                            win.fill(255, 255, 0);
                                            win.circle((float) mid.getX(), (float) mid.getY(), 5);
                                        }
                                        if (Sat.haveCollided(ob.getPoly(), mid, rRobot)) {
                                            if (win != null) {
                                                win.fill(255, 0, 0, 150);
                                                win.circle((float) mid.getX(), (float) mid.getY(), rRobot * 2);
                                            }
                                            add = false;
                                            break;
                                        }
                                    }
                                }
                                break;
                            case HALT:
                                continue;
                        }
                    }
                    if (add)
                        e = this.qtGraph.addEdge(n, node);
                    if (e == null)
                        continue;// arco già esistente
                    double weight = n.getBoundary().getVertex().dist(node.getBoundary().getVertex());
                    this.qtGraph.setEdgeWeight(e, weight);
                }
            }
        }
        // ### CREO LA STRUTTRA PER I PATH ###
        // this.path = new DijkstraShortestPath<>(this.qtGraph);

    }

    public Vertex middleBoundary(QuadTree n1, QuadTree n2) {
        Side sideBigFromSmall;
        sideBigFromSmall = n1.neighborsSide(n2);
        if (sideBigFromSmall == null)
            return null;
        Boundary big, small;
        if (n1.getBoundary().getMinExtension() >= n2.getBoundary().getMinExtension()) {
            small = n2.getBoundary();
            sideBigFromSmall = n2.neighborsSide(n1);    // DEVO cambiare la variabile del side
        } else {
            small = n1.getBoundary();
        }
        return small.getVertex(sideBigFromSmall);
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
            printEdge(edge);
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
        QTGraph graph = new QTGraph(qt);
        graph.printNodeEdges(qt.nearestPoint(-50, 50));

//
//        Box ob1 = new Box(2, 3, 10);
//        Box ob2 = new Box(1, 5, 10);
//        ob2.setD(3, 2, 0);
//        Obstacle[] ob = {ob1, ob2};
//
//        QuadTree qtAuto = new QuadTree(ob, new Boundary(-5, -5, 5, 5), 0.5f);
//        qtAuto.printTree();
//
//        QTGraph graphAuto = new QTGraph(qtAuto, 40, ob);
//
//        graphAuto.printNodes();
//        graphAuto.printEdges();

    }

    public void printEdge(DefaultWeightedEdge edge) {
        System.out.println(edge.toString());
        System.out.println("##[SRC]##\t" + qtGraph.getEdgeSource(edge).dataNode());
        System.out.println("##[TAR]##\t" + qtGraph.getEdgeTarget(edge).dataNode() + "\n");
    }

    public void printPath(PApplet win, float r) {

        win.pushStyle();
        win.strokeWeight(r / 2);
        win.stroke(130, 0, 0, 200);

        if (this.node2visit == null) {
            win.popStyle();
            System.out.println("Nothing to print");
            return;
        }

        for (Vertex v : this.node2visit) {
            v.printVertex();
        }
        System.out.println("");

        Vertex src, tg;
        for (int i = 0; i < this.node2visit.size() - 1; i++) {
            src = this.node2visit.get(i);
            tg = this.node2visit.get(i + 1);
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
        try {
            QuadTree s = root.nearestPoint(start);
            QuadTree e = root.nearestPoint(end);

            if (!s.isFreeSpace() || !e.isFreeSpace())
                return null;
            return DijkstraShortestPath.findPathBetween(this.qtGraph, s, e);

        } catch (RuntimeException e) {

            return null;
        }
    }

    public void calcVert2Visit(Vertex start, Vertex end) {
        this.node2visit = new Vector<>(0);
        this.node2visit.add(start);

        GraphPath<QuadTree, DefaultWeightedEdge> graphPath = this.findPath(start, end);
        if (graphPath == null) {
            System.err.println("### Cammino non esistente, uno dei due punti è su un ostacolo ###");
            return;
        }

        List<QuadTree> vert_list = graphPath.getVertexList();

        for (QuadTree q : vert_list) {
            this.node2visit.add(q.getBoundary().getVertex());
        }
        this.node2visit.add(end);
        for (Vertex v : this.node2visit) {
            v.printVertex();
        }
        System.out.println("");
    }

}

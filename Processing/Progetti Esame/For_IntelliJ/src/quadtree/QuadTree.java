package quadtree;

import processing.core.PApplet;

import static quadtree.Coord.*;
import static quadtree.Side.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import geometry.*;
import processingElement.Obstacle;


// The method to find neighbor are describe here:
//http://web.archive.org/web/20120907211934/http://ww1.ucmss.com/books/LFS/CSREA2006/MSV4517.pdf
// or in Doc directory of the project
/*                           _______
 *  			N           | 0 | 1 |
 *  		W		E       |---|---| cell order
 *  			S           | 2 | 3 |
 */

/**
 * Ogni classe QuadTree è essa stessa un nodo, se
 * QuadTree northWest = null;
 * QuadTree northEast = null;
 * QuadTree southWest = null;
 * QuadTree southEast = null;
 * Allora è una foglia, altrimenti è uno splitPoint
 */
public class QuadTree {
    //Global tree define
    /**
     * WARNING: non sovrascrivere i metodi hash() e toString() (necessari per far funzionare il grafo)
     */

    private final int MAX_CAPACITY = 4;

    /**
     * Tree structure variable
     **/
    private int level = 0;
    String myCode = "";
    private QuadTree dad = null;

    private QuadTree northWest = null;      //0
    private QuadTree northEast = null;      //1
    private QuadTree southWest = null;      //2
    private QuadTree southEast = null;      //3
    private Boundary boundary;

    //Data attribute
    private boolean freeSpace;

    /**
     * Costruttore radice
     **/
    public QuadTree(Boundary boundary) {
        this.level = 1;
        this.boundary = boundary;
        freeSpace = true;
        this.myCode = "";
    }

    public QuadTree(Obstacle[] obst, Boundary boundary) {
        this.level = 1;
        this.boundary = boundary;
        freeSpace = true;
        this.myCode = "";
        int nodeCount = 1;
//        while (nodeCount>0){
//
//
//        }

    }

    private static void foundFreeSpace(QuadTree node, Obstacle[] obst) {
//        Sat
    }

    /**
     * Costruttore dei nodi
     **/
    protected QuadTree(int level, Boundary boundary, String myCode) {
        this.level = level;
        this.boundary = boundary;
        this.myCode = myCode.replaceAll("[^0123]", "");
        freeSpace = true;
    }

    public QuadTree getDad() {
        return dad;
    }

    public QuadTree getNode(char c) throws RuntimeException {
        switch (c) {
            case '0':
                return getNode(NW);
            case '1':
                return getNode(NE);
            case '2':
                return getNode(SW);
            case '3':
                return getNode(SE);
            default:
                throw new RuntimeException("Codice:" + c + " impossibile!!");
        }
    }

    public QuadTree getNode(Coord c) {
        switch (c) {
            case NE:
                return northEast;
            case NW:
                return northWest;
            case SW:
                return southWest;
            case SE:
                return southEast;
        }
        return null;
    }

    public static Stack<QuadTree> qt2leaves(QuadTree root) {
        // tale funzione si proccupa di creare uno stack di nodi foglia del quadtree, e contemporaneamente genera i
        // nodi del grafo per il percorso del robot. Successivamente, un'altra funzione si preoccuperà di creare gli
        // archi tra nodi adiacenti, utilizzando lo stack ritornato da questa funzione.
        // todo: sviluppare la funzione che genera gli archi

        Queue<QuadTree> q = new LinkedList<QuadTree>();
        Stack<QuadTree> s = new Stack<QuadTree>();
        q.add(root);// add the root node to the queue

        while (!q.isEmpty()) {
            // add the children to the queue
            QuadTree n = q.remove();
            if (!n.isLeaf()) {
                q.add(n.getNode(NE));
                q.add(n.getNode(NW));
                q.add(n.getNode(SW));
                q.add(n.getNode(SE));
            }
            // add the extracted node to the Stack
            // here we must insert all the logic
            if (n.isLeaf() && n.isFreeSpace()) {
                s.add(n);
            }
            //so we add only white valid tiles
        }
        return s;
    }

    // Ritorna il nodo padre più vicino alla coordinata richiesta
    public static QuadTree nearestParent(QuadTree tree, String code) throws RuntimeException {
        code = code.replaceAll("[^0123]", "");

        QuadTree node = tree;
        char c;
        for (int i = 0; i < code.length(); i++) {
            c = code.charAt(i);
            node = node.getNode(c);
            if (node.isLeaf())
                break;
        }
//        System.out.println("Code search:" + code + "\tCode found:" + node.myCode);
        return node;
    }

    // Usando la tabella nel paper, genera il codice del vicino richiestp
    public static String FSMneighbors(String code, Side side) {

        code = code.replaceAll("[^0123]", "");
        char[] codeBuf = code.toCharArray();

        for (int i = code.length() - 1; i != -1; i--) {
            char c = codeBuf[i];
            switch (side) {
                case E:     //Right
                    switch (c) {
                        case '0':
                            codeBuf[i] = '1';
                            side = HALT;
                            break;
                        case '1':
                            codeBuf[i] = '0';
                            side = E;
                            break;
                        case '2':
                            codeBuf[i] = '3';
                            side = HALT;
                            break;
                        case '3':
                            codeBuf[i] = '2';
                            side = E;
                            break;
                        default:
                            break;
                    }
                    break;
                case W:     //Left
                    switch (c) {
                        case '0':
                            codeBuf[i] = '1';
                            side = W;
                            break;
                        case '1':
                            codeBuf[i] = '0';
                            side = HALT;
                            break;
                        case '2':
                            codeBuf[i] = '3';
                            side = W;
                            break;
                        case '3':
                            codeBuf[i] = '2';
                            side = HALT;
                            break;
                        default:
                            break;
                    }
                    break;
                case S:     //Down
                    switch (c) {
                        case '0':
                            codeBuf[i] = '2';
                            side = HALT;
                            break;
                        case '1':
                            codeBuf[i] = '3';
                            side = HALT;
                            break;
                        case '2':
                            codeBuf[i] = '0';
                            side = S;
                            break;
                        case '3':
                            codeBuf[i] = '1';
                            side = S;
                            break;
                        default:
                            break;
                    }
                    break;
                case N:     //Up
                    switch (c) {
                        case '0':
                            codeBuf[i] = '2';
                            side = N;
                            break;
                        case '1':
                            codeBuf[i] = '3';
                            side = N;
                            break;
                        case '2':
                            codeBuf[i] = '0';
                            side = HALT;
                            break;
                        case '3':
                            codeBuf[i] = '1';
                            side = HALT;
                            break;
                        default:
                            break;
                    }
                    break;
                case HALT:
                    break;
            }
        }
        if (side != HALT)  // Sto saltando tipo "PacMAn"
            code = "";
        else
            code = String.valueOf(codeBuf);
        return code;
    }

    // Trova il vicino richiesto del nodo corrente
    public QuadTree FSMneighbors(Side side) {
        if (!this.isLeaf())
            return null;
        String tgCode = FSMneighbors(this.myCode, side);
        if (tgCode.equals(""))  //Lato senza vicini
            return null;
        QuadTree tree = this;
        while (!tree.isRoot())
            tree = tree.dad;

        return nearestParent(tree, tgCode);
    }

    //Divido il nodo trasformandolo da foglia a split point
    public void split() throws RuntimeException {
        if (!isLeaf())
            throw new RuntimeException("Non è una foglia");

        northWest = new QuadTree(this.level + 1, boundary.getSector(NW), myCode + "0");
        northWest.dad = this;

        northEast = new QuadTree(this.level + 1, boundary.getSector(NE), myCode + "1");
        northEast.dad = this;

        southWest = new QuadTree(this.level + 1, boundary.getSector(SW), myCode + "2");
        southWest.dad = this;

        southEast = new QuadTree(this.level + 1, boundary.getSector(SE), myCode + "3");
        southEast.dad = this;

        setFreeSpace(false);

    }

    public static void main(String[] args) {
        // Riprodico QuadTree presente nella publicazione
        QuadTree qt = new QuadTree(new Boundary(-100, -100, 100, 100));
        qt.split();
        qt.getNode('1').split();
        qt.getNode('2').split();
        qt.getNode('2').getNode('1').split();
        qt.getNode('3').split();
        qt.getNode('3').getNode('0').split();
        qt.getNode('3').getNode('2').split();

        //Traveling the graph
        QuadTree.dfs(qt);
        System.out.println();
        //Test neighbors method

        QuadTree node = QuadTree.nearestParent(qt, "321");
        System.out.println(node.dataNode());

        System.out.print("Find Coord of Est neighbors of 302: ");
        System.out.println(QuadTree.FSMneighbors("302", E));

        System.out.print("Find Coord of West neighbors of 320: ");
        System.out.println(QuadTree.FSMneighbors("320", W));

        System.out.println("Find Node West neighbors 320:");
        System.out.println("\t" + QuadTree.nearestParent(qt, "320").FSMneighbors(W).dataNode());

        System.out.println("Find Node Sud neighbors 323 (Out of range):");
        node = QuadTree.nearestParent(qt, "323").FSMneighbors(S);
        if (node != null)
            System.out.println("\t" + node.dataNode());
        else
            System.out.println("\t Il nodo non esiste");

        System.out.println("Find Node Est neighbors 0 (Split Node):");
        node = QuadTree.nearestParent(qt, "0").FSMneighbors(E);
        if (node != null)
            System.out.println("\t" + node.dataNode());
        else
            System.out.println("\t Il nodo non esiste");

    }

    /* Traveling the QTGraph using Depth First Search*/
    public static void dfs(QuadTree node) {
        if (node == null)
            return;

        System.out.print(node.dataNode());

        if (node.isLeaf()) {
            System.out.print("|");
            for (int i = 0; i < node.level; i++)
                System.out.print("\t");
            System.out.println("\tLeaf Node. FreeSpace:" + node.isFreeSpace() + "\tCode:" + node.myCode);
        } else {
            for (int i = 0; i < node.level; i++)
                System.out.print("|-----");
            System.out.print("NW:");
            dfs(node.northWest);

            for (int i = 0; i < node.level; i++)
                System.out.print("|-----");
            System.out.print("NE:");
            dfs(node.northEast);

            for (int i = 0; i < node.level; i++)
                System.out.print("|-----");
            System.out.print("SW:");
            dfs(node.southWest);

            for (int i = 0; i < node.level; i++)
                System.out.print("|-----");
            System.out.print("SE:");
            dfs(node.southEast);
        }
    }

    // non lascia invariato lo stile
    static public void dfs(QuadTree node, PApplet win) {
        if (node == null)
            return;

        if (node.isLeaf()) {
            if (node.isFreeSpace())
                win.fill(255);
            win.rect((float) node.boundary.getxMin(), (float) node.boundary.getyMin(), (float) node.boundary.getW(), (float) node.boundary.getH());

        } else {

            win.fill(255, 0, 0);
            dfs(node.northEast, win);

            win.fill(0, 255, 0);
            dfs(node.northWest, win);

            win.fill(0, 0, 255);
            dfs(node.southWest, win);

            win.fill(0, 255, 255);
            dfs(node.southEast, win);
        }

    }

    public void printTree() {
        QuadTree.dfs(this);
    }

    public Boundary getBoundary() {
        return boundary;
    }

    public boolean isRoot() {
        return dad == null;
    }

    public boolean isLeaf() {
        return northWest == null && northEast == null &&
                southWest == null && southEast == null;
    }

    public boolean isFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(boolean freeSpace) {
        this.freeSpace = freeSpace;
    }

    public String dataNode() {
        return String.format("L%d code:%s; isLeaf:%s; isFree:%s %s", level, myCode, isLeaf(), isFreeSpace(), boundary.dataBoundary());
    }
}

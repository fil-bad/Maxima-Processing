package quadtree;

import geometry.Sat;
import geometry.Vertex;
import processing.core.PApplet;
import processingElement.Obstacle;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import static quadtree.Coord.*;
import static quadtree.Side.*;


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
    String myCode = "";
    float minSize;
    /**
     * Tree structure variable
     **/
    private int level = 0;
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
        float minSize = 0.1f;
        this.myCode = "";
    }

    public QuadTree(Obstacle[] obst, Boundary boundary, float minSize) {
        this.level = 1;
        this.boundary = boundary;
        freeSpace = true;
        this.myCode = "";
        this.minSize = minSize;
        foundFreeSpace(this, obst, minSize);
    }


    /**
     * Costruttore dei nodi
     **/
    protected QuadTree(int level, Boundary boundary, String myCode, float minSize) {
        this.level = level;
        this.boundary = boundary;
        this.myCode = myCode.replaceAll("[^0123]", "");
        this.minSize = minSize;
        freeSpace = true;
    }

    /**
     * Operazioni sul QT
     **/

    private static void foundFreeSpace(QuadTree node, Obstacle[] obst, float minSize) {
        // entro con i nodi bianchi, nell'avanzare li rendo neri o li splitto
        int i = 0;
        for (Obstacle ob : obst) {

            // se la finestra interseca un ostacolo
            if (Sat.haveCollided(ob.getPoly(), node.getBoundary().getPoly())) {
                // se La finestra è contenuta in un ostacolo allora è occupata
                if (Sat.contains(ob.getPoly(), node.getBoundary().getPoly())) {
                    node.setFreeSpace(false);
                    return;
                }
                // Se l'attuale bordo è troppo piccolo coloro nero e vado avanti
                if (node.getBoundary().getMinExtension() < minSize) {
                    node.setFreeSpace(false);
                    return;
                }
                node.split();
                // Get the slice of the Array
                foundFreeSpace(node.getNode(NW), Arrays.copyOfRange(obst, i, obst.length), minSize);
                foundFreeSpace(node.getNode(NE), Arrays.copyOfRange(obst, i, obst.length), minSize);
                foundFreeSpace(node.getNode(SW), Arrays.copyOfRange(obst, i, obst.length), minSize);
                foundFreeSpace(node.getNode(SE), Arrays.copyOfRange(obst, i, obst.length), minSize);
                return;
            }

            i++;
        }
    }

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
        return node;
    }

    public static QuadTree nearestPoint(QuadTree tree, Vertex v) throws RuntimeException {
        if (tree.isLeaf())
            return tree;
        for (Coord c : Coord.values()) {
            QuadTree n = tree.getNode(c);
            if (n.getBoundary().inRange(v))
                return nearestPoint(n, v);
        }
        throw new RuntimeException("Point" + v.toString() + " OUT OF BOUNDARY");
    }

    public static String FSMneighbors(String code, Side side) {
        if (side == HALT)
            return "";
        code = code.replaceAll("[^0123]", "");
        char[] codeBuf = code.toCharArray();

        for (int i = code.length() - 1; i != -1; i--) {
            char c = codeBuf[i];
            switch (side) {
                case R:     //Right
                    switch (c) {
                        case '0':
                            codeBuf[i] = '1';
                            side = HALT;
                            break;
                        case '1':
                            codeBuf[i] = '0';
                            side = R;
                            break;
                        case '2':
                            codeBuf[i] = '3';
                            side = HALT;
                            break;
                        case '3':
                            codeBuf[i] = '2';
                            side = R;
                            break;
                        default:
                            break;
                    }
                    break;
                case L:     //Left
                    switch (c) {
                        case '0':
                            codeBuf[i] = '1';
                            side = L;
                            break;
                        case '1':
                            codeBuf[i] = '0';
                            side = HALT;
                            break;
                        case '2':
                            codeBuf[i] = '3';
                            side = L;
                            break;
                        case '3':
                            codeBuf[i] = '2';
                            side = HALT;
                            break;
                        default:
                            break;
                    }
                    break;
                case D:     //Down
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
                            side = D;
                            break;
                        case '3':
                            codeBuf[i] = '1';
                            side = D;
                            break;
                        default:
                            break;
                    }
                    break;
                case U:     //Up
                    switch (c) {
                        case '0':
                            codeBuf[i] = '2';
                            side = U;
                            break;
                        case '1':
                            codeBuf[i] = '3';
                            side = U;
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
                case RU:     //Right-Up
                    switch (c) {
                        case '0':
                            codeBuf[i] = '3';
                            side = U;
                            break;
                        case '1':
                            codeBuf[i] = '2';
                            side = RU;
                            break;
                        case '2':
                            codeBuf[i] = '1';
                            side = HALT;
                            break;
                        case '3':
                            codeBuf[i] = '0';
                            side = R;
                            break;
                        default:
                            break;
                    }
                    break;
                case RD:     //Right-Down
                    switch (c) {
                        case '0':
                            codeBuf[i] = '3';
                            side = HALT;
                            break;
                        case '1':
                            codeBuf[i] = '2';
                            side = R;
                            break;
                        case '2':
                            codeBuf[i] = '1';
                            side = D;
                            break;
                        case '3':
                            codeBuf[i] = '0';
                            side = RD;
                            break;
                        default:
                            break;
                    }
                    break;
                case LD:     //Left-Down
                    switch (c) {
                        case '0':
                            codeBuf[i] = '3';
                            side = L;
                            break;
                        case '1':
                            codeBuf[i] = '2';
                            side = HALT;
                            break;
                        case '2':
                            codeBuf[i] = '1';
                            side = LD;
                            break;
                        case '3':
                            codeBuf[i] = '0';
                            side = D;
                            break;
                        default:
                            break;
                    }
                    break;
                case LU:     //Left-Up
                    switch (c) {
                        case '0':
                            codeBuf[i] = '3';
                            side = LU;
                            break;
                        case '1':
                            codeBuf[i] = '2';
                            side = U;
                            break;
                        case '2':
                            codeBuf[i] = '1';
                            side = L;
                            break;
                        case '3':
                            codeBuf[i] = '0';
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
    // Ritorna il nodo padre più vicino alla coordinata richiesta

    /**
     * Dati di ritorno
     **/
    public static Stack<QuadTree> qt2leaves(QuadTree root) {
        // tale funzione si proccupa di creare uno stack di nodi foglia del quadtree, e contemporaneamente genera i
        // nodi del grafo per il percorso del robot. Successivamente, un'altra funzione si preoccuperà di creare gli
        // archi tra nodi adiacenti, utilizzando lo stack ritornato da questa funzione.

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
                s.push(n);
            }
            //so we add only white valid tiles
        }
        return s;
    }
    // Usando la tabella nel paper, genera il codice del vicino richiestp

    /**
     * Explore QT
     **/
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

    static public void dfs(QuadTree node, PApplet win) {
        if (node == null)
            return;

        if (node.isLeaf()) {
            if (node.isFreeSpace())
                win.noFill();
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

    /**
     * Demo Main
     **/
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
        System.out.println(QuadTree.FSMneighbors("302", R));

        System.out.print("Find Coord of West neighbors of 320: ");
        System.out.println(QuadTree.FSMneighbors("320", L));

        System.out.println("Find Node West neighbors 320:");
        System.out.println("\t" + QuadTree.nearestParent(qt, "320").FSMneighbors(L).dataNode());

        System.out.println("Find Node Sud neighbors 323 (Out of range):");
        node = QuadTree.nearestParent(qt, "323").FSMneighbors(D);
        if (node != null)
            System.out.println("\t" + node.dataNode());
        else
            System.out.println("\t Il nodo non esiste");

        System.out.println("Find Node Est neighbors 0 (Split Node):");
        node = QuadTree.nearestParent(qt, "0").FSMneighbors(R);
        if (node != null)
            System.out.println("\t" + node.dataNode());
        else
            System.out.println("\t Il nodo non esiste");

    }
    //Ritorna in quale lato è vicino il nodo n

    //Divido il nodo trasformandolo da foglia a split point
    public void split() throws RuntimeException {
        if (!isLeaf())
            throw new RuntimeException("Non è una foglia");

        northWest = new QuadTree(this.level + 1, getBoundary().getSector(NW), myCode + "0", minSize);
        northWest.dad = this;

        northEast = new QuadTree(this.level + 1, getBoundary().getSector(NE), myCode + "1", minSize);
        northEast.dad = this;

        southWest = new QuadTree(this.level + 1, getBoundary().getSector(SW), myCode + "2", minSize);
        southWest.dad = this;

        southEast = new QuadTree(this.level + 1, getBoundary().getSector(SE), myCode + "3", minSize);
        southEast.dad = this;

        setFreeSpace(false);

    }

    public void maxSplit() throws RuntimeException {
        if (this.getBoundary().getMinExtension() < minSize)
            return;
        else {
            split();
            for (Coord c : Coord.values()) {
                QuadTree node = this.getNode(c);
                node.maxSplit();
            }
        }
    }

    /**
     * Query al QT
     **/
    public QuadTree nearestPoint(Vertex v) throws RuntimeException {
        return nearestPoint(this, v);
    }

    public QuadTree nearestPoint(float x, float y) throws RuntimeException {
        return nearestPoint(this, new Vertex(x, y));
    }

    // HALT = Non è vicino/non sono una foglia
    public Side neighborsSide(QuadTree n) {
        if (!this.isLeaf())
            return null;
        String neighCode = "";
        Side sRet = HALT;
        int minDigit;
        for (Side s : Side.values()) {
            neighCode = FSMneighbors(this.myCode, s);
//            System.out.println("neighborsSide s=" + s.name() + "  neighCode=" + neighCode + "  digit " + neighCode.length() + "  myCode=" + n.myCode + "  digit " + n.myCode.length());
            minDigit = Math.min(neighCode.length(), n.myCode.length());
            if (sRet == HALT)
                // devo troncare i nomi al più corto per confrontarli
                if (neighCode.substring(0, minDigit).equals(n.myCode.substring(0, minDigit)))
                    sRet = s;
            if (sRet == U || sRet == D || sRet == L || sRet == R)
                break;
        }
        return sRet;
    }

    // Trova il vicino richiesto del nodo corrente
    public QuadTree FSMneighbors(Side side) {
        if (!this.isLeaf())
            return null;
        if (side == HALT)
            return null;
        String tgCode = FSMneighbors(this.myCode, side);
        if (tgCode.equals(""))  //Lato senza vicini
            return null;
        QuadTree tree = this;
        while (!tree.isRoot())
            tree = tree.dad;
        return nearestParent(tree, tgCode);
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
    // non lascia invariato lo stile

    public void setFreeSpace(boolean freeSpace) {
        this.freeSpace = freeSpace;
    }

    public String dataNode() {
        return String.format("L%d code:%s; isLeaf:%s; isFree:%s %s", level, myCode, isLeaf(), isFreeSpace(), boundary.dataBoundary());
    }

    public void printTree() {
        QuadTree.dfs(this);
    }
}

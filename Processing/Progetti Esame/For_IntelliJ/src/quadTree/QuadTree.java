package quadTree;

import processing.core.PApplet;

import static quadTree.Coord.*;
import static quadTree.Side.*;
// The method to find neighbor are describe here:
//http://web.archive.org/web/20120907211934/http://ww1.ucmss.com/books/LFS/CSREA2006/MSV4517.pdf
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
    private final int MAX_CAPACITY = 4;
    private int level = 0;
    String myCode = "";

    private boolean freeSpace;

    private QuadTree dad = null;


    private QuadTree northWest = null;      //0
    private QuadTree northEast = null;      //1
    private QuadTree southWest = null;      //2
    private QuadTree southEast = null;      //3
    private Boundary boundary;

    /**
     * Costruttore radice
     **/
    public QuadTree(Boundary boundry) {
        this.level = 1;
        this.boundary = boundry;
        freeSpace = true;
    }

    /**
     * Costruttore dei nodi
     **/
    protected QuadTree(int level, Boundary boundry, String myCode) {
        this.level = level;
        this.boundary = boundry;
        this.myCode = myCode;
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

    // Ritorna il nodo più vicino alla coordinata richiesta, per verificare se sia proprio lui
    // è suffucente vedere se level == numero di caratteri nel codice
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
        System.out.println("Code search:" + code + "\tCode found:" + node.myCode);
        return node;
    }

    // Usando la tabella nel paper, ora genererò un nuovo codice
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
                            side = W;
                            break;
                        case '2':
                            codeBuf[i] = '3';
                            side = HALT;
                            break;
                        case '3':
                            codeBuf[i] = '2';
                            side = W;
                            break;
                        default:
                            break;
                    }
                    break;
                case W:     //Left
                    switch (c) {
                        case '0':
                            codeBuf[i] = '1';
                            side = E;
                            break;
                        case '1':
                            codeBuf[i] = '0';
                            side = HALT;
                            break;
                        case '2':
                            codeBuf[i] = '3';
                            side = E;
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
    code =String.valueOf(codeBuf);
        return code;
}

    public QuadTree FSMneighbors(Side side) {
        if (!this.isLeaf())
            return null;
        String tgCode = FSMneighbors(this.myCode,side);
        QuadTree tree = this;
        while (!tree.isRoot())
            tree = tree.dad;

        return nearestParent(tree,tgCode);
    }

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

    /* Traveling the Graph using Depth First Search*/
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

    public Boundary getBoundry() {
        return boundary;
    }

    public String dataNode() {
        String a = String.format("L%d %s", level, boundary.dataBoundary());
        return a;
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

    public static void main(String args[]) {
        QuadTree qt = new QuadTree(new Boundary(0, 0, 1000, 1000));
        qt.split();
        qt.getNode(SE).split();
        qt.getNode(SE).getNode(NW).setFreeSpace(false);
        qt.getNode(SE).getNode(NW).split();
        //Traveling the graph
        QuadTree.dfs(qt);

        QuadTree node = qt.nearestParent(qt, "321");
        System.out.println(node.dataNode());

        System.out.println(QuadTree.FSMneighbors("302", E));

        System.out.println("SE-NW-NE find nord neighborsN:");
        System.out.println(qt.getNode(SE).getNode(NW).getNode(NE).FSMneighbors(N).dataNode());

    }
}

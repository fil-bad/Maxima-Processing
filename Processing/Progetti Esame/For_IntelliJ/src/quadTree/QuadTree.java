package quadtree;

import processing.core.PApplet;

import static quadtree.Coord.*;

/*
 *  			N
 *  		W		E
 *  			S
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

    private boolean freeSpace;

    private QuadTree dad = null;


    private QuadTree northEast = null;
    private QuadTree northWest = null;
    private QuadTree southWest = null;
    private QuadTree southEast = null;
    private Boundary boundary;

    public QuadTree(int level, Boundary boundry) {
        this.level = level;
        this.boundary = boundry;
        freeSpace = true;
    }

    public QuadTree getDad() {
        return dad;
    }

    public QuadTree getNode(Coord c) {
        switch (c){
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

    public void split() throws RuntimeException {
        if (!isLeaf())
            throw new RuntimeException("Non è una foglia");

        northEast = new QuadTree(this.level + 1, boundary.getSector(NE));
        northEast.dad = this;

        northWest = new QuadTree(this.level + 1, boundary.getSector(NW));
        northWest.dad = this;

        southWest = new QuadTree(this.level + 1, boundary.getSector(SW));
        southWest.dad = this;

        southEast = new QuadTree(this.level + 1, boundary.getSector(SE));
        southEast.dad = this;

        setFreeSpace(false);

    }

    public Boundary getBoundry() {
        return boundary;
    }

    /* Traveling the Graph using Depth First Search*/
    static void dfs(QuadTree node) {
        if (node == null)
            return;

        System.out.printf("L%d [X1=%.2f Y1=%.2f] \t[X2=%.2f Y2=%.2f]\n",
                node.level, node.boundary.getxMin(), node.boundary.getyMin(),
                node.boundary.getxMax(), node.boundary.getyMax());

        if (node.isLeaf()) {
            System.out.print("|");
            for (int i = 0; i < node.level; i++)
                System.out.print("\t");
            System.out.println("\tLeaf Node. FreeSpace:" + node.isFreeSpace());
        } else {
            for (int i = 0; i < node.level; i++)
                System.out.print("|-----");
            System.out.print("NE:");
            dfs(node.northEast);

            for (int i = 0; i < node.level; i++)
                System.out.print("|-----");
            System.out.print("NW:");
            dfs(node.northWest);

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
        QuadTree qt = new QuadTree(1, new Boundary(0, 0, 1000, 1000));
        qt.split();
        qt.getNode(SE).split();
        qt.getNode(SE).getNode(NW).setFreeSpace(false);
        qt.getNode(SE).getNode(NW).split();
        //Traveling the graph
        QuadTree.dfs(qt);
    }
}

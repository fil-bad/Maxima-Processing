package quadtree;

/*
 *  			N
 *  		W		E
 *  			S
 */

public class Node {
    private int x, y;
    private boolean leaf,freeSpace;

    Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public void setFreeSpace(boolean freeSpace) {
        this.freeSpace = freeSpace;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public boolean isFreeSpace() {
        return freeSpace;
    }
}
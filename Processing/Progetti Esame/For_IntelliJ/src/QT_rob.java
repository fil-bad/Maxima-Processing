import javaMisc.Polygon;
import quadtree.QuadTree;
import javaMisc.Sat;
public class QT_rob extends QuadTree<Float>{

    /**
     * Constructs a new quad tree.
     *
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     */
    public QT_rob(double minX, double minY, double maxX, double maxY) {
        super(minX, minY, maxX, maxY);
    }

    public void generateQT(Polygon[] obstacles, float minEdge){
        //todo ricorsivamente splitto l'albero finchè non ho celle solo bianche o solo nere usando sat
        // creare pure una funzione tutto ricoperto
    }

    //private void split()

    public static void main(String[] args) {
        QuadTree quadTree = new QuadTree(-1000,-1000,1000,1000);
    //todo creare un quad tree semplice, debaggare e ottenere informazioni dai nodi
    }

}

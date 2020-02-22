package geometry;
import static java.lang.Math.*;

public class Polygon {

    static final double EPS = 1e-9;

    private Vertex[] vertices;

    public Polygon(Vertex... vertexes) throws RuntimeException {
        if (vertexes.length <= 2) throw new RuntimeException("Invalid size for a polygon");
        this.vertices = vertexes;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public int numVertices() {
        return this.vertices.length;
    }

    /** Ruota ogni punto di rad, rispetto al  (0,0)    */
    public void rotate(double rad){
        for (Vertex i : this.vertices)
            i.rotate(rad);
    }

    /** Trasla ogni punto di rad, rispetto al  (0,0)    */
    public void translate(double x,double y){
        for (Vertex i : this.vertices)
            i.translate(x, y);
    }

    public void printVertices(){
        System.out.print("{");
        for (Vertex elem: this.vertices){
            elem.printVertex();
        }
        System.out.println("}");
    }

    public double perimeter()
    {
        double sum = 0.0;
        for(int i = 0; i < this.vertices.length - 1; ++i)
            sum += this.vertices[i].dist(this.vertices[i+1]);
        return sum;
    }

    public double area() 		//clockwise/anti-clockwise check, for convex/concave polygons
    {
        double area = 0.0;
        for(int i = 0; i < this.vertices.length - 1; ++i)
            area += this.vertices[i].getX() * this.vertices[i+1].getY() - this.vertices[i].getY() * this.vertices[i+1].getX();
        return Math.abs(area) / 2.0;			//negative value in case of clockwise
    }

    public boolean contains(Vertex tested) {
        //verifying if a Vertex is contained in the polygon; for further explanation, see
        //https://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon
        int i, j;
        boolean result = false;
        double test_x = tested.getX();
        double test_y = tested.getY();

        for (i = 0, j = this.vertices.length - 1; i < this.vertices.length; j = i++) {
            if ((this.vertices[i].getY() > test_y) != (this.vertices[j].getY() > test_y) &&
                    (test_x < (this.vertices[j].getX() - this.vertices[i].getX()) * (test_y - this.vertices[i].getY()) / (this.vertices[j].getY() - this.vertices[i].getY()) + this.vertices[i].getX())) {
                result = !result;
            }
        }
        if (!result) { // se riesco a trovare almeno un punto che va bene, allora sono sul bordo
            for (int k = 0; k < this.vertices.length - 1; k++) {
                if (isLeft(this.vertices[k], this.vertices[k + 1], tested) == 0.0) {
                    // unico falso positivo, i bordi sono su un asse, esamino ciò
                    double v1_x = this.vertices[k].getX();
                    double v2_x = this.vertices[k + 1].getX();
                    double v1_y = this.vertices[k].getY();
                    double v2_y = this.vertices[k + 1].getY();

                    if (v1_x - v2_x == 0.0) { // cambia solo y
                        if (max(v1_y, v2_y) >= test_y && min(v1_y, v2_y) <= test_y) return true;
                    }
                    if (v1_y - v2_y == 0.0) { // cambia solo x
                        if (max(v1_x, v2_x) >= test_x && min(v1_x, v2_x) <= test_x) return true;
                    }
                }
            }
        }
        return result;
    }

    private static double isLeft(Vertex P0, Vertex P1, Vertex P2 ){
        // lo risolve come sottrazione di aree, se è nulla ci troviamo sulla linea tra P0 e P1
        // >0 for P2 left of the line through P0 and P1
        // =0 for P2  on the line
        // <0 for P2  right of the line

        return ( (P1.getX() - P0.getX()) * (P2.getY() - P0.getY())
                - (P2.getX()-  P0.getX()) * (P1.getY() - P0.getY()) );
    }

    public static void main(String[] args) throws Exception {

        Vertex v1 = new Vertex(5, 2);
        Vertex v2 = v1.orthogonal();
        Polygon p1 = new Polygon(v1,v2);
        System.out.println("p1 = " + p1.numVertices());
        p1.printVertices();
        Vertex[] v_s = {v1, v2};
        Polygon p2 = new Polygon(v_s);
        System.out.println("p2 = " + p2.numVertices());
        p2.printVertices();

        System.out.println("p1 rotate by 90°");
        p1.rotate(PI/2.0);
        p1.printVertices();

        System.out.println("p1 translate by (5,9)");
        p1.translate(5,9);
        p1.printVertices();
    }

}

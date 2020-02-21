package javaMisc;
import static java.lang.Math.*;

public class Polygon {

    static final double EPS = 1e-9;

    private Vertex[] vertices;

 //   public Polygon(Vertex... vertexes) throws Exception {
 //       if (vertexes.length <= 2) throw new Exception("Invalid size for a polygon");
 //       this.vertices = vertexes;
 //   }
    public Polygon(Vertex... vertexes) {
        this.vertices = vertexes;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public int numVertices(){
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
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = this.vertices.length - 1; i < this.vertices.length; j = i++) {
            if ((this.vertices[i].getY() > tested.getY()) != (this.vertices[j].getY() > tested.getY()) &&
                    (tested.getX() < (this.vertices[j].getX() - this.vertices[i].getX()) * (tested.getY() - this.vertices[i].getY()) / (this.vertices[j].getY()-this.vertices[i].getY()) + this.vertices[i].getX())) {
                result = !result;
            }
        }
        if (!result){ // se riesco a trovare almeno un punto che va bene, allora sono sul bordo
            for (int k = 0; k<this.vertices.length-1; k++) {
                if(isLeft(this.vertices[k], this.vertices[k + 1], tested) == 0.0) return true;
            }
        }
        return result;
    }

    // function brought from:
    // https://github.com/AhmadElsagheer/Competitive-programming-library/blob/master/geometry/Polygon.java

    public int wn_PnPoly2(Vertex P)
    {
        int wn = 0;    // the  winding number counter

        // loop through all edges of the polygon
        for (int i = 0; i<this.vertices.length-1; i++) {   // edge from V[i] to  V[i+1]
            if (this.vertices[i].getY() <= P.getY()) {          // start y <= P.y
                if (this.vertices[i + 1].getY()  > P.getY())      // an upward crossing
                {
                    double l = isLeft(this.vertices[i], this.vertices[i + 1], P);
                    if (l > EPS)  // P left of  edge
                        ++wn;            // have  a valid up intersect
                    else if (l == EPS) // boundary
                        return 1;
                }
            }
            else {                        // start y > P.y (no test needed)
                if (this.vertices[i + 1].getY() <= P.getY())     // a downward crossing
                {
                    double l = isLeft(this.vertices[i], this.vertices[i + 1], P);
                    if (l < EPS)  // P right of  edge
                        --wn;            // have  a valid down intersect
                    else if (l == EPS)
                        return 0;
                }
            }
        }
        return wn;
    }

    private static double isLeft(Vertex P0, Vertex P1, Vertex P2 ){
        // lo risolve come sottrazione di aree, se è nulla ci troviamo sulla linea tra P0 e P1
        // >0 for P2 left of the line through P0 and P1
        // =0 for P2  on the line
        // <0 for P2  right of the line

        return ( (P1.getX() - P0.getX()) * (P2.getY() - P0.getY())
                - (P2.getX()-  P0.getX()) * (P1.getY() - P0.getY()) );
    }



    public boolean inside(Vertex v)	//for convex/concave polygons - winding number algorithm
    {
        double sum = 0.0;
        for(int i = 0; i <this.vertices.length - 1; ++i)
        {
            double angle = Vertex.angle(this.vertices[i], v, vertices[i+1]);
            if((Math.abs(angle) < EPS || Math.abs(angle - Math.PI) < EPS) && v.between(this.vertices[i], this.vertices[i+1]))
                return true;
            if(Vertex.ccw(v, this.vertices[i], this.vertices[i+1]))
                sum += angle;
            else
                sum -= angle;
        }
        return Math.abs(2 * Math.PI - Math.abs(sum)) < EPS;		//abs makes it work for clockwise
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

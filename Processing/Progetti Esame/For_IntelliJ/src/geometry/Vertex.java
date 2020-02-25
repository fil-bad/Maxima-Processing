package geometry;

import org.ejml.data.DMatrix2;
import org.ejml.data.DMatrix2x2;
import org.ejml.dense.fixed.CommonOps_DDF2;
import processing.core.PApplet;

import static java.lang.Math.*;


public class Vertex {
    DMatrix2 pos, ap;
    static final double EPS = 1e-9;

    public Vertex(double x, double y) {
        pos = new DMatrix2(x, y);
        ap = new DMatrix2();

    }

    // Vettore relativo
    public Vertex(Vertex a, Vertex b) {
        this(b.getX() - a.getX(), b.getY() - a.getY());
    }


    public double dist(Vertex p) {
        CommonOps_DDF2.subtract(pos, p.get(), ap);
        return Math.sqrt(sq(ap.a1) + sq(ap.a2));
    }

    static double sq(double x) {
        return x * x;
    }
    double cross(Vertex v) { return this.getX() * v.getY() - getY() * v.getX(); }
    double dot(Vertex v) { return (this.getX() * v.getX() + this.getY() * v.getY()); }
    double norm2() { return this.getX() * this.getX() + this.getY() * this.getY(); }

    boolean between(Vertex p, Vertex q)
    {
        return this.getX() < Math.max(p.getX(), q.getX()) + EPS && this.getX() + EPS > Math.min(p.getX(), q.getX())
                && this.getY() < Math.max(p.getY(), q.getY()) + EPS && this.getY() + EPS > Math.min(p.getY(), q.getY());
    }
    static boolean ccw(Vertex p, Vertex q, Vertex r)
    {
        return new Vertex(p, q).cross(new Vertex(p, r)) > 0;
    }


    static double angle(Vertex a, Vertex o, Vertex b)  // angle AOB
    {
        Vertex oa = new Vertex(o, a), ob = new Vertex(o, b);
        return Math.acos(oa.dot(ob) / Math.sqrt(oa.norm2() * ob.norm2()));
    }

    public void translate(double x, double y) {
        ap.set(x, y);
        CommonOps_DDF2.addEquals(pos, ap);
    }

    public void translate(Vertex p) {
        CommonOps_DDF2.addEquals(pos, p.get());
    }

    public void rotate(double rad) {
        DMatrix2x2 R = new DMatrix2x2(
                cos(rad), -sin(rad),
                sin(rad), cos(rad));
        CommonOps_DDF2.mult(R, pos, ap);
        pos.set(ap);
    }

    /**
     *  +1 := Oltre l'asse X, o se sull'asse con Y maggiore
     * -1 := opposto
     * 0 := Stesso punto entro EPS
     **/
    public int compareTo(Vertex p) {
        CommonOps_DDF2.subtract(pos, p.get(), ap);
        if (Math.abs(ap.a1) > EPS) return pos.a1 > p.get().a1 ? 1 : -1; // my.x > p.x return 1 else -1
        if (Math.abs(ap.a2) > EPS) return pos.a2 > p.get().a2 ? 1 : -1; // my.y > p.y return 1 else -1
        return 0;
    }

    /** returns true if this Vertex it is on the line defined by a and b **/
    boolean onLine(Vertex a, Vertex b) {
        if (a.compareTo(b) == 0) return compareTo(a) == 0;      // Se a,b molto vicini, li considero stesso punto
        // a X b = 0 se a//b
        CommonOps_DDF2.subtract(a.get(),b.get(),ap);
        double z = pos.a1 * ap.a2 - pos.a2 * ap.a1;     // x*ap.y - y*ap.x
        return Math.abs(z) < EPS; // a X b = 0 se a//b
    }

    boolean onSegment(Vertex a, Vertex b)
    {
        if(onLine(a,b)){
            if(a.compareTo(b) == 1){    //a oltre di b
                return a.compareTo(this) == -1 && b.compareTo(this) == 1;   // prima di a, dopo b
            }else{                      //a prima di b
                return a.compareTo(this) == 1 && b.compareTo(this) == -1;   // dopo di a, prima b
            }
        }else
            return false;

    }

    public void set(double x, double y) {
        pos.set(x, y);
    }

    /** Returns a new vector which is orthogonal to the current vector **/
    public Vertex orthogonal() {
        return new Vertex(-getY(), getX());
    }

    public DMatrix2 get() {
        return pos;
    }

    public double getX() {
        return pos.get(0, 0);
    }

    public double getY() {
        return pos.get(1, 0);
    }

    public void printVertex() {
        System.out.print(this.toString());
    }

    public void printVertex(PApplet win, float r) {
        win.pushStyle();
        win.fill(255, 90, 0);
        win.stroke(0);
        win.strokeWeight(1);
        win.pushMatrix();
        win.translate(0, 0, 2);

        win.circle((float) this.getX(), (float) this.getY(), (float) r);

        win.popMatrix();
        win.popStyle();
    }

    @Override
    public String toString() {
        return "[" + getX() + ";" + getY() + "]";
    }

    public static void main(String[] args) {
        Vertex v1 = new Vertex(5, 2);
        v1.printVertex();
        Vertex v2 = v1.orthogonal();
        System.out.println("");
        System.out.print("orthogonal of v1:");
        v2.printVertex();
        System.out.println("\nv2 Rotate by 180°:");
        v2.rotate(PI);
        v2.printVertex();
        System.out.println("\nv2 Rotate by 180°+90°:");
        v2.rotate(PI / 2.0);
        v2.printVertex();
        System.out.println("\nv2 translate by +10,+20:");
        v2.translate(10, 20);
        v2.printVertex();

        Vertex l1 = new Vertex(5, 0);
        Vertex l2 = new Vertex(6, 0);
        Vertex l3 = new Vertex(7, 0);
        System.out.println("\nl2 è tra sulla linea l1 e l3? "+ l2.onLine(l1,l3));
        System.out.println("\nl1 è tra sulla linea l2 e l3? "+ l1.onLine(l2,l3));
        System.out.println("\nl1 è tra sul segmento l2 e l3? "+ l1.onSegment(l2,l3));



    }
}


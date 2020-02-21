package javaMisc;

import org.ejml.data.DMatrix2;
import org.ejml.data.DMatrix2x2;
import org.ejml.dense.fixed.CommonOps_DDF2;
import org.ejml.dense.fixed.NormOps_DDF2;
import org.ejml.dense.row.NormOps_DDRM;

import static java.lang.Math.*;


public class Vertex {
    DMatrix2 pos, ap;
    static final double EPS = 1e-9;

    public Vertex(double x, double y) {
        pos = new DMatrix2(x, y);
        ap = new DMatrix2();
    }

    public double dist(Vertex p) {
        CommonOps_DDF2.subtract(pos, p.get(), ap);
        return Math.sqrt(sq(ap.a1) + sq(ap.a2));
    }

    static double sq(double x) {
        return x * x;
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

    // +1 := Oltre l'asse X, o se sull'asse con Y maggiore
    // -1 := opposto
    // 0 := Stesso punto entro EPS
    public int compareTo(Vertex p) {
        CommonOps_DDF2.subtract(pos, p.get(), ap);
        if (Math.abs(ap.a1) > EPS) return pos.a1 > p.get().a1 ? 1 : -1;
        if (Math.abs(ap.a2) > EPS) return pos.a2 > p.get().a2 ? 1 : -1;
        return 0;
    }

    /** returns true if this Vertex it is on the line defined by a and b **/
//    boolean onLine(Vertex a, Vertex b) {
//        if (a.compareTo(b) == 0) return compareTo(a) == 0;
//        return Math.abs(new Vector(a, b).cross(new Vector(a, this))) < EPS;
//    }

    public void set(double x, double y) {
        pos.set(x, y);
    }

    /** Returns a new vector which is orthogonal to the current vector **/
    //todo: chiedere a fil perchè ritorna il perpendicolare negativo
    public Vertex orthogonal() {
        return new Vertex(getY(), -getX());
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
    }
}


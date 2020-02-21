package javaMisc;
import org.ejml.data.DMatrix2;
import org.ejml.data.DMatrix2x2;
import org.ejml.dense.fixed.CommonOps_DDF2;

import static java.lang.Math.*;

public class Vertex {
        DMatrix2 pos, ap;

        public Vertex(double x, double y) {
            pos = new DMatrix2(x,y);
            ap = new DMatrix2();
        }

        public void translate(double x, double y){
            ap.set(x,y);
            CommonOps_DDF2.addEquals(pos,ap);
        }

        public void rotate (double rad){
            DMatrix2x2 R = new DMatrix2x2(
                    cos(rad), -sin(rad),
                    sin(rad), cos(rad));
            CommonOps_DDF2.mult(R,pos, ap);
            pos.set(ap);
        }

        public void set(double x, double y){
            pos.set(x,y);
        }

        /**
         * Returns a new vector which is orthogonal to the current vector
         */
        //todo: chiedere a fil perchè ritorna il perpendicolare negativo
        public Vertex orthogonal() {
            return new Vertex(getY(), -getX());
        }

    public double getX() {
        return pos.get(0,0);
    }

    public double getY() {
        return pos.get(1,0);
    }

    public void printVertex() {
            System.out.print(this.toString());
        }

        @Override
        public String toString (){
            return "[" + getX() + ";" + getY() + "]";
        }

        public static void main(String[] args){
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
            v2.rotate(PI/2.0);
            v2.printVertex();
            System.out.println("\nv2 translate by +10,+20:");
            v2.translate(10,20);
            v2.printVertex();
        }
}


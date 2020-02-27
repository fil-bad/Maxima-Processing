package processingElement;

import geometry.Vertex;
import org.ejml.data.DMatrix4;
import org.ejml.data.DMatrix4x4;
import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class Rover {
    Vertex pos, obj;
    SimpleMatrix A, B, xnew, x;

    Stack<Vertex> checkPoint = new Stack<>();

    PApplet win;
    double rho, ka;
    double kp, ki, kd;

    public Rover(PApplet win, double rho, double ka, double kp) {
        this.pos = new Vertex(0, 0);
        this.obj = new Vertex(0, 0);

        A = new SimpleMatrix(4, 4);
        A.set(0, 0, -rho);
        A.set(1, 1, -rho);
        A.set(2, 0, 1);
        A.set(3, 1, 1);
        B = new SimpleMatrix(4, 2);
        B.set(0, 0, ka);
        B.set(1, 1, ka);

        xnew = new SimpleMatrix(4, 1);
        x = new SimpleMatrix(4, 1);
        xnew.zero();
        x.set(2, 0, pos.getX());
        x.set(3, 0, pos.getY());

        this.win = win;
        this.rho = rho;
        this.ka = ka;
        this.kp = kp;
    }

    public Rover(PApplet win, Vertex start, double rho, double ka, double kp) {
        this.pos = start;
        this.obj = new Vertex(start);

        A = new SimpleMatrix(4, 4);
        A.set(0, 0, -rho);
        A.set(1, 1, -rho);
        A.set(2, 0, 1);
        A.set(3, 1, 1);
        B = new SimpleMatrix(4, 2);
        B.set(0, 0, ka);
        B.set(1, 1, ka);

        xnew = new SimpleMatrix(4, 1);
        x = new SimpleMatrix(4, 1);
        xnew.zero();
        x.set(2, 0, pos.getX());
        x.set(3, 0, pos.getY());

        this.win = win;
        this.rho = rho;
        this.ka = ka;
        this.kp = kp;
    }

    int w = 40;
    int h = 10;


    public void draw() {
        controllStep();

        win.push();
        //Move the level
        win.translate((float) pos.getX(), (float) pos.getY());
        roverDraw();

        win.pop();
    }

    private void controllStep() {

        SimpleMatrix er = new SimpleMatrix(4, 1);
        er.set(2, 0, pos.getX() - obj.getX());
        er.set(3, 0, pos.getY() - obj.getY());

        // Errore troppo piccolo, smetto di fare l'update


        //Cambio obiettivo prima di fermarsi
        if (er.normF() < 1) {
            if (!checkPoint.isEmpty())
                obj = checkPoint.remove(0);
        }

        er = er.scale(-kp);

        if (er.normF() < 0.0001) {
            return;
        }
        x.print();
        xnew.print();
        SimpleMatrix u = new SimpleMatrix(2, 1);
        u.set(0, 0, er.get(2, 0));
        u.set(1, 0, er.get(3, 0));

        //Uso un controllo proporzionale, quindi moltiplico l'errore per kp
        System.out.println("errore e controllo");
        er.print();
        u.print();
        System.out.println("uNorm:" + u.normF());
        if (u.normF() > 1)
            u = u.divide(u.normF());
        u.print();

        System.out.println("Matrici A*x e B*u");
        A.mult(x).print();
        B.mult(u).print();
        xnew = x.plus(A.mult(x).plus(B.mult(u)));
        System.out.println("xNew");
        xnew.print();
        System.out.println("x");
        x.print();

        pos.set(xnew.get(2, 0), xnew.get(3, 0));
        x.set(xnew);
    }

    public void setObj(Vertex obj) {
        if (obj != null)
            this.obj.set(obj);
    }

    public void setObjs(Vertex[] objs) {
        Collections.addAll(checkPoint, objs);
    }

    public void clearObjs() {
        checkPoint.clear();
    }


    private void roverDraw() {
        //Draw the robot
        win.fill(0, 200, 60);
        win.push();
        win.translate(0, 0, h);
        win.box(w, w, h);
        win.noStroke();
        win.translate(-(w) / 2.0f, 0);
        win.sphere(h);
        win.translate((w), 0);
        win.sphere(h);
        win.translate(-(w) / 2.0f, (w) / 2.0f);
        win.sphere(h);
        win.translate(0, -(w));
        win.sphere(h);
        win.pop();
    }

    public Vertex get() {
        return pos;
    }


}

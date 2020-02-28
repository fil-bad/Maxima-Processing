package robots;

import geometry.Vertex;
import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import processing.core.PShape;
import processingElement.CommonDraw;
import processingElement.Obj3D;

import java.util.LinkedList;

public class Rover implements Obj3D {
    Vertex pos, obj;
    SimpleMatrix A, B, xnew, x, dx;

    LinkedList<Vertex> checkPoint = new LinkedList<>();

    CommonDraw com = CommonDraw.getInstance();

    PApplet win;
    double rho, ka;
    double kp, ki, kd;

    PShape[] omniWheel = new PShape[4];

    //Dimensioni del rover
    int w = 40;
    int h = 10;
    float whellDeep;

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
        x.set(2, 0, pos.getX());
        x.set(3, 0, pos.getY());
        xnew.set(x);

        dx = new SimpleMatrix(4, 1);


        this.win = win;
        this.rho = rho;
        this.ka = ka;
        this.kp = kp;

        float size;
        for (int i = 0; i < omniWheel.length; i++) {
            omniWheel[i] = win.loadShape("rover/omniWheel.obj");
            size = Math.max(Math.max(omniWheel[i].getHeight(), omniWheel[i].getWidth()), omniWheel[i].getDepth());
            omniWheel[i].scale(2 * h / size);
            switch (i) {
                case 0:     //<--
                    omniWheel[i].rotateY(-win.PI / 2.0f);
                    omniWheel[i].translate(-w / 2.0f, 0, 0);
                    break;
                case 1:     //-->
                    omniWheel[i].rotateY(-win.PI / 2.0f);
                    omniWheel[i].translate(+w / 2.0f, 0, 0);
                    break;
                case 2:     //Up
                    omniWheel[i].rotateX(win.PI / 2.0f);
                    omniWheel[i].translate(0, +w / 2.0f, 0);
                    break;
                case 3:     //Down
                    omniWheel[i].rotateX(win.PI / 2.0f);
                    omniWheel[i].translate(0, -w / 2.0f, 0);
                    break;
            }
            omniWheel[i].setVisible(true);
        }
        whellDeep = Math.min(Math.min(omniWheel[0].getHeight(), omniWheel[0].getWidth()), omniWheel[0].getDepth());

    }

    public Rover(PApplet win, double rho, double ka, double kp) {
        this(win, new Vertex(0, 0), rho, ka, kp);
    }

    @Override
    public void draw() {
        ctrlStep();

        win.push();

        //Move the level
        win.translate((float) pos.getX(), (float) pos.getY());
        roverDraw();

        win.pop();
    }

    @Override
    public void highlight(boolean b) {

    }

    @Override
    public void highlight() {

    }

    @Override
    public SimpleMatrix getD() {
        SimpleMatrix attach = new SimpleMatrix(3, 1);
        attach.set(0, 0, pos.getX());
        attach.set(1, 0, pos.getY());
        attach.set(2, 0, 3 * h / 2.0);
        return attach;
    }

    @Override
    public double getR() {
        return 0;
    }

    @Override
    public void setD(SimpleMatrix m) {
        setD((float) m.get(0), (float) m.get(1), (float) m.get(2));

    }

    @Override
    public void setD(float x, float y, float z) {
        pos.set(x, y);
    }

    @Override
    public void addD(SimpleMatrix m) {

    }

    @Override
    public void addD(float x, float y, float z) {

    }

    @Override
    public void setR(double rad) {

    }

    @Override
    public void addR(double rad) {

    }

    private void ctrlStep() {
        x.set(xnew);

        SimpleMatrix er = new SimpleMatrix(4, 1);
        er.set(2, 0, pos.getX() - obj.getX());
        er.set(3, 0, pos.getY() - obj.getY());

        er = er.scale(-kp);

        SimpleMatrix u = new SimpleMatrix(2, 1);
        u.set(0, 0, er.get(2, 0));
        u.set(1, 0, er.get(3, 0));

        //Uso un controllo proporzionale, quindi moltiplico l'errore per kp
//        System.out.println("Stato");
//        x.print();
//        System.out.println("errore");
//        er.print();
//        System.out.println("controllo");
//        u.print();
        //Cambio obiettivo prima di fermarsi
        if (u.normF() <= 1) {
            if (!checkPoint.isEmpty()) {
                obj = checkPoint.pollFirst();
            }
        }
        if (u.normF() > 1)
            u = u.divide(u.normF());

        // Errore troppo piccolo, smetto di fare l'update
        if (u.normF() < 0.0001) {
            return;
        }
        dx.set(A.mult(x).plus(B.mult(u)));  // la uso per far ruotare le ruote
        xnew = x.plus(dx);

        pos.set(xnew.get(2, 0), xnew.get(3, 0));
    }

    public void setObj(Vertex obj) {
        if (obj != null)
            this.obj.set(obj);
    }

    public void setObjs(Vertex[] objs) {
        if (objs == null) {
            if (checkPoint.isEmpty())
                setObj(this.get());
            return;
        }
        for (Vertex v : objs) {
            checkPoint.addLast(v);
        }
    }

    public void clearObjs() {
        checkPoint.clear();
        setObj(get());
    }


    private void roverDraw() {
        //Draw the robot
        win.fill(0, 200, 60);
        win.push();
        win.translate(0, 0, h);
        win.box(w - whellDeep * 1.2f, w - whellDeep * 1.2f, h);


        omniWheel[0].rotateX((float) dx.get(3, 0) / h);
        omniWheel[1].rotateX((float) dx.get(3, 0) / h);
        omniWheel[2].rotateY((float) dx.get(2, 0) / h);
        omniWheel[3].rotateY((float) dx.get(2, 0) / h);

        for (PShape wheel : omniWheel) {
            win.shape(wheel);
        }
//        win.noStroke();
//        win.translate(-(w) / 2.0f, 0);
//        win.sphere(h);
//        win.translate((w), 0);
//        win.sphere(h);
//        win.translate(-(w) / 2.0f, (w) / 2.0f);
//        win.sphere(h);
//        win.translate(0, -(w));
//        win.sphere(h);
        win.pop();
    }

    public Vertex get() {
        return pos;
    }


    public double getRatius() {
        return Math.sqrt(2 * w) / 2.0;      // Math.sqrt(2*w) = diagonale quadrato
    }


}

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
    protected static final double EPSer = 0.001;

    PShape[] omniWheel = new PShape[4];

    //Dimensioni del rover
    int w = 40;
    int h = 10;
    float whellDeep;
    double distChange;

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

        x = new SimpleMatrix(4, 1);
        x.set(2, 0, pos.getX());
        x.set(3, 0, pos.getY());
        xnew = x.copy();

        dx = new SimpleMatrix(4, 1);


        this.win = win;
        this.rho = rho;
        this.ka = ka;
        this.kp = kp;
        this.distChange = getRadius();

        float size;
        for (int i = 0; i < omniWheel.length; i++) {
            omniWheel[i] = win.loadShape("rover/omniWheel.obj");
            size = Math.max(Math.max(omniWheel[i].getHeight(), omniWheel[i].getWidth()), omniWheel[i].getDepth());
            omniWheel[i].scale(2 * h / size);
            switch (i) {
                case 0:     //<--
                    omniWheel[i].rotateY(win.PI / 2.0f);
                    omniWheel[i].translate(-w / 2.0f, 0, 0);
                    break;
                case 1:     //-->
                    omniWheel[i].rotateY(win.PI / 2.0f);
                    omniWheel[i].translate(+w / 2.0f, 0, 0);
                    break;
                case 2:     //Up
                    omniWheel[i].rotateX(-win.PI / 2.0f);
                    omniWheel[i].translate(0, +w / 2.0f, 0);
                    break;
                case 3:     //Down
                    omniWheel[i].rotateX(-win.PI / 2.0f);
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

    private void roverDraw() {
        //Draw the robot
        win.fill(0, 200, 60);
        win.push();
        win.translate(0, 0, h);
        win.box(w - whellDeep * 1.2f, w - whellDeep * 1.2f, h);


        omniWheel[0].rotateX((float) -x.get(1, 0) / h);
        omniWheel[1].rotateX((float) -x.get(1, 0) / h);
        omniWheel[2].rotateY((float) x.get(0, 0) / h);
        omniWheel[3].rotateY((float) x.get(0, 0) / h);

        for (PShape wheel : omniWheel) {
            win.shape(wheel);
        }
        win.pop();
    }

    private void ctrlStep() {
        x.set(xnew);

        SimpleMatrix er = new SimpleMatrix(2, 1);
        er.set(0, 0, obj.getX() - pos.getX());
        er.set(1, 0, obj.getY() - pos.getY());

        //Uso un controllo proporzionale, quindi moltiplico l'errore per kp
        SimpleMatrix u = er.scale(kp);

        //Cambio obiettivo prima di fermarsi o se comunque sono sufficentemente vicino
        if (pos.minus(obj).len() <= distChange || u.normF() <= 1) {
            if (!checkPoint.isEmpty()) {
                obj = checkPoint.pollFirst();
            }
        }

        double maxU = Math.max(Math.abs(u.get(0)), Math.abs(u.get(1)));
        if (maxU > 1) {
            u = u.divide(u.normF());
        }

        // Errore troppo piccolo, smetto di fare l'update
        if (u.normF() < EPSer) {
            return;
        }
        dx.set(A.mult(x).plus(B.mult(u)));  // la uso per far ruotare le ruote
        xnew = x.plus(dx);

        pos.set(xnew.get(2, 0), xnew.get(3, 0));
    }

    public void setCheckPoint(Vertex obj) {
        if (obj != null)
            this.obj.set(obj);
    }

    public void setCheckPoint(Vertex[] objs) {
        if (objs == null) {
            if (checkPoint.isEmpty())
                setCheckPoint(this.get());
            return;
        }
        for (Vertex v : objs) {
            checkPoint.addLast(v);
        }
    }

    public void clearCheckPoint() {
        checkPoint.clear();
        setCheckPoint(get());
    }

    public Vertex get() {
        return pos;
    }

    public double getRadius() {
        return Math.sqrt(2) * w / 2.0;      // diagonale quadrato/2 = raggio cerchio circoscritto
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
    public void setD(SimpleMatrix m) {
        setD((float) m.get(0), (float) m.get(1), (float) m.get(2));

    }

    @Override
    public double getR() {
        return 0;
    }

    @Override
    public void setR(double rad) {

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
    public void addR(double rad) {

    }


}

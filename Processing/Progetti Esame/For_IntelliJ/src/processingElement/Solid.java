package processingElement;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;

public abstract class Solid implements Obj3D {
    // Coordinate origine del solido
    SimpleMatrix d;
    double angle = 0;
    PApplet win;

    public Solid(PApplet win) {
        this.d = new SimpleMatrix(3, 1);
        this.win = win;
    }

    protected void applyCoord() {
        win.translate((float) d.get(0), (float) d.get(1), (float) d.get(2));
        win.rotateZ((float) angle);
    }

    @Override
    public SimpleMatrix getD() {
        return d;
    }

    @Override
    public double getR() {
        return angle;
    }

    @Override
    public void setD(SimpleMatrix m) {
        d.set(m);
    }

    public void setD(float x, float y, float z) {
        SimpleMatrix m = new SimpleMatrix(3,1);
        m.set(0,0,x);
        m.set(1,0,y);
        m.set(2,0,z);
        this.setD(m);
    }

    @Override
    public void setR(double rad) {
        this.angle = rad;
    }
}

package processingElement;
import processing.core.PApplet;

import org.ejml.simple.SimpleMatrix;

public class Box implements Obj3D, Obstacle {
    private int w,l,h;
    private PApplet win;
    private CommonDraw common = CommonDraw.getInstance();
    public Box (PApplet win, int w, int l, int h) {
        this.w = w;
        this.l = l;
        this.h = h;
        this.win = win;
    }

    @Override
    public void draw() {
        //pushMatrix();
        //translate(w/2.0f,l/2.0f,h/2.0f);
        win.box(w,l,h);
        //common.assi(100);
        //popMatrix();
    }

    @Override
    public SimpleMatrix getD() {
        return null;
    }

    @Override
    public SimpleMatrix getR() {
        return null;
    }

    @Override
    public void setD(SimpleMatrix m) {

    }

    @Override
    public void setR(SimpleMatrix m) {

    }

    @Override
    public void highlight() {

    }

    @Override
    public void getPoly() {

    }
}

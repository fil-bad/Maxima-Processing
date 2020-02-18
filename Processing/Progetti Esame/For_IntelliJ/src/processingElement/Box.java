package processingElement;
import processing.core.PApplet;

import org.ejml.simple.SimpleMatrix;

public class Box implements Obj3D, Obstacle {
    private PApplet win;
    private CommonDraw com = CommonDraw.getInstance();

    private int w,l,h;
    private int color;
    public Box (PApplet win, int w, int l, int h, int color) {
        this.w = w;
        this.l = l;
        this.h = h;
        this.win = win;
        this.color = color;
    }

    @Override
    public void draw() {
        win.pushMatrix();
        win.pushStyle();

        win.translate(w/2.0f,l/2.0f,h/2.0f);
        win.fill(color);
        win.box(w,l,h);
        com.assi(100);
        win.translate(0,0,h);
        com.assi(100);

        win.popStyle();
        win.popMatrix();
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

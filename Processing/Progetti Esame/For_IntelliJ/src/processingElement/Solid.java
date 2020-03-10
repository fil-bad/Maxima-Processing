package processingElement;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;

public abstract class Solid implements Obstacle, Subject {
    // Coordinate origine del solido
    SimpleMatrix d, traslC;      //d= pos basso, traslC= pos rel per connessione

    double angle = 0;
    PApplet win;

    Observer myScene = null;


    public Solid(PApplet win) {
        this.d = new SimpleMatrix(3, 1);
        this.traslC = new SimpleMatrix(3, 1);
        this.win = win;
    }

    protected void applyCoord() {
        win.translate((float) d.get(0), (float) d.get(1), (float) d.get(2));
        win.rotateZ((float) angle);
    }

    @Override
    public SimpleMatrix getD() {
        return d.plus(traslC);
    }

    @Override
    public void setD(SimpleMatrix m) {
        d.set(m);
        notifyChange();
    }

    @Override
    public double getR() {
        return angle;
    }

    @Override
    public void setR(double rad) {
        this.angle = rad;
        notifyChange();
    }

    @Override
    public void setD(float x, float y, float z) {
        SimpleMatrix m = new SimpleMatrix(3, 1);
        m.set(0, 0, x);
        m.set(1, 0, y);
        m.set(2, 0, z);
        this.setD(m);
    }

    @Override
    public void addD(SimpleMatrix m) {
        d.plus(m);
        notifyChange();
    }

    @Override
    public void addD(float x, float y, float z) {
        SimpleMatrix m = new SimpleMatrix(3, 1);
        m.set(0, 0, x);
        m.set(1, 0, y);
        m.set(2, 0, z);
        this.addD(m);
    }

    @Override
    public void addR(double rad) {
        this.angle += rad;
        notifyChange();
    }

    @Override
    public void attachScene(Observer s) {
        myScene = s;
    }

    @Override
    public void notifyChange() {
        if (myScene != null)
            myScene.updateChange();
    }

}

package robots;

import processing.core.PApplet;
import processingElement.CommonDraw;
import robots.DH.DenHart;
import robots.DH.math.DoubleReal;
import robots.DH.math.autodiff.Variable;

public abstract class Robot {
    protected DenHart dhTab;
    protected CommonDraw com = CommonDraw.getInstance();
    protected PApplet win = null;

    public abstract void draw();

    public abstract void inverse(double x, double y, double z, double theta);

    public void set(String name, double qVal) {
        dhTab.getDHVar().setVars(name, qVal);
    }

    public void set(int index, double qVal) {
        dhTab.getDHVar().setVars(index, qVal);
    }

    public void set(double... qVals) {
        dhTab.getDHVar().setVars(qVals);
    }

    public void add(String name, double qVal) {
        dhTab.getDHVar().setVars(name, get(name) + qVal);
    }

    public void add(int index, double qVal) {
        dhTab.getDHVar().setVars(index, get(index) + qVal);
    }

    public void add(double... qVals) {
        for (int i = 0; i < qVals.length; i++) {
            add(i, qVals[i]);
        }
    }

    public double get(String name) {
        return dhTab.getDHVar().getVar(name).getValue().doubleValue();
    }

    public double get(int index) {
        return dhTab.getDHVar().getVar(index).getValue().doubleValue();
    }

    public double[] get() {
        Variable<DoubleReal>[] v = dhTab.getDHVar().getVar();
        double[] vRet = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            Variable<DoubleReal> var = v[i];
            vRet[i] = var.getValue().doubleValue();
        }
        return vRet;
    }
}

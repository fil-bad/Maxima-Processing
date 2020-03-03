package robots.DH.Links;

import processing.core.PApplet;
import processingElement.CommonDraw;
import robots.DH.MatrixQ;
import robots.DH.RobVars;
import robots.DH.math.DoubleReal;
import robots.DH.math.autodiff.Variable;

public abstract class GenericLink implements Link {

    protected String Theta, D;
    protected double theta, d, alpha, a;

    protected MatrixQ Q0_1;

    protected CommonDraw com = CommonDraw.getInstance();
    protected float sqB;  // lato del quadrato del connettore
    //Parametri per il disegno
    PApplet win;


    protected GenericLink(PApplet win) {
        sqB = 10;   // valore di default
        this.win = win;

    }

    public abstract void draw();


    @Override
    public MatrixQ getQLink() {
        return this.Q0_1;
    }

    @Override
    public Variable<DoubleReal> getVar() {
        return this.Q0_1.getRobVars().getVar(0);
    }

    @Override
    public RobVars whichQ_iIs() {
        return this.Q0_1.getRobVars();
    }

    @Override
    public void printLink() {
        System.out.printf("[%s  %s  %.3f  %.3f]", this.Theta, this.D, this.alpha, this.a);
    }

    @Override
    public void printValLink() {
        if (!Theta.isBlank() && D.isBlank())
            System.out.printf("[%.3f  %.3f  %.3f  %.3f]", getVar().getValue().doubleValue(), this.d, this.alpha, this.a);
        else if (Theta.isBlank() && !D.isBlank())
            System.out.printf("[%.3f  %.3f  %.3f  %.3f]", this.theta, getVar().getValue().doubleValue(), this.alpha, this.a);
        else
            System.out.printf("[%.3f  %.3f  %.3f  %.3f]", this.theta, this.d, this.alpha, this.a);

    }

    @Override
    public float getRadius() {
        return (float) (sqB / 2 * Math.sqrt(2));
    }
}

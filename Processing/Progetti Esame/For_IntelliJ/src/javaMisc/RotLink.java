package javaMisc;

import javaMisc.Link;
import javaMisc.math.DoubleReal;
import javaMisc.math.autodiff.Variable;

public class RotLink implements Link {

    private String theta_qi;
    private double d;
    private double alpha;
    private double a;

    private MatrixQ Q0_1;


    public RotLink(String qi, double d, double alpha, double a) {
        this.theta_qi = qi;
        this.d = d;
        this.alpha = alpha;
        this.a = a;

        MatrixQ avvZ = new MatrixQ().setRotZ(qi, 0).mul(new MatrixQ().setTraslZ("", d));
        MatrixQ avvX = new MatrixQ().setRotX("", alpha).mul(new MatrixQ().setTraslX("", a));
        this.Q0_1 = avvZ.mul(avvX);
    }

    @Override
    public MatrixQ getQLink() {
        return this.Q0_1;
    }

    @Override
    public Variable<DoubleReal> getVar() {
        return this.Q0_1.getVars().get(0);
    }

    @Override
    public String whichQ_iIs() {
        return this.theta_qi;
    }

    @Override
    public void printLink() {
        System.out.printf("[%s  %.3f  %.3f  %.3f]\n", this.theta_qi, this.d, this.alpha, this.a);
    }

}

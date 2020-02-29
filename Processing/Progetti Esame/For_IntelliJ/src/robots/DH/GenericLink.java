package robots.DH;

import robots.DH.math.DoubleReal;
import robots.DH.math.autodiff.DifferentialFunction;
import robots.DH.math.autodiff.Variable;

public class GenericLink implements Link {

    private String d_qi;
    private String theta;
    private double alpha;
    private double a;

    private MatrixQ Q0_1;


    public GenericLink(String theta, String qi, double alpha, double a) {

        this.theta = theta;
        this.d_qi = qi;
        this.alpha = alpha;
        this.a = a;

//        MatrixQ avvZ = new MatrixQ().setRotZ("", (double)theta.length()).mul(new MatrixQ().setTraslZ(qi, 0));
//        MatrixQ avvX = new MatrixQ().setRotX("", alpha).mul(new MatrixQ().setTraslX("", a));
//        this.Q0_1 = avvZ.mul(avvX);
    }


    @Override
    public MatrixQ getQLink() {
        return null;
    }

    @Override
    public Variable<DoubleReal> getVar() {
        return null;
    }

    @Override
    public String whichQ_iIs() {
        return null;
    }

    @Override
    public void printLink() {

    }

    @Override
    public void printValLink() {

    }
}

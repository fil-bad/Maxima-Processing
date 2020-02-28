package javaMisc;

import javaMisc.Link;

import static java.lang.Math.PI;


public class PrismLink implements Link {


    private String d_qi;
    private double theta;
    private double alpha;
    private double a;

    private MatrixQ Q0_1;

    public PrismLink(double theta, String qi, double alpha, double a) {

        this.theta = theta;
        this.d_qi = qi;
        this.alpha = alpha;
        this.a = a;

        MatrixQ avvZ = new MatrixQ().setRotZ("", theta).mul(new MatrixQ().setTraslZ(qi, 0));
        MatrixQ avvX = new MatrixQ().setRotX("", alpha).mul(new MatrixQ().setTraslX("", a));
        this.Q0_1 = avvZ.mul(avvX);
    }

    @Override
    public MatrixQ getQLink() {
        return this.Q0_1;
    }

    @Override
    public void printLink() {
        System.out.printf("[%.3f  %s  %.3f  %.3f]\n", this.theta, this.d_qi, this.alpha, this.a);
    }

    @Override
    public String whichQ_iIs() {
        return this.d_qi;
    }


    public static void main(String[] args) {
        Link l = new PrismLink(PI, "q1", PI / 2, 12);
        l.printLink();

    }

}

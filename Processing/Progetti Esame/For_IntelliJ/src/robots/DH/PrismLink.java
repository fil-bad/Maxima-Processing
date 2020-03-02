package robots.DH;

import robots.DH.math.DoubleReal;
import robots.DH.math.autodiff.Variable;

import static java.lang.Math.PI;


public class PrismLink extends GenericLink {


    public PrismLink(double theta, String qi, double alpha, double a) {
        super();
        this.Theta = "";
        this.D = qi;
        this.theta = theta;
        this.d = 0;
        this.alpha = alpha;
        this.a = a;

        this.Q0_1 = new MatrixQ().setAvvZ(qi, theta, AvvType.TslVariable).mul(new MatrixQ().setAvvX(alpha, a));

    }

    public PrismLink(double theta, String qi, double alpha, double a, float l) {
        this(theta, qi, alpha, a);
        connectWith = l;
    }

    @Override
    public void draw() {

    }

}

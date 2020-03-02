package robots.DH;

import robots.DH.math.DoubleReal;
import robots.DH.math.autodiff.Variable;

public class RotLink extends GenericLink {


    public RotLink(String qi, double d, double alpha, double a) {
        super();
        this.Theta = qi;
        D = "";
        this.theta = 0;
        this.d = d;
        this.alpha = alpha;
        this.a = a;

        this.Q0_1 = new MatrixQ().setAvvZ(qi, d, AvvType.RotVariable).mul(new MatrixQ().setAvvX(alpha, a));

    }

    public RotLink(String qi, double d, double alpha, double a, float l) {
        this(qi, d, alpha, a);
        connectWith = l;
    }

    @Override
    public void draw() {

    }


}

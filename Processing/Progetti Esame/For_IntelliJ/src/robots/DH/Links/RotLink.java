package robots.DH.Links;

import processing.core.PApplet;
import robots.DH.AvvType;
import robots.DH.MatrixQ;

public class RotLink extends GenericLink {


    public RotLink(PApplet win, String qi, double d, double alpha, double a) {
        super(win);
        this.Theta = qi;
        D = "";
        this.theta = 0;
        this.d = d;
        this.alpha = alpha;
        this.a = a;

        this.Q0_1 = new MatrixQ().setAvvZ(qi, d, AvvType.RotVariable).mul(new MatrixQ().setAvvX(alpha, a));

    }

    public RotLink(PApplet win, float l, String qi, double d, double alpha, double a) {
        this(win, qi, d, alpha, a);
        sqB = l;
    }

    @Override
    public void draw() {

    }


}

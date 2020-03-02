package robots.DH.Links;

import processing.core.PApplet;
import robots.DH.AvvType;
import robots.DH.MatrixQ;


public class PrismLink extends GenericLink {


    public PrismLink(PApplet win, double theta, String qi, double alpha, double a) {
        super(win);
        this.Theta = "";
        this.D = qi;
        this.theta = theta;
        this.d = 0;
        this.alpha = alpha;
        this.a = a;

        this.Q0_1 = new MatrixQ().setAvvZ(qi, theta, AvvType.TslVariable).mul(new MatrixQ().setAvvX(alpha, a));

    }

    public PrismLink(PApplet win, float l, double theta, String qi, double alpha, double a) {
        this(win, theta, qi, alpha, a);
        sqB = l;
    }

    @Override
    public void draw() {
        //la funzione suppene di essere già con l'origine ben orientata
        d = Q0_1.getRobVars().getVar(0).getValue().doubleValue();

        com.drawBoxBase((float) d * 24 / 48.0f, sqB, win.color(255, 0, 0));
        com.drawBoxBase((float) d * 23 / 48.0f, sqB * 0.9f, win.color(150, 255, 0));
        com.drawBoxBase((float) d / 48.0f, sqB, win.color(0, 255, 255));

        win.rotateZ((float) theta);
        win.translate((float) a, 0, 0);
        win.rotateX((float) alpha);
    }

}

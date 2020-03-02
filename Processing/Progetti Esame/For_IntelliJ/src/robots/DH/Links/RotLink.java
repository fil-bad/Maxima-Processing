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
        //todo: Ste rotazioni non vanno sicuramente bene
        theta = Q0_1.getRobVars().getVar(0).getValue().doubleValue();
        com.axes(255);

        win.rotateY(-(float) Math.PI / 2.0f);
        win.rotateZ((float) theta);
        float planeH = com.drawCylinder(16, (float) sqB, sqB, false);
        win.rotateY((float) Math.PI / 2.0f);

        //Escamotage perchè drawBoxBase cresce lungo la Z locale
        com.drawBoxBase((float) (d - planeH) * 47 / 48.0f, sqB * 0.9f, win.color(255, 0, 0));
        com.drawBoxBase((float) (d - planeH) / 48.0f, sqB, win.color(0, 255, 255));

        win.rotateX((float) alpha);
        win.rotateY(-(float) Math.PI / 2.0f);
        com.drawBoxBase((float) a * 47 / 48.0f, sqB * 0.9f, win.color(255, 150, 150));
        com.drawBoxBase((float) a / 48.0f, sqB, win.color(0, 255, 255));
        win.rotateY((float) Math.PI / 2.0f);

        com.axes(255);

    }


}

package robots.DH.Links;

import processing.core.PApplet;

public class CoaxialLink extends RotLink {


    public CoaxialLink(PApplet win, float l, String Theta, double d) {    ////Rotoidale Pivot
        super(win, Theta, d, 0, 0);
        sqB = l;
    }

    @Override
    public void draw() {
        theta = Q0_1.getRobVars().getVar(0).getValue().doubleValue();

        com.drawBoxBase((float) d * 23 / 48.0f, sqB, win.color(255, 0, 255));
        com.drawBoxBase((float) d / 48.0f, sqB, win.color(255, 0, 255));
        win.rotateZ((float) theta);
        com.drawBoxBase((float) d / 48.0f, sqB, win.color(150, 255, 0));
        com.drawBoxBase((float) d * 22 / 48.0f, sqB * 0.9f, win.color(150, 255, 0));
        com.drawBoxBase((float) d / 48.0f, sqB, win.color(0, 255, 255));

        win.translate((float) a, 0, 0);
        win.rotateX((float) alpha);

    }

}

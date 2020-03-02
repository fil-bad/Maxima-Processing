package robots.DH.Links;

import processing.core.PApplet;

public class CoaxialLink extends RotLink {


    CoaxialLink(PApplet win, float l, String Theta, double d) {    ////Rotoidale Pivot
        super(win, Theta, d, 0, 0);
        sqB = l;
    }

    @Override
    public void draw() {

    }
}

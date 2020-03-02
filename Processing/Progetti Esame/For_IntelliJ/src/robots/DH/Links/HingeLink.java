package robots.DH.Links;

import processing.core.PApplet;

public class HingeLink extends RotLink {


    HingeLink(PApplet win, float l, String Theta, double a) {  //Rotoidale a cerniera
        super(win, Theta, 0, 0, a);
        sqB = l;

    }

    @Override
    public void draw() {

    }
}

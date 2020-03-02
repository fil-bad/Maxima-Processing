package robots.DH;

public class CoaxialLink extends RotLink {


    CoaxialLink(String Theta, double d, float l) {    ////Rotoidale Pivot
        super(Theta, d, 0, 0);
        connectWith = l;
    }

    @Override
    public void draw() {

    }
}

package robots.DH;

public class HingeLink extends RotLink {


    HingeLink(String Theta, double a, float l) {  //Rotoidale a cerniera
        super(Theta, 0, 0, a);
        connectWith = l;

    }

    @Override
    public void draw() {

    }
}

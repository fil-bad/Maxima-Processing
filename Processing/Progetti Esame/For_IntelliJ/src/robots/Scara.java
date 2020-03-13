package robots;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import robots.DH.DenHart;
import robots.DH.Links.PrismLink;
import robots.DH.Links.RotLink;

public class Scara extends Robot {

    public Scara(PApplet win, float b) {
        dhTab = new DenHart(win);
        this.win = win;
        dhTab.addLinkStrut(new RotLink(win, b, "q1", 100, 0, 50));
        dhTab.addLinkStrut(new RotLink(win, b, "q2", 0, Math.PI, 50));
        dhTab.addLinkStrut(new PrismLink(win, b, 0, "q3", 0, 0));

        dhTab.addLinkOri(new RotLink(win, b, "q4", 30, 0, 0));

        dhTab.getDHVar().setVars(0, 0, 0, 0);

        super.setCtrl(0.2, 0.05, 0.2);
    }

    @Override
    public SimpleMatrix inverse(double x, double y, double z, double theta) {
        SimpleMatrix Kep, Keo;
        double lambda = 1 / 5000.0;
        double gamma = 1 / 100.0;
        Kep = SimpleMatrix.identity(dhTab.getStrutVar().varSize()).scale(lambda);
        Keo = SimpleMatrix.identity(dhTab.getOriVar().varSize()).scale(gamma);
        SimpleMatrix ret = null;

        try {
            ret = super.inverse(10000, x, y, z, theta, Kep, Keo);
        } catch (Exception e) {
            if (e.getMessage().equals("EndWork")) {
                if (ret == null) {
                    System.out.println("Just in solution");
                    return dhTab.getDHVar().get_qVect();
                } else {
                    System.out.println("Scara inverse found");
                    return ret;
                }
            } else {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }
        }
        return ret;
    }


}

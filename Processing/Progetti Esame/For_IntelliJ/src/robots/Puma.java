package robots;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import robots.DH.DenHart;
import robots.DH.Links.RotLink;

public class Puma extends Robot {

    public Puma(PApplet win, float b) {
        dhTab = new DenHart(win);
        this.win = win;
        dhTab.addLinkStrut(new RotLink(win, b, "q1", 50, Math.PI / 2.0, 0));
        dhTab.addLinkStrut(new RotLink(win, b, "q2", 0, 0, 100));
        dhTab.addLinkStrut(new RotLink(win, b, "q3", 0, 0, 60));

        dhTab.addLinkOri(new RotLink(win, b, "q4", 0, -Math.PI / 2.0, 0));
        dhTab.addLinkOri(new RotLink(win, b, "q5", 0, Math.PI / 2.0, 0));
        dhTab.addLinkOri(new RotLink(win, b, "q6", 30, 0, 0));
        dhTab.getDHVar().setVars(0, 0, 0, 0.1, 0.2, 0);

        super.setCtrl(0.2, 0.05, 0.2);
    }

    @Override
    public SimpleMatrix inverse(double x, double y, double z, double theta) {
        SimpleMatrix Kep, Keo;
        double lambda = 1 / 20000.0;
        double gamma = 1 / 100.0;
        Kep = SimpleMatrix.identity(dhTab.getStrutVar().varSize()).scale(lambda);
        Keo = SimpleMatrix.identity(dhTab.getOriVar().varSize()).scale(gamma);
        SimpleMatrix ret = null;

//        SimpleMatrix ret = super.inverse(10000, x, y, z, theta, Kep, Keo);
//        ret = super.inverse(10, x, y, z, theta, Kep, Keo);
//
        SimpleMatrix originalQ = dhTab.getDHVar().get_qVect();
        for (int i = 0; i < 10000 / 10; i++) {
            try {
                ret = super.inverse(10, x, y, z, theta, Kep, Keo);
                dhTab.getDHVar().setVars(ret);
            } catch (Exception e) {
                if (e.getMessage().equals("EndWork")) {
                    System.out.println("Soluzione trovata");
                    if (ret == null)
                        return dhTab.getDHVar().get_qVect();
                    else {
                        dhTab.getDHVar().setVars(originalQ);
                        return ret;
                    }
                } else {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                    System.exit(-1);
                }
            }


            System.out.println("i = " + i);
        }
        dhTab.getDHVar().setVars(originalQ);
        return ret;
    }


}

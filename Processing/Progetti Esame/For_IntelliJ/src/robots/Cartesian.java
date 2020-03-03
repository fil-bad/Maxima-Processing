package robots;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import robots.DH.DenHart;
import robots.DH.Links.PrismLink;
import robots.DH.Links.RotLink;


public class Cartesian extends Robot {

    public Cartesian(PApplet win, float b) {
        dhTab = new DenHart(win);
        dhTab.addLink(new PrismLink(win, b, 0, "q1", -Math.PI / 2.0, 0));
        dhTab.addLink(new PrismLink(win, b, -Math.PI / 2.0, "q2", -Math.PI / 2.0, 0));
        dhTab.addLink(new PrismLink(win, b, 0, "q3", 0, 0));
        dhTab.addLink(new RotLink(win, b, "q4", 0, -Math.PI / 2.0, 0));
        dhTab.addLink(new RotLink(win, b, "q5", 0, Math.PI / 2.0, 0));
        dhTab.addLink(new RotLink(win, b, "q6", 50, 0, 0));
    }

    public void draw() {
        dhTab.draw();
    }

    @Override
    public void inverse(double x, double y, double z, double theta) {

        // Matrici di peso dell'errore
        SimpleMatrix Ke;
        double lambda = 1 / 25.0;
        double gamma = 1 / 3000.0;
        Ke = new SimpleMatrix(6, 6);

        Ke.set(0, 0, lambda);
        Ke.set(1, 1, lambda);
        Ke.set(2, 2, lambda);
        Ke.set(3, 3, gamma);
        Ke.set(4, 4, gamma);
        Ke.set(5, 5, gamma);

        SimpleMatrix ep, eo;
        ep = new SimpleMatrix(3, 1);
        ep.set(0, x);
        ep.set(1, y);
        ep.set(2, z);
        ep = ep.minus(dhTab.getD());
        ep.print();

        // Errore orientamento mediante terna ZYZ di eulero (polso sferico)
//        SimpleMatrix rDes = new SimpleMatrix(3,3);
//        rDes.set(0,0, Math.cos(theta)); rDes.set(0,1, -Math.sin(theta));
//        rDes.set(1,0, Math.sin(theta)); rDes.set(1,1, Math.cos(theta));
//        rDes.set(1,1,-1.0); // Pinsa verso il basso sempre (da progetto

        eo = new SimpleMatrix(3, 1);
        eo.set(0, 0);
        eo.set(1, 0);
        eo.set(2, 0);

        SimpleMatrix qCap, qJ, qCapNew, Jp;
        qCap = dhTab.getDHVar().get_qVect();
        Jp = dhTab.getJ();
        qJ = Ke.mult(Jp.transpose().mult(ep));

        //Necessario calcolare ancora Joa ( jacobbiano dell'orientamento)
//        SimpleMatrix error = Kp.mult(ep).concatRows(Ko.mult(eo));
//        error.print();
//        Jp.print();
//        qJ = Jp.transpose().mult(error);


        qCapNew = qCap.plus(qJ);
        dhTab.getDHVar().setVars(qCapNew);

        System.out.println();

    }
}

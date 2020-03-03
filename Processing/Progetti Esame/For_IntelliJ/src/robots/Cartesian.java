package robots;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import robots.DH.DenHart;
import robots.DH.Links.CoaxialLink;
import robots.DH.Links.PrismLink;
import robots.DH.Links.RotLink;

import java.util.Arrays;

public class Cartesian extends Robot {

    public Cartesian(PApplet win, float b) {
        dhTab = new DenHart(win);
        dhTab.addLink(new PrismLink(win, b, 0, "q1", -Math.PI / 2.0, 0));
        dhTab.addLink(new PrismLink(win, b, -Math.PI / 2.0, "q2", -Math.PI / 2.0, 0));
//        dhTab.addLink(new PrismLink(win, b, 0, "q3", 0, 0));
//        dhTab.addLink(new RotLink(win, b, "q4", 0, -Math.PI / 2.0, 0));
//        dhTab.addLink(new RotLink(win, b, "q5", 0, Math.PI / 2.0, 0));
//        dhTab.addLink(new RotLink(win, b, "q6", 50, 0, 0));
    }

    public void draw() {
//        com.axes(255);
        dhTab.draw();
    }

    @Override
    public void inverse(double x, double y, double z, double theta) {


        double lambda = 1 / 50.0;
        SimpleMatrix qCap, qCapNew, J, P, pCap;
        qCap = dhTab.getDHVar().get_qVect();
//        dhTab.getDHVar().printVarName();
        P = new SimpleMatrix(3, 1);
        P.set(0, x);
        P.set(1, y);
        P.set(2, z);
        pCap = dhTab.getD();
        J = dhTab.getJ();


//        System.out.println("Q");
//        dhTab.getQ().print();
//
//        System.out.println("P");
//        P.print();
//        System.out.println("pCap");
//        pCap.print();
//
//        System.out.println("P-pCap");
//        P.minus(pCap).print();
//
//
//
        System.out.println("J");
        J.print();
//        System.out.println("Jt");
//        J.transpose().print();
//
//        System.out.println("J*lambda * (p-pcap)");
//        J.transpose().scale(lambda).mult(P.minus(pCap)).print();


        qCapNew = qCap.plus(J.transpose().scale(lambda).mult(P.minus(pCap)));
        dhTab.getDHVar().setVars(qCapNew);

//        System.out.println("qCapNew");
//        qCapNew.print();


        System.out.println();

    }
}

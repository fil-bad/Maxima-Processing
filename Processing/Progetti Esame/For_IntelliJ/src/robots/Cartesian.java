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


        double lambda = 1 / 100.0;
        SimpleMatrix qCap, qJ, qCapNew, J, P, pCap;
        qCap = dhTab.getDHVar().get_qVect();
        P = new SimpleMatrix(3, 1);
        P.set(0, x);
        P.set(1, y);
        P.set(2, z);
        pCap = dhTab.getD();
        J = dhTab.getJ();

        qJ = J.transpose().scale(lambda).mult(P.minus(pCap));
        //Ulteriore scala per il passo di incremento dei giunti rotoidali
        qJ.set(3, qJ.get(3) * lambda);
        qJ.set(4, qJ.get(4) * lambda);
        qJ.set(5, qJ.get(5) * lambda);

        qCapNew = qCap.plus(qJ);
        dhTab.getDHVar().setVars(qCapNew);

//        System.out.println("Q");
//        dhTab.getQ().print();
//
//        System.out.println("P");
//        P.print();
        System.out.println("pCap");
        pCap.print();
//
//        System.out.println("P-pCap");
//        P.minus(pCap).print();
//
//        System.out.println("J");
//        J.print();
//        System.out.println("Jt");
//        J.transpose().print();
//
//        System.out.println("J*lambda * (p-pcap)");
//        qJ.print();
//
//        System.out.println("qCapNew");
//        qCapNew.print();
//
        System.out.println();

    }
}

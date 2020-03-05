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
//        SimpleMatrix Ke, Kep, Keo;
//        double lambda = 1 / 10.0;
//        double gamma = 1 / 3000.0;
//        Kep = SimpleMatrix.identity(3).scale(lambda);
//        Keo = SimpleMatrix.identity(3).scale(gamma);
//        Ke = new SimpleMatrix(6, 6);
//        Ke.insertIntoThis(0, 0, Kep);
//        Ke.insertIntoThis(3, 3, Keo);
//
//        SimpleMatrix ep, eo;
//        ep = new SimpleMatrix(3, 1);
//        ep.set(0, x);
//        ep.set(1, y);
//        ep.set(2, z);
//        ep = ep.minus(dhTab.getD());
//        ep.print();
//
//        // Errore orientamento mediante terna ZYZ di eulero (polso sferico)
////        SimpleMatrix rDes = new SimpleMatrix(3,3);
////        rDes.set(0,0, Math.cos(theta)); rDes.set(0,1, -Math.sin(theta));
////        rDes.set(1,0, Math.sin(theta)); rDes.set(1,1, Math.cos(theta));
////        rDes.set(1,1,-1.0); // Pinsa verso il basso sempre (da progetto
//
//        eo = new SimpleMatrix(3, 1);
//        eo.set(0, 0);
//        eo.set(1, 0);
//        eo.set(2, 0);
//
//        SimpleMatrix qCap, qJ, qCapNew, Jp;
//        qCap = dhTab.getDHVar().get_qVect();
//        Jp = dhTab.getJ();
//        if (ep.normF() > 10.0)  //se errore "grande" uso gradiente
//            qJ = Ke.mult(Jp.transpose().mult(ep));
//        else
//            qJ = Jp.pseudoInverse().mult(ep);
//
//
//        //Necessario calcolare ancora Joa ( jacobbiano dell'orientamento)
////        SimpleMatrix error = Kp.mult(ep).concatRows(Ko.mult(eo));
////        error.print();
////        Jp.print();
////        qJ = Jp.transpose().mult(error);
//
//
//        qCapNew = qCap.plus(qJ);
//        dhTab.getDHVar().setVars(qCapNew);
//
//        System.out.println();

        /** Metodo di stima a 12 variabii **/
//         Matrici di peso dell'errore
        SimpleMatrix Ke, Kep, Keo;
        double lambda = 1 / 1000.0;
        double gamma = 1 / 3000000.0;
        Kep = SimpleMatrix.identity(3).scale(lambda);
        Keo = SimpleMatrix.identity(9).scale(gamma);
        Ke = new SimpleMatrix(12, 12);
        Ke.insertIntoThis(0, 0, Kep);
        Ke.insertIntoThis(3, 3, Keo);


        // Definizone matrice di orientamento desiderata
        SimpleMatrix rDes = new SimpleMatrix(3, 3);
        rDes.set(0, 0, Math.cos(theta));
        rDes.set(0, 1, -Math.sin(theta));
        rDes.set(1, 0, Math.sin(theta));
        rDes.set(1, 1, Math.cos(theta));
        rDes.set(1, 1, -1.0); // Pinsa verso il basso sempre (da progetto
        SimpleMatrix rDesSys = new SimpleMatrix(9, 1);
        // 3 ori X | 3 ori Y | 3 ori Z
        for (int i = 0; i < 9; i++) {
            rDesSys.set(i, rDes.get(i % 3, i / 3));
        }
        SimpleMatrix pDes;
        pDes = new SimpleMatrix(3, 1);
        pDes.set(0, x);
        pDes.set(1, y);
        pDes.set(2, z);
        SimpleMatrix e = new SimpleMatrix(12, 1);
        e.insertIntoThis(0, 0, pDes);
        e.insertIntoThis(3, 0, rDesSys);
        e = e.minus(dhTab.getQsys());

        SimpleMatrix qCap, qJ, qCapNew, Jsys;
        qCap = dhTab.getDHVar().get_qVect();
        Jsys = dhTab.getJsys();
        System.out.println("e.normF() = " + e.normF());
        if (e.normF() > 10.0)  //se errore "grande" uso gradiente
            qJ = Jsys.transpose().mult(Ke.mult(e));
        else
            qJ = Jsys.pseudoInverse().mult(Ke.mult(e));


        qCapNew = qCap.plus(qJ);
        dhTab.getDHVar().setVars(qCapNew);

        System.out.println();

    }
}

package robots;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import robots.DH.DenHart;
import robots.DH.Links.PrismLink;
import robots.DH.Links.RotLink;
import robots.DH.TriadDegs;

import static java.lang.Math.PI;


public class CartesianSplit extends Robot {

    public CartesianSplit(PApplet win, float b) {
        dhTab = new DenHart(win);
        this.win = win;
        dhTab.addLinkStrut(new PrismLink(win, b, 0, "q1", -Math.PI / 2.0, 0));
        dhTab.addLinkStrut(new PrismLink(win, b, -Math.PI / 2.0, "q2", -Math.PI / 2.0, 0));
        dhTab.addLinkStrut(new PrismLink(win, b, 0, "q3", 0, 0));
        dhTab.addLinkOri(new RotLink(win, b, "q4", 0, -Math.PI / 2.0, 0));
        dhTab.addLinkOri(new RotLink(win, b, "q5", 0, Math.PI / 2.0, 0));
        dhTab.addLinkOri(new RotLink(win, b, "q6", 50, 0, 0));
        dhTab.getDHVar().setVars(50, 50, 50, 0.1, 0.2, 0);
//        System.out.println("getJXYZ");
//        dhTab.getJXYZ().print();
//
//        System.out.println("getJYXZ");
//        dhTab.getJYXZ().print();
//
//        System.out.println("getJZYZ");
//        dhTab.getJZYZ().print();
    }

    public void draw() {
        com.axes(255);
        dhTab.draw();
    }

    int state = 0;

    @Override
    public void inverse(double x, double y, double z, double theta) {

        //   Matrici di peso dell'errore
        SimpleMatrix Ke, Kep, Keo;
        double lambda = 1 / 10.0;
        double gamma = 1 / 5.0;
        Kep = SimpleMatrix.identity(3).scale(lambda);
        Keo = SimpleMatrix.identity(3).scale(gamma);
        Ke = new SimpleMatrix(6, 6);
        Ke.insertIntoThis(0, 0, Kep);
        Ke.insertIntoThis(3, 3, Keo);


        /** Fase uno, aggiusto la posizione**/
        SimpleMatrix ep;
        ep = new SimpleMatrix(3, 1);
        ep.set(0, x);
        ep.set(1, y);
        ep.set(2, z);
        //Trovo epCap:
        ep = ep.minus(dhTab.getRStrut().mult(dhTab.getDOri())); // Trovo l'errore di posizione relativo con orientamento attuale
        ep = ep.minus(dhTab.getDStrut());    // Errore rispetto al punto terminale della struttura portante

        SimpleMatrix qCap, qJ, qCapNew, JpStrut;
        qCap = dhTab.getStrutVar().get_qVect();
        JpStrut = dhTab.getJpStrut();

        //todo, se J non vicina a singolarità gradiente
        if (ep.normF() > 10.0)  //se errore "grande" uso gradiente
            qJ = JpStrut.transpose().mult(ep);
        else
            qJ = JpStrut.pseudoInverse().mult(ep);

        qJ = Kep.mult(qJ);
        qCapNew = qCap.plus(qJ);
        dhTab.getStrutVar().setVars(qCapNew);

        /** Fase 2, aggiusto l'orientamento**/
        // Definizone matrice di orientamento desiderata
        SimpleMatrix rDes = new SimpleMatrix(3, 3);
        rDes.set(0, 0, -Math.cos(theta));
        rDes.set(0, 1, +Math.sin(theta));
        rDes.set(1, 0, Math.sin(theta));
        rDes.set(1, 1, Math.cos(theta));
        rDes.set(2, 2, -1.0); // Pinsa verso il basso sempre (da progetto

        TriadDegs best = DenHart.bestTriadOutsing(rDes, dhTab.getROri());
        SimpleMatrix eo, eoUp, eoDown;
        eoUp = DenHart.getAnglesTriad(best, rDes, true).minus(dhTab.getAnglesTriad(best, true));
        eoDown = DenHart.getAnglesTriad(best, rDes, false).minus(dhTab.getAnglesTriad(best, false));

        if (eoUp.normF() <= eoDown.normF())
            eo = eoUp;
        else
            eo = eoDown;

        for (int i = 0; i < eo.numRows(); i++)
            eo.set(i, eo.get(i) % (2 * PI));

        SimpleMatrix JOri;

        qCap = dhTab.getOriVar().get_qVect();
        JOri = dhTab.getJTriad(best);

        //todo, se J non vicina a singolarità gradiente
        if (eo.normF() > 10.0)  //se errore "grande" uso gradiente
            qJ = JOri.transpose().mult(eo);
        else
            qJ = JOri.pseudoInverse().mult(eo);

        qJ = Keo.mult(qJ);
        qCapNew = qCap.plus(qJ);

        dhTab.getOriVar().setVars(qCapNew);
    }
}

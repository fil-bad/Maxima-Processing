package robots;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import processingElement.CommonDraw;
import robots.DH.DenHart;
import robots.DH.TriadDegs;
import robots.DH.math.DoubleReal;
import robots.DH.math.autodiff.Variable;

import static java.lang.Math.PI;

public abstract class Robot {
    protected DenHart dhTab;
    protected CommonDraw com = CommonDraw.getInstance();
    protected PApplet win = null;
    protected static final double EPSer = 0.001;

    protected SimpleMatrix A, B, xnew, x;  //Must be set by son robot using for example setCtrl()
    protected double kp;

    protected SimpleMatrix qObj;

    public void draw() {
        if (qObj == null)
            qObj = dhTab.getDHVar().get_qVect();

        ctrlStep(qObj);
        dhTab.draw();
    }

    protected void setCtrl(double rho, double ka, double kp) {
        A = new SimpleMatrix(dhTab.getNumDOF() * 2, dhTab.getNumDOF() * 2);
        for (int i = 0; i < dhTab.getNumDOF(); i++) {
            A.set(i, i, -rho);              // Friction param
            A.set(i + dhTab.getNumDOF(), i, 1);   // Integrall eigenvalue
        }

        B = new SimpleMatrix(dhTab.getNumDOF() * 2, dhTab.getNumDOF());
        for (int i = 0; i < dhTab.getNumDOF(); i++) {
            B.set(i, i, ka);              // Friction param
        }
        this.kp = kp;

        x = new SimpleMatrix(A.numRows(), 1);
        x.insertIntoThis(dhTab.getNumDOF(), 0, dhTab.getDHVar().get_qVect());
        xnew = x.copy();
    }

    public void setqObj(SimpleMatrix qObj) {
        this.qObj = qObj;
    }

    protected void ctrlStep(SimpleMatrix qFin) {
        x.set(xnew);

        SimpleMatrix qCap;
        qCap = dhTab.getDHVar().get_qVect();
        SimpleMatrix er = qFin.minus(qCap);

        //Uso un controllo proporzionale, quindi moltiplico l'errore per kp
        SimpleMatrix u = er.scale(kp);

        if (u.normF() > 1) {
            u = u.divide(u.normF());
        }

        // Errore troppo piccolo, smetto di fare l'update
        if (u.normF() < EPSer) {
            return;
        }

        xnew = x.plus(A.mult(x).plus(B.mult(u)));
        qCap = xnew.rows(dhTab.getNumDOF(), xnew.numRows());
        dhTab.getDHVar().setVars(qCap);
    }


    //Metodo di interfaccia che è realizzato dai figli
    public SimpleMatrix inverse(SimpleMatrix D, double theta) {
        return inverse(D.get(0), D.get(1), D.get(2), theta);
    }

    public abstract SimpleMatrix inverse(double x, double y, double z, double theta);

    //Return a new set of q value to reach the target point and orientation, after loops iteration
    protected SimpleMatrix inverse(int loop, double x, double y, double z, double theta, SimpleMatrix Kep, SimpleMatrix Keo) {

        SimpleMatrix dhOldVar = dhTab.getDHVar().get_qVect();

        SimpleMatrix qCapNew, qCap, qJ; //Variabili di stato per sistema Tempo Discreto
        SimpleMatrix JpStrut, JOri;     //Jacobbiani per esecuzione degli algoritmi


        SimpleMatrix ep;
        ep = new SimpleMatrix(3, 1);


        // Definizone matrice di orientamento desiderata
        SimpleMatrix rDes = new SimpleMatrix(3, 3);
        rDes.set(0, 0, -Math.cos(theta));
        rDes.set(0, 1, +Math.sin(theta));
        rDes.set(1, 0, Math.sin(theta));
        rDes.set(1, 1, Math.cos(theta));
        rDes.set(2, 2, -1.0); // Pinsa verso il basso sempre (da progetto)

        for (int j = 0; j < loop; j++) {
            /** Fase uno, aggiusto la posizione**/
            //Trovo epCap:
            ep.set(0, x);
            ep.set(1, y);
            ep.set(2, z);
            ep = ep.minus(dhTab.getRStrut().mult(dhTab.getDOri())); // Traslo l'errore di posizione nel sitema di rif della punta della struttura
            ep = ep.minus(dhTab.getDStrut());    // Errore rispetto al punto terminale della struttura portante

            qCap = dhTab.getStrutVar().get_qVect();
            JpStrut = dhTab.getJpStrut();   //Aggiorno numericamente Jp in base alla posizione

            if (ep.normF() > EPSer) { //Aggiorno solo se l'errore è tangibile
                //todo, se J non vicina a singolarità gradiente
                if (ep.normF() > 10.0)  //se errore "grande" uso gradiente
                    qJ = JpStrut.transpose().mult(ep);
                else
                    qJ = JpStrut.pseudoInverse().mult(ep);

                qJ = Kep.mult(qJ);
                qCapNew = qCap.plus(qJ);
                dhTab.getStrutVar().setVars(qCapNew); // Aggiorno lo stato del robot temporaneamente
            } else
                System.out.println("Posizione Raggiunta");

            /** Fase 2, aggiusto l'orientamento**/

            qCap = dhTab.getOriVar().get_qVect();

            //Trovo la terna migliore e la soluzione con meno errore per l'orientamento
            TriadDegs best = DenHart.bestTriadOutsing(rDes, dhTab.getROri());
            SimpleMatrix eo, eoUp, eoDown;
            eoUp = DenHart.getAnglesTriad(best, rDes, true).minus(dhTab.getAnglesTriad(best, true));
            eoDown = DenHart.getAnglesTriad(best, rDes, false).minus(dhTab.getAnglesTriad(best, false));
            if (eoUp.normF() <= eoDown.normF())
                eo = eoUp;
            else
                eo = eoDown;
//            Normalizzo tutti i risultati tra -2Pi e 2Pi, per evitare numeri troppo grandi
            for (int i = 0; i < eo.numRows(); i++)
                eo.set(i, (eo.get(i) % (2 * PI)));

            JOri = dhTab.getJTriad(best); //Aggiorno numericamente JOri in base alla posizione, terna e orientamento

            if (eo.normF() > EPSer) { //Aggiorno solo se l'errore è tangibile
                qJ = JOri.transpose().mult(eo);

                //todo, se J non vicina a singolarità gradiente
//                if (eo.normF() > 10.0)  //se errore "grande" uso gradiente
//                    qJ = JOri.transpose().mult(eo);
//                else
//                    qJ = JOri.pseudoInverse().mult(eo);
                qJ = Keo.mult(qJ);
                qCapNew = qCap.plus(qJ);
                for (int i = 0; i < eo.numRows(); i++)
                    qCapNew.set(i, qCapNew.get(i) % (2 * PI));
                dhTab.getOriVar().setVars(qCapNew); // Aggiorno temporaneamente lo stato del robot
            } else
                System.out.println("Orientamento Raggiunto");

            if (ep.normF() <= EPSer && eo.normF() <= EPSer) {
                System.out.println("Finisco loop, sol trovata");
                break;
            }
        }
        SimpleMatrix ret = dhTab.getDHVar().get_qVect();
        dhTab.getDHVar().setVars(dhOldVar); // Ripristino la situazione iniziale
        return ret;
    }

    public void set(String name, double qVal) {
        dhTab.getDHVar().setVars(name, qVal);
    }

    public void set(int index, double qVal) {
        dhTab.getDHVar().setVars(index, qVal);
    }

    public void set(double... qVals) {
        dhTab.getDHVar().setVars(qVals);
    }

    public void add(String name, double qVal) {
        dhTab.getDHVar().setVars(name, get(name) + qVal);
    }

    public void add(int index, double qVal) {
        dhTab.getDHVar().setVars(index, get(index) + qVal);
    }

    public void add(double... qVals) {
        for (int i = 0; i < qVals.length; i++) {
            add(i, qVals[i]);
        }
    }

    public double get(String name) {
        return dhTab.getDHVar().getVar(name).getValue().doubleValue();
    }

    public double get(int index) {
        return dhTab.getDHVar().getVar(index).getValue().doubleValue();
    }

    public double[] get() {
        Variable<DoubleReal>[] v = dhTab.getDHVar().getVar();
        double[] vRet = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            Variable<DoubleReal> var = v[i];
            vRet[i] = var.getValue().doubleValue();
        }
        return vRet;
    }
}

package robots.DH;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import processingElement.CommonDraw;
import robots.DH.Links.Link;
import robots.DH.Links.PrismLink;
import robots.DH.Links.RotLink;
import robots.DH.math.DoubleReal;
import robots.DH.math.autodiff.DifferentialFunction;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.PI;

public class DenHart {

    protected PApplet win;
    private CommonDraw com = CommonDraw.getInstance();


    /**
     * Attributes
     */
    private ArrayList<Link> dhTabStrut, dhTabOri;
    private MatrixQ Q_tot, Jp;
    private MatrixQ Q_strut, JpStrut;
    private MatrixQ Q_Ori;
    private MatrixQ aXYZ, aYXZ, aZYZ;   // 3 Terne Fisse: Nautica RPY | Nautica Nautica YXZ | EULERO(POLSO) ZYZ
    public MatrixQ J_XYZ, J_YXZ, J_ZYZ;
    protected static final double eSing = 1 - 0.01;   // margine prima di considerarmi troppo vicino a singolarità angolari
    private MatrixQ JsysQ;

    /**
     * Constructors
     */
    public DenHart(PApplet win) {
        this.dhTabStrut = new ArrayList<Link>(0);
        this.dhTabOri = new ArrayList<Link>(0);

        this.Q_tot = new MatrixQ().setIdentity();
        this.Q_strut = new MatrixQ().setIdentity();
        this.Q_Ori = new MatrixQ().setIdentity();

        this.Jp = this.getDsym().jacobian();
        this.JpStrut = this.getDsym().jacobian();

        this.createXYZeq(Q_tot, Q_Ori.getQVars());
        this.createYXZeq(Q_tot, Q_Ori.getQVars());
        this.createZYZeq(Q_tot, Q_Ori.getQVars());
        this.JsysQ = this.getSysQSym().jacobian();

        this.win = win;
    }

    //Copy constructor (per ora non lo è)
//    public DenHart(DenHart denHart) {
//        this.denHartTab = denHart.getLinks();
//        this.Q_tot = denHart.Q_tot;
//
//        this.vars = denHart.vars;
//        this.win = denHart.win;
//    }

    private DenHart() {
        this((PApplet) null);
    }

    private DenHart(ArrayList<Link> dhTabStrut) {
        this(null, dhTabStrut);
    }

    private DenHart(PApplet win, ArrayList<Link> dhTabStrut) {
        this(win);
        for (Link l : dhTabStrut) {
            this.addLinkStrut(l);
        }

    }

    public void draw() {
        win.push();
        for (Link l : dhTabStrut) {
            l.draw();
            win.pushStyle();
            win.noStroke();
            win.sphere(l.getRadius());
            win.popStyle();
        }
        com.axes(150);
        Link lastLink = null;
        for (Link l : dhTabOri) {
            l.draw();
            win.pushStyle();
            win.noStroke();
            win.sphere(l.getRadius());
            win.popStyle();
            lastLink = l;
        }
        if (lastLink != null)
            com.pinza(lastLink.getRadius() * 5, lastLink.getRadius() * 3, lastLink.getRadius() * 2, 1);
        com.axes(255);
        win.pop();
    }

    /**
     * Structural methods
     */

    public void addLinkStrut(Link link) {
        //append a new link to D-H table
        this.dhTabStrut.add(link);
        this.Q_strut.mulOnSelf(link.getQLink());
        this.JpStrut = this.getDStrutSym().jacobian();

        this.Q_tot = this.Q_strut.mul(this.Q_Ori);
        this.Jp = this.getDsym().jacobian();
        this.JsysQ = this.getSysQSym().jacobian();
    }

    public void addLinkOri(Link link) {
        //append a new link to D-H table
        this.dhTabOri.add(link);
        this.Q_Ori.mulOnSelf(link.getQLink());

        this.Q_tot = this.Q_strut.mul(this.Q_Ori);
        this.Jp = this.getDsym().jacobian();
        this.JsysQ = this.getSysQSym().jacobian();

        this.createXYZeq(Q_tot, Q_Ori.getQVars());
        this.createYXZeq(Q_tot, Q_Ori.getQVars());
        this.createZYZeq(Q_tot, Q_Ori.getQVars());
    }

    //todo: Cambiare e adattare per le 2 diverse liste
//    public Link removeLastLink() {
//        // remove last link (-> entry of D-H table). WARNING: could be very heavy to compute.
//
//        Link link2ret = this.dhTabStrut.remove(dhTabStrut.size() - 1);
//        Q_tot.setIdentity();    //"clear della matrice e delle variabili"
//        for (Link l : dhTabStrut) {
//            addLinkStrut(l);
//        }
//        return link2ret;
//    }

    /**
     * Variable interaction
     **/

    public QVars getDHVar() {
        return this.Q_tot.getQVars();
    }

    public QVars getStrutVar() {
        return this.Q_strut.getQVars();
    }

    public QVars getOriVar() {
        return this.Q_Ori.getQVars();
    }

    /**
     * Compute Numeric Matrix form variable value
     **/
    public SimpleMatrix getQ() {
        return this.Q_tot.getNumeric();
    }

    public SimpleMatrix getQsys() {
        return getSysQSym().getNumeric();
    }

    public SimpleMatrix getD() {
        return getDsym().getNumeric();
    }

    public SimpleMatrix getDStrut() {
        return getDStrutSym().getNumeric();
    }

    public SimpleMatrix getDOri() {
        return getDOriSym().getNumeric();
    }

    public SimpleMatrix getR() {
        return getRsym().getNumeric();
    }

    public SimpleMatrix getRStrut() {
        return getRStrutsym().getNumeric();
    }

    public SimpleMatrix getROri() {
        return getROrisym().getNumeric();
    }

    public SimpleMatrix getJp() {
        return this.Jp.getNumeric();
    }

    public SimpleMatrix getJpStrut() {
        return this.JpStrut.getNumeric();
    }

    public SimpleMatrix getJsys() {
        return this.JsysQ.getNumeric();
    }

    /**
     * Symbolic Matrix
     **/
    public MatrixQ getQsym() {
        return this.Q_tot;
    }

    public MatrixQ getSysQSym() {
        return this.Q_tot.getSysQ();
    }

    public MatrixQ getDsym() {
        return this.Q_tot.getVPos();
    }

    public MatrixQ getDStrutSym() {
        return this.Q_strut.getVPos();
    }

    public MatrixQ getDOriSym() {
        return this.Q_Ori.getVPos();
    }

    public MatrixQ getRsym() {
        return this.Q_tot.getMatRot();
    }

    public MatrixQ getRStrutsym() {
        return this.Q_strut.getMatRot();
    }

    public MatrixQ getROrisym() {
        return this.Q_Ori.getMatRot();
    }

    public MatrixQ getJsym() {
        return this.Jp;
    }

    public MatrixQ getJSysSym() {
        return this.JsysQ;
    }

    /**
     * DH table informatio
     **/
    public ArrayList<Link> getLinks() {
        return this.dhTabStrut;
    }

    public int getNumDOF() {
        return this.dhTabStrut.size();
    }

    /**
     * Orientation function calc XYZ, YXZ, ZYZ
     */

    // sol = true up solution, false = button solution
    public static SimpleMatrix getTriadAngles(TriadDegs tri, SimpleMatrix R, boolean sol) {
        switch (tri) {
            case XYZ:   // Roll - Pich - Yaw
                return getXYZangles(R, sol);
            case YXZ:
                return getYXZangles(R, sol);
            case ZYZ:
                return getZYZangles(R, sol);
        }
        return null;
    }

    // sol = true up solution, false = button solution
    public static SimpleMatrix getAnglesTriad(TriadDegs tri, SimpleMatrix deg, boolean sol) {
        switch (tri) {
            case XYZ:   // Roll - Pich - Yaw
                return getXYZangles(deg, sol);
            case YXZ:
                return getYXZangles(deg, sol);
            case ZYZ:
                return getZYZangles(deg, sol);
        }
        return null;
    }

    public SimpleMatrix getAnglesTriad(TriadDegs tri, boolean sol) {
        return getAnglesTriad(tri, this.getR(), sol);
    }

    public SimpleMatrix getJTriad(TriadDegs tri) {
        switch (tri) {
            case XYZ:   // Roll - Pich - Yaw
                return getJXYZ();
            case YXZ:
                return getJYXZ();
            case ZYZ:
                return getJZYZ();
        }
        return null;
    }

    public static TriadDegs bestTriadOutsing(SimpleMatrix R1, SimpleMatrix R2) {
        double minXYZ = Math.pow(howNearXYZsing(R1), 2) + Math.pow(howNearXYZsing(R2), 2);
        double minYXZ = Math.pow(howNearYXZsing(R1), 2) + Math.pow(howNearYXZsing(R2), 2);
        double minZYZ = Math.pow(howNearZYZsing(R1), 2) + Math.pow(howNearZYZsing(R2), 2);
        double min = minXYZ;
        TriadDegs best = TriadDegs.XYZ;
        if (min > minYXZ) {
            min = minYXZ;
            best = TriadDegs.YXZ;
        }
        if (min > minZYZ) {
            min = minZYZ;
            best = TriadDegs.ZYZ;
        }

        if (min > DenHart.eSing) {  // Tutti sono vicino singolarità!! ( in teoria impossibile)
            System.err.println("DenHart.bestTriadOutsing(): hai bucato la matematica che ci aspettavamo!!!!\nMOSTRO");
            best = null;
        }
        return best;
    }

    /*XYZ*/
    private void createXYZeq(MatrixQ Q, QVars Qv) {
        this.aXYZ = new MatrixQ(3, 1);
        this.aXYZ.getQVars().mergeVar_s(Qv);
        // cos(beta)
        DifferentialFunction<DoubleReal> cb = MatrixQ.DFFactory.square(MatrixQ.one.minus(Q.getMatrix()[2][0].pow(2)));
        // +beta
        aXYZ.getMatrix()[1][0] = MatrixQ.DFFactory.atan2(Q.getMatrix()[2][0].negate(), cb);
        // + gamma
        aXYZ.getMatrix()[2][0] = MatrixQ.DFFactory.atan2(Q.getMatrix()[1][0], Q.getMatrix()[0][0]);
        // + alpha
        aXYZ.getMatrix()[0][0] = MatrixQ.DFFactory.atan2(Q.getMatrix()[2][1], Q.getMatrix()[2][2]);
        this.J_XYZ = aXYZ.jacobian();
    }
    // sol = true up solution, false = button solution

    public static SimpleMatrix getXYZangles(SimpleMatrix R, boolean sol) {
        SimpleMatrix aXYZret = new SimpleMatrix(3, 1);
        // cos(beta)
        double cb = Math.sqrt(1 - Math.pow(R.get(2, 0), 2));
        // +beta
        aXYZret.set(1, 0, Math.atan2(-R.get(2, 0), cb));
        // + gamma
        aXYZret.set(2, 0, Math.atan2(+R.get(1, 0), +R.get(0, 0)));
        // + alpha
        aXYZret.set(0, 0, Math.atan2(+R.get(2, 1), +R.get(2, 2)));
        return getDegXYZSol(aXYZret, sol);
    }

    // sol = true up solution, false = button solution
    public static SimpleMatrix getDegXYZSol(SimpleMatrix deg, boolean sol) {
        SimpleMatrix degSol = deg.copy();
        if (!sol) {
            SimpleMatrix pi = new SimpleMatrix(3, 1);
            pi.fill(PI);
            degSol.minus(pi);
        }
//        for (int i = 0; i < degSol.numRows(); i++)
//            degSol.set(i, degSol.get(i) % (2 * PI) - PI);

        return degSol;
    }

    public SimpleMatrix getJXYZ() {
        return J_XYZ.getNumeric();
    }

    private static double howNearXYZsing(SimpleMatrix R) {
        return R.get(2, 0); // meglio se lontano da +-1
    }


    /*YXZ*/
    private void createYXZeq(MatrixQ Q, QVars Qv) {
        this.aYXZ = new MatrixQ(3, 1);
        this.aYXZ.getQVars().mergeVar_s(Qv);
        // cos(beta)
        DifferentialFunction<DoubleReal> cb = MatrixQ.DFFactory.square(MatrixQ.one.minus(Q.getMatrix()[2][1].pow(2)));
        // +beta
        aYXZ.getMatrix()[1][0] = MatrixQ.DFFactory.atan2(Q.getMatrix()[2][1], cb);
        // + gamma
        aYXZ.getMatrix()[2][0] = MatrixQ.DFFactory.atan2(Q.getMatrix()[0][1].negate(), Q.getMatrix()[1][1]);
        // + alpha
        aYXZ.getMatrix()[0][0] = MatrixQ.DFFactory.atan2(Q.getMatrix()[2][0].negate(), Q.getMatrix()[2][2]);
        this.J_YXZ = aYXZ.jacobian();
    }
    // sol = true up solution, false = button solution

    public static SimpleMatrix getYXZangles(SimpleMatrix R, boolean sol) {
        SimpleMatrix aYXZret = new SimpleMatrix(3, 1);
        // cos(beta)
        double cb = Math.sqrt(1 - Math.pow(R.get(2, 1), 2));
        // +beta
        aYXZret.set(1, 0, Math.atan2(+R.get(2, 1), +cb));
        // + gamma
        aYXZret.set(2, 0, Math.atan2(-R.get(0, 1), +R.get(1, 1)));
        // + alpha
        aYXZret.set(0, 0, Math.atan2(-R.get(2, 0), +R.get(2, 2)));
        return getDegYXZSol(aYXZret, sol);
    }

    // sol = true up solution, false = button solution
    public static SimpleMatrix getDegYXZSol(SimpleMatrix deg, boolean sol) {
        SimpleMatrix degSol = deg.copy();
        if (!sol) {
            SimpleMatrix pi = new SimpleMatrix(3, 1);
            pi.fill(PI);
            degSol.plus(pi);
        }
//        for (int i = 0; i < degSol.numRows(); i++)
//            degSol.set(i, degSol.get(i) % (2 * PI) - PI);

        return degSol;
    }

    public SimpleMatrix getJYXZ() {
        return J_YXZ.getNumeric();
    }

    private static double howNearYXZsing(SimpleMatrix R) {
        return R.get(2, 1); // meglio se lontano da +-1
    }

    /*ZYZ*/
    private void createZYZeq(MatrixQ Q, QVars Qv) {
        this.aZYZ = new MatrixQ(3, 1);
        this.aZYZ.getQVars().mergeVar_s(Qv);
        // cos(beta)
        DifferentialFunction<DoubleReal> sb = MatrixQ.DFFactory.square(MatrixQ.one.minus(Q.getMatrix()[2][2].pow(2)));
        // +beta
        aZYZ.getMatrix()[1][0] = MatrixQ.DFFactory.atan2(sb, Q.getMatrix()[2][2]);
        // + gamma
        aZYZ.getMatrix()[2][0] = MatrixQ.DFFactory.atan2(Q.getMatrix()[1][2], Q.getMatrix()[0][2]);
        // + alpha
        aZYZ.getMatrix()[0][0] = MatrixQ.DFFactory.atan2(Q.getMatrix()[2][1], Q.getMatrix()[2][0].negate());
        this.J_ZYZ = aZYZ.jacobian();
    }
    // sol = true up solution, false = button solution

    public static SimpleMatrix getZYZangles(SimpleMatrix R, boolean sol) {
        SimpleMatrix aZYZret = new SimpleMatrix(3, 1);
        // cos(beta)
        double sb = Math.sqrt(1 - Math.pow(R.get(2, 2), 2));
        // +beta
        aZYZret.set(1, 0, Math.atan2(+sb, +R.get(2, 2)));
        // + gamma
        aZYZret.set(2, 0, Math.atan2(+R.get(1, 2), +R.get(0, 2)));
        // + alpha
        aZYZret.set(0, 0, Math.atan2(+R.get(2, 1), -R.get(2, 0)));
        return getDegZYZSol(aZYZret, sol);
    }

    // sol = true up solution, false = button solution
    public static SimpleMatrix getDegZYZSol(SimpleMatrix deg, boolean sol) {
        SimpleMatrix degSol = deg.copy();
        if (!sol) {
            degSol.set(0, 0, -degSol.get(0, 0));
            SimpleMatrix pi = new SimpleMatrix(3, 1);
            pi.fill(PI);
            pi.set(0, 0, 0);
            degSol.minus(pi);
        }
//        for (int i = 0; i < degSol.numRows(); i++)
//            degSol.set(i, degSol.get(i) % (2 * PI) - PI);

        return degSol;
    }

    public SimpleMatrix getJZYZ() {
        return J_ZYZ.getNumeric();
    }


    private static double howNearZYZsing(SimpleMatrix R) {
        return R.get(2, 2); // meglio se lontano da +-1
    }

    /**
     * Print methods
     */

    public void printDHTab() {
        System.out.println("DH sym:\t\t\t\t\t\tDH num:");
        for (Link l : this.dhTabStrut) {
            l.printLink();
            System.out.print("\t");
            l.printValLink();
            System.out.println();
        }
        System.out.println();

        Q_tot.printMatValue();
    }


    /**
     * Demo main
     **/
    public static void main(String[] args) {
        DenHart dh = new DenHart();
        dh.addLinkStrut(new RotLink(null, "q1", 50, (float) PI, 0));
        dh.addLinkStrut(new PrismLink(null, (float) PI / 2, "q2", 0, 20));
        dh.printDHTab();
        System.out.println("Robot RP, in cui si fa variare la P a ogni iterazione:");
        int i = 0;
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dh.getDHVar().setVars(1, i);  //checked
//            dh.getRVar().setVars("q2", i);  //checked
//            double[] v = {0.0,i}; dh.getRVar().setVars(v);    //checked
//            dh.getRVar().getVar()[1].set(new DoubleReal(i));  //checked
            dh.printDHTab();
            System.out.println("Variabili di giunto:");
            dh.getDHVar().printVar();
            System.out.println("Matrice cinematica diretta:");
            dh.getQ().print("%.3f");
            System.out.println("Matrice Rotazione:");
            dh.getR().print("%.3f");
            System.out.println("Matrice Posizione:");
            dh.getD().print("%.3f");
            System.out.println("Matrice Jacobiana:");
            dh.getJp().print("%.3f");
            System.out.println();
            i++;
        }
    }

}

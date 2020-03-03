package robots.DH;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import processingElement.CommonDraw;
import robots.DH.Links.Link;
import robots.DH.Links.PrismLink;
import robots.DH.Links.RotLink;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.PI;

public class DenHart {

    PApplet win;
    /**
     * Attributes
     */

    private ArrayList<Link> dhTab;
    private CommonDraw com = CommonDraw.getInstance();
    private MatrixQ Q_tot;
    //private RobVars vars;
    private MatrixQ J;

    /**
     * Constructors
     */
    public DenHart(PApplet win) {
        this.dhTab = new ArrayList<Link>(0);
        this.Q_tot = new MatrixQ().setIdentity();

//        this.vars = new RobVars();
        this.J = this.getDsym().jacobian();

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

    private DenHart(ArrayList<Link> dhTab) {
        this(null, dhTab);
    }

    private DenHart(PApplet win, ArrayList<Link> dhTab) {
        this(win);
        for (Link l : dhTab) {
            this.addLink(l);
        }

    }

    /**
     * Demo main
     **/
    public static void main(String[] args) {
        DenHart dh = new DenHart();
        dh.addLink(new RotLink(null, "q1", 50, (float) PI, 0));
        dh.addLink(new PrismLink(null, (float) PI / 2, "q2", 0, 20));
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
            dh.getJ().print("%.3f");
            System.out.println();
            i++;
        }
    }

    public void draw() {
        win.push();
        Link lastLink = null;
        for (Link l : dhTab) {
            l.draw();
            win.pushStyle();
            win.noStroke();
            win.sphere(l.getRadius());
            win.popStyle();
            lastLink = l;
        }
        if (lastLink != null)
//            com.pinza(lastLink.getRadius(),lastLink.getRadius(),lastLink.getRadius(),1);
            com.pinza(lastLink.getRadius() * 5, lastLink.getRadius() * 3, lastLink.getRadius() * 2, 1);
        win.pop();
    }

    /**
     * Structural methods
     */

    public void addLink(Link link) {
        //append a new link to D-H table
        this.dhTab.add(link);
        this.Q_tot.mulOnSelf(link.getQLink());
//        this.vars.addVar(link.getVar());
//        this.Q_tot.getRobVars().addVar(link.getVar());

        this.getDsym().printMatSym();

        this.J = this.getDsym().jacobian();
    }

    public Link removeLastLink() {
        // remove last link (-> entry of D-H table). WARNING: could be very heavy to compute.

        Link link2ret = this.dhTab.remove(dhTab.size() - 1);
        Q_tot.setIdentity();    //"clear della matrice e delle variabili"
        for (Link l : dhTab) {
            addLink(l);
        }
        return link2ret;
    }

    /**
     * Variable interaction
     **/

    public RobVars getDHVar() {
        return this.Q_tot.getRobVars();
    }

    /**
     * Compute Numeric Matrix form variable value
     **/
    public SimpleMatrix getQ() {
        return this.Q_tot.getNumeric();
    }

    public SimpleMatrix getD() {
        return getDsym().getNumeric();
    }

    public SimpleMatrix getR() {
        return getRsym().getNumeric();
    }

    public SimpleMatrix getJ() {
        return this.J.getNumeric();
    }

    /**
     * Symbolic Matrix
     **/
    public MatrixQ getQsym() {
        return this.Q_tot;
    }

    public MatrixQ getDsym() {
        return this.Q_tot.getVPos();
    }

    public MatrixQ getRsym() {
        return this.Q_tot.getMatRot();
    }

    public MatrixQ getJsym() {
        return this.J;
    }

    /**
     * DH table informatio
     **/
    public ArrayList<Link> getLinks() {
        return this.dhTab;
    }

    public int getNumDOF() {
        return this.dhTab.size();
    }

    /**
     * Print methods
     */

    public void printDHTab() {
        System.out.println("DH sym:\t\t\t\t\t\tDH num:");
        for (Link l : this.dhTab) {
            l.printLink();
            System.out.print("\t");
            l.printValLink();
            System.out.println();
        }
        System.out.println();

        Q_tot.printMatValue();
    }

}

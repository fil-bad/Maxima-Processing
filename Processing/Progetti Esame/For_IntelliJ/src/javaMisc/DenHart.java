package javaMisc;

import javaMisc.math.DoubleReal;
import javaMisc.math.autodiff.Variable;

import static java.lang.Math.*;

import java.util.ArrayList;

public class DenHart {

    /**
     * Attributes
     */

    private ArrayList<Link> denHartTab;

    private MatrixQ Q_tot;
    private ArrayList<Variable<DoubleReal>> vars;

    /**
     * Constructors
     */

    public DenHart() {
        this.denHartTab = new ArrayList<Link>(0);
        this.Q_tot = new MatrixQ().setIdentity();
        this.vars = new ArrayList<Variable<DoubleReal>>(0);
    }

    public DenHart(ArrayList<Link> denHartTab) {
        this();
        for (Link l : denHartTab) {
            this.addLink(l);
        }
    }

    public DenHart(DenHart denHart) {
        this.denHartTab = denHart.getLinks();
        this.Q_tot = denHart.Q_tot;
        this.vars = denHart.vars;
    }

    /**
     * Structural methods
     */

    public void addLink(Link link) {
        //append a new link to D-H table
        this.denHartTab.add(link);
        this.Q_tot.mulOnSelf(link.getQLink());
        this.vars.add(link.getVar());
    }

    public Link removeLink() {
        // remove last link (-> entry of D-H table)
        // WARNING: could be very heavy to compute
        Link link2ret = this.denHartTab.remove(denHartTab.size() - 1);
        DenHart dhTmp = new DenHart(this.denHartTab);
        // we copy only the fields we need
        this.Q_tot = dhTmp.Q_tot;
        this.vars = dhTmp.vars;
        return link2ret;
    }

    /**
     * Update matrix
     */


    /**
     * Getter & Setter methods
     */

    public ArrayList<Link> getLinks() {
        return this.denHartTab;
    }

    public int getNumDOF() {
        return this.denHartTab.size();
    }

    /**
     * Print methods
     */

    public void printDHTab() {
        System.out.println("DH:");
        for (Link l : this.denHartTab) {
            l.printLink();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        DenHart dh = new DenHart();
        dh.addLink(new RotLink("q1", 50, (float) PI, 0));
        dh.addLink(new PrismLink((float) PI/2, "q2", 0,20));
        dh.printDHTab();
    }

}

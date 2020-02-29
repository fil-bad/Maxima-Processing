package javaMisc;

import javaMisc.math.DoubleReal;
import javaMisc.math.autodiff.Variable;
import org.ejml.simple.SimpleMatrix;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DenHart {

    /**
     * Attributes
     */

    private ArrayList<Link> denHartTab;

    private MatrixQ Q_tot;
    private ArrayList<Variable<DoubleReal>> vars;

    private MatrixQ J;

    // todo: add Jacobian matrix & update method

    /**
     * Constructors
     */

    public DenHart() {
        this.denHartTab = new ArrayList<Link>(0);
        this.Q_tot = new MatrixQ().setIdentity();
        this.vars = new ArrayList<Variable<DoubleReal>>(0);
        this.J = this.getPos().jacobian();
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
        this.J = this.getPos().jacobian();
    }

    public Link removeLink() {
        // remove last link (-> entry of D-H table). WARNING: could be very heavy to compute.

        Link link2ret = this.denHartTab.remove(denHartTab.size() - 1);
        DenHart dhTmp = new DenHart(this.denHartTab);
        // we copy only the fields we need
        this.Q_tot = dhTmp.Q_tot;
        this.vars = dhTmp.vars;
        this.J = this.getPos().jacobian();
        return link2ret;
    }

    /**
     * Update matrix
     */

    public void updateVar(String qi, double val) {
        for (Variable<DoubleReal> var : this.vars) {
            if (qi.equals(var.toString())) {
                var.set(new DoubleReal(val));
                return;
            }
        }
        System.err.println("Variable not Found!");
    }

    public void updateVars(double... vals) {
        assert (vals.length == this.vars.size()); //we have to update all variables at once
        int i = 0;
        for (Variable<DoubleReal> var : this.vars) {
            var.set(new DoubleReal(vals[i]));
            i++;
        }
    }


    /**
     * Getter & Setter methods
     */

    public SimpleMatrix getNumericQ() {
        return this.Q_tot.getNumeric();
    }

    public SimpleMatrix getNumericJ() {
        return this.J.getNumeric();
    }


    public ArrayList<Link> getLinks() {
        return this.denHartTab;
    }

    public int getNumDOF() {
        return this.denHartTab.size();
    }

    public MatrixQ getPos() {
        return this.Q_tot.getVPos();
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

    public static void main(String[] args) throws InterruptedException {
        DenHart dh = new DenHart();
        dh.addLink(new RotLink("q1", 50, (float) PI, 0));
        dh.addLink(new PrismLink((float) PI / 2, "q2", 0, 20));
        dh.printDHTab();
        int i = 0;
        while (true) {
            TimeUnit.SECONDS.sleep(1);
            dh.updateVar("q2", i);
            dh.getNumericQ().print("%.3f");
            i++;
        }
        //dh.getNumericJ().print("%.3f");

    }

}

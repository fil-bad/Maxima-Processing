package robots.DH;

import org.ejml.simple.SimpleMatrix;
import robots.DH.math.DoubleReal;
import robots.DH.math.autodiff.Variable;

import java.util.ArrayList;

import static java.lang.Math.PI;

public class DenHart {

    /**
     * Attributes
     */

    private ArrayList<Link> denHartTab;

    private MatrixQ Q_tot, J;
    private RobVars vars;

    // todo: add Jacobian matrix & update method

    /**
     * Constructors
     */

    public DenHart() {
        this.denHartTab = new ArrayList<Link>(0);
        this.Q_tot = new MatrixQ().setIdentity();
        this.vars = new RobVars();
        //new ArrayList<Variable<DoubleReal>>(0);
        this.J = this.getDsym().jacobian();
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
        this.vars.addVar(link.getVar());
        this.J = this.getDsym().jacobian();
    }

    public Link removeLink() {
        // remove last link (-> entry of D-H table). WARNING: could be very heavy to compute.

        Link link2ret = this.denHartTab.remove(denHartTab.size() - 1);
        DenHart dhTmp = new DenHart(this.denHartTab);
        // we copy only the fields we need
        this.Q_tot = dhTmp.Q_tot;
        this.vars = dhTmp.vars;
        this.J = this.getDsym().jacobian();
        return link2ret;
    }


    /**
     * Numeric Compute of the Variable set
     **/
    public SimpleMatrix getQ() {
        return this.Q_tot.getNumeric();
    }

    public SimpleMatrix getD() {
        return this.Q_tot.getVPos().getNumeric();
    }

    public SimpleMatrix getR() {
        return this.Q_tot.getMatRot().getNumeric();
    }

    public SimpleMatrix getJ() {
        return this.J.getNumeric();
    }

    /**
     * Symbolic Return
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
     * DH table information
     **/
    public ArrayList<Link> getLinks() {
        return this.denHartTab;
    }

    public int DOFsize() {
        return this.denHartTab.size();
    }

    /**
     * Print methods
     **/
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
        dh.addLink(new PrismLink((float) PI / 2, "q2", 0, 20));
        dh.printDHTab();
    }

}

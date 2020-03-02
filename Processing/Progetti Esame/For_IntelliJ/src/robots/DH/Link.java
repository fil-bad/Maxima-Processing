package robots.DH;

import robots.DH.math.DoubleReal;
import robots.DH.math.autodiff.Variable;

public interface Link {
    /**
     *
     **/
    MatrixQ getQLink();

    Variable<DoubleReal> getVar();

    RobVars whichQ_iIs();

    void printLink();

    void printValLink();

    void draw();
}

package robots.DH.Links;

import robots.DH.MatrixQ;
import robots.DH.QVars;
import robots.DH.math.DoubleReal;
import robots.DH.math.autodiff.Variable;

public interface Link {
    /**
     *
     **/
    MatrixQ getQLink();

    Variable<DoubleReal> getVar();

    QVars whichQ_iIs();

    void printLink();

    void printValLink();

    void draw();

    float getRadius();
}

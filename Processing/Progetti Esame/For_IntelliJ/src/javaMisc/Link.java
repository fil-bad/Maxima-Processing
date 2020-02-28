package javaMisc;

import javaMisc.math.DoubleReal;
import javaMisc.math.autodiff.Variable;

public interface Link {

    MatrixQ getQLink();

    Variable<DoubleReal> getVar();

    String whichQ_iIs();

    void printLink();
}

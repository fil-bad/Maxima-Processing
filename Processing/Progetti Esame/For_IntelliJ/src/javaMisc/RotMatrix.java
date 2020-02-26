package javaMisc;

import javaMisc.math.autodiff.DifferentialMatrixFunction;
import javaMisc.math.autodiff.DifferentialRealFunctionFactory;
import javaMisc.math.autodiff.Variable;

import javaMisc.math.*;

public class RotMatrix {

    private final DoubleRealFactory RNFactory = DoubleRealFactory.instance();
    private final DifferentialRealFunctionFactory<DoubleReal> DFFactory = new DifferentialRealFunctionFactory<DoubleReal>(RNFactory);

    private DifferentialMatrixFunction<DoubleReal> matrix; // todo: implementare questa interfaccia!

    public void getXRot(double th) {
        Variable<DoubleReal> q = DFFactory.var("q", new DoubleReal(th));


    }

}

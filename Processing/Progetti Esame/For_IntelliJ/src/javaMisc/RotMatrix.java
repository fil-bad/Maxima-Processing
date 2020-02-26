package javaMisc;

import math.autodiff.DifferentialMatrixFunction;
import math.autodiff.DifferentialRealFunctionFactory;
import math.autodiff.Variable;

import math.*;

public class RotMatrix {

    private final DoubleRealFactory RNFactory = DoubleRealFactory.instance();
    private final DifferentialRealFunctionFactory<DoubleReal> DFFactory = new DifferentialRealFunctionFactory<DoubleReal>(RNFactory);

    private DifferentialMatrixFunction<DoubleReal> matrix; // todo: implementare questa interfaccia!

    public void getXRot(double th) {
        Variable<DoubleReal> q = DFFactory.var("q", new DoubleReal(th));


    }

}

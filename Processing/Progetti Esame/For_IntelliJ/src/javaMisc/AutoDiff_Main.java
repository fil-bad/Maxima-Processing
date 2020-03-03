package javaMisc;

import robots.DH.math.DoubleReal;
import robots.DH.math.DoubleRealFactory;
import robots.DH.math.autodiff.Constant;
import robots.DH.math.autodiff.DifferentialFunction;
import robots.DH.math.autodiff.DifferentialRealFunctionFactory;
import robots.DH.math.autodiff.Variable;

public class AutoDiff_Main {

    private static final DoubleRealFactory RNFactory = DoubleRealFactory.instance();
    private static final DifferentialRealFunctionFactory<DoubleReal> DFFactory = new DifferentialRealFunctionFactory<DoubleReal>(RNFactory);


    public static void main(String[] args) {

        double value_x = 3.0;
        double value_y = 3.0;

        Variable<DoubleReal> xVar = DFFactory.var("x", new DoubleReal(value_x));
        Variable<DoubleReal> y = DFFactory.var("y", new DoubleReal(value_y));

        Constant<DoubleReal> q = DFFactory.val(new DoubleReal(5));

        //h = q*x*( cos(x*y) + y )
        //DifferentialFunction<DoubleReal> h = q.mul(x).mul( DFFactory.cos( x.mul(y) ).plus(y) );
        DifferentialFunction<DoubleReal> h = xVar;
        System.out.println(h.toString());

        System.out.println(h.getValue().doubleValue());

        xVar.set(new DoubleReal(42));

        System.out.println(h.getValue().doubleValue());

        DifferentialFunction<DoubleReal> h1 = h.mul(5);
        System.out.println(h1.toString());


//
//
//        System.out.println(h.getValue().doubleValue());
//
//        xVar.set(new DoubleReal(-5));
//
//        System.out.println(h.getValue().doubleValue());
//
//
//        DifferentialFunction<DoubleReal> dhpx = h.diff(xVar);
//        System.out.println(dhpx.toString());
//
//        DifferentialFunction<DoubleReal> r = xVar.mul(xVar).diff(xVar);
//        System.out.println(r.toString());
//
//        DifferentialFunction<DoubleReal> trig = DFFactory.cos(xVar.mul(xVar)).diff(xVar);
//        System.out.println(trig.toString());

    }
}

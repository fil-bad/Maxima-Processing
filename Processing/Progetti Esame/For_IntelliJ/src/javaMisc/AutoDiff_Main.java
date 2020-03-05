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

        double value_x = 1.0;
        double theta_y = Math.PI/4.0;

        Variable<DoubleReal> xVar = DFFactory.var("x", new DoubleReal(value_x));
        Variable<DoubleReal> theta = DFFactory.var("theta", new DoubleReal(theta_y));

        DifferentialFunction<DoubleReal> xVel = xVar.mul(DFFactory.cos(theta));
        DifferentialFunction<DoubleReal> yVel = xVar.mul(DFFactory.sin(theta));


        DifferentialFunction<DoubleReal> h = DFFactory.atan2(xVel,yVel);
        System.out.println("h funx : " + h.toString() + "\nin xVal=1 h = " + h.getValue().doubleValue());


        DifferentialFunction<DoubleReal> h1 = h.diff(xVar);
        DifferentialFunction<DoubleReal> h2 = h.diff(theta);
        System.out.println("h diff(xVel) = " + h1.getValue().doubleValue());
        System.out.println("h diff(theta) = " + h2.getValue().doubleValue());

        System.out.println();

        xVar.set(new DoubleReal(3));
        System.out.println("xVar change to 3 = " + h.getValue().doubleValue());
        System.out.println("h diff(xVel) = " + h1.getValue().doubleValue());
        System.out.println("h diff(theta) = " + h2.getValue().doubleValue());

        System.out.println("h diff(theta) =\n" + h2.toString());


//        Constant<DoubleReal> q = DFFactory.val(new DoubleReal(5));


    }
}

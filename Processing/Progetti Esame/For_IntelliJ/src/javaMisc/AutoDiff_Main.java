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
        double theta_y = Math.PI / 4;

        Variable<DoubleReal> xVar = DFFactory.var("x", new DoubleReal(value_x));
        Variable<DoubleReal> theta = DFFactory.var("theta", new DoubleReal(theta_y));

        DifferentialFunction<DoubleReal> xVel = xVar.mul(DFFactory.cos(theta));
        DifferentialFunction<DoubleReal> yVel = xVar.mul(DFFactory.sin(theta));


        DifferentialFunction<DoubleReal> h = DFFactory.atan2(xVel, yVel);
        System.out.println("h funx : " + h.toString() + "\nin xVal=1 h = " + h.getValue().doubleValue());


        DifferentialFunction<DoubleReal> h1 = h.diff(xVar);
        DifferentialFunction<DoubleReal> h2 = h.diff(theta);
        System.out.println("h diff(xVel) = " + h1.getValue().doubleValue());
        System.out.println("h diff(theta) = " + h2.getValue().doubleValue());
        System.out.println("h diff(theta):\n" + h2.toString());


        System.out.println();
        xVar.set(new DoubleReal(3));
        System.out.println("xVar change to 3; h = " + h.getValue().doubleValue());
        System.out.println("h diff(xVel) = " + h1.getValue().doubleValue());
        System.out.println("h diff(theta) = " + h2.getValue().doubleValue());


        System.out.println();
        xVar.set(new DoubleReal(0));
        System.out.println("xVar change to 0; h = " + h.getValue().doubleValue());
        System.out.println("h diff(xVel) = " + h1.getValue().doubleValue());
        System.out.println("h diff(theta) = " + h2.getValue().doubleValue());

        System.out.println();
        xVar.set(new DoubleReal(1));
        theta.set(new DoubleReal(Math.PI));
        System.out.println("theta change to PI; h = " + h.getValue().doubleValue());
        System.out.println("h diff(xVel) = " + h1.getValue().doubleValue());
        System.out.println("h diff(theta) = " + h2.getValue().doubleValue());

        System.out.println("#############################################");
        double dist = 1.0;
        double theta_1 = Math.PI;

        Variable<DoubleReal> distV = DFFactory.var("dist", new DoubleReal(dist));
        Variable<DoubleReal> theta1 = DFFactory.var("theta1", new DoubleReal(theta_1));

        DifferentialFunction<DoubleReal> xVel1 = distV.mul(DFFactory.cos(theta1));
        DifferentialFunction<DoubleReal> yVel1 = distV.mul(DFFactory.sin(theta1));


        DifferentialFunction<DoubleReal> g = DFFactory.atan2(yVel1, xVel1);
        System.out.println("g funx : " + g.toString() + "\nin dist=1 h = " + g.getValue().doubleValue());
        DifferentialFunction<DoubleReal> g1 = g.diff(distV);
        DifferentialFunction<DoubleReal> g2 = g.diff(theta1);
        System.out.println("g diff(distV) = " + g1.getValue().doubleValue());
        System.out.println("g diff(theta1) = " + g2.getValue().doubleValue());
        System.out.println("g diff(theta1):\n" + g2.toString());

        System.out.println("Test mod: -5.8 % 2.7=" + (-5.8 % 2.7));


//        Constant<DoubleReal> q = DFFactory.val(new DoubleReal(5));


    }
}

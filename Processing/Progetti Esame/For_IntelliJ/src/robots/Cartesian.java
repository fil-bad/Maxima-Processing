package robots;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import robots.DH.DenHart;
import robots.DH.Links.PrismLink;
import robots.DH.Links.RotLink;
import robots.DH.MatrixQ;
import robots.DH.math.DoubleReal;
import robots.DH.math.DoubleRealFactory;
import robots.DH.math.autodiff.Constant;
import robots.DH.math.autodiff.DifferentialRealFunctionFactory;


public class Cartesian extends Robot {

    public Cartesian(PApplet win, float b) {
        dhTab = new DenHart(win);
        this.win = win;
        dhTab.addLink(new PrismLink(win, b, 0, "q1", -Math.PI / 2.0, 0));
        dhTab.addLink(new PrismLink(win, b, -Math.PI / 2.0, "q2", -Math.PI / 2.0, 0));
        dhTab.addLink(new PrismLink(win, b, 0, "q3", 0, 0));
        dhTab.addLink(new RotLink(win, b, "q4", 0, -Math.PI / 2.0, 0));
        dhTab.addLink(new RotLink(win, b, "q5", 0, Math.PI / 2.0, 0));
        dhTab.addLink(new RotLink(win, b, "q6", 50, 0, 0));
    }

    public void draw() {
        com.axes(255);
        dhTab.draw();
    }

    @Override
    public void inverse(double x, double y, double z, double theta) {


        // Matrici di peso dell'errore
//        SimpleMatrix Ke, Kep, Keo;
//        double lambda = 1 / 10.0;
//        double gamma = 1 / 3000.0;
//        Kep = SimpleMatrix.identity(3).scale(lambda);
//        Keo = SimpleMatrix.identity(3).scale(gamma);
//        Ke = new SimpleMatrix(6, 6);
//        Ke.insertIntoThis(0, 0, Kep);
//        Ke.insertIntoThis(3, 3, Keo);
//
//        SimpleMatrix ep, eo;
//        ep = new SimpleMatrix(3, 1);
//        ep.set(0, x);
//        ep.set(1, y);
//        ep.set(2, z);
//        ep = ep.minus(dhTab.getD());
//        ep.print();
//
//        // Errore orientamento mediante terna ZYZ di eulero (polso sferico)
////        SimpleMatrix rDes = new SimpleMatrix(3,3);
////        rDes.set(0,0, Math.cos(theta)); rDes.set(0,1, -Math.sin(theta));
////        rDes.set(1,0, Math.sin(theta)); rDes.set(1,1, Math.cos(theta));
////        rDes.set(1,1,-1.0); // Pinsa verso il basso sempre (da progetto
//
//        eo = new SimpleMatrix(3, 1);
//        eo.set(0, 0);
//        eo.set(1, 0);
//        eo.set(2, 0);
//
//        SimpleMatrix qCap, qJ, qCapNew, Jp;
//        qCap = dhTab.getDHVar().get_qVect();
//        Jp = dhTab.getJ();
//        if (ep.normF() > 10.0)  //se errore "grande" uso gradiente
//            qJ = Ke.mult(Jp.transpose().mult(ep));
//        else
//            qJ = Jp.pseudoInverse().mult(ep);
//
//
//        //Necessario calcolare ancora Joa ( jacobbiano dell'orientamento)
////        SimpleMatrix error = Kp.mult(ep).concatRows(Ko.mult(eo));
////        error.print();
////        Jp.print();
////        qJ = Jp.transpose().mult(error);
//
//
//        qCapNew = qCap.plus(qJ);
//        dhTab.getDHVar().setVars(qCapNew);
//
//        System.out.println();

        /** Metodo di stima a 12 variabii **/
//         Matrici di peso dell'errore
        SimpleMatrix Ke, Kep, Keo;
        double lambda = 1 / 10.0;
        double gamma = 1/10000.0;
        Kep = SimpleMatrix.identity(3).scale(lambda);
        Keo = SimpleMatrix.identity(3).scale(gamma);
        Ke = new SimpleMatrix(6,6);
        Ke.insertIntoThis(0, 0, Kep);
        Ke.insertIntoThis(3, 3, Keo);

        SimpleMatrix pDes;
        pDes = new SimpleMatrix(3, 1);
        pDes.set(0, x);
        pDes.set(1, y);
        pDes.set(2, z);


        // 12 Eq tutte insieme
        // Definizone matrice di orientamento desiderata
        SimpleMatrix rDes = new SimpleMatrix(3, 3);
        rDes.set(0, 0, -Math.cos(theta));
        rDes.set(0, 1, +Math.sin(theta));
        rDes.set(1, 0, Math.sin(theta));
        rDes.set(1, 1, Math.cos(theta));
        rDes.set(2, 2, -1.0); // Pinsa verso il basso sempre (da progetto

        System.out.println("rDes:");
        rDes.print();
//        SimpleMatrix Re = rDes.mult(dhTab.getR().transpose());
        System.out.println("dhTab.getR():");
        dhTab.getR().print();
        SimpleMatrix Re = rDes.mult(dhTab.getR().transpose());
//        SimpleMatrix Re = SimpleMatrix.identity(3);
        System.out.println("Re:");
        Re.print();
        Re = Re.plus(SimpleMatrix.identity(3));



        SimpleMatrix rDesSys = new SimpleMatrix(9, 1);
        // 3 ori X | 3 ori Y | 3 ori Zi
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                rDesSys.set(3 * i + j, 0, Re.get(j, i));
        }
        rDesSys.print();


        SimpleMatrix e = new SimpleMatrix(12, 1);
        e.insertIntoThis(0, 0, pDes.minus(dhTab.getD()));
        e.insertIntoThis(3, 0, rDesSys);


//        //Rodrigez simbolico per ottenere dei vettori
//        DifferentialRealFunctionFactory<DoubleReal> DFFactory = new DifferentialRealFunctionFactory<DoubleReal>(DoubleRealFactory.instance());
//        MatrixQ rDes = new MatrixQ(3, 3);
//        rDes.getMatrix()[0][0] = DFFactory.val(new DoubleReal(-Math.cos(theta)));
//        rDes.getMatrix()[0][1] = DFFactory.val(new DoubleReal(+Math.sin(theta)));
//        rDes.getMatrix()[1][0] = DFFactory.val(new DoubleReal(+Math.sin(theta)));
//        rDes.getMatrix()[1][1] = DFFactory.val(new DoubleReal(+Math.cos(theta)));
//        rDes.getMatrix()[2][2] = DFFactory.val(new DoubleReal(-1.0));
//
//        MatrixQ Re = rDes.mul(dhTab.getRsym().transpose());
//        MatrixQ vRod = new MatrixQ(3, 1);
//        vRod.getQVars().mergeVar_s(Re.getQVars());
//        vRod.getMatrix()[0][0] = Re.getMatrix()[2][1].minus(Re.getMatrix()[1][2]);
//        vRod.getMatrix()[1][0] = Re.getMatrix()[0][2].minus(Re.getMatrix()[2][0]);
//        vRod.getMatrix()[2][0] = Re.getMatrix()[1][0].minus(Re.getMatrix()[0][1]);
//        //Normalizzazione
//        vRod.mulOnSelf(DFFactory.sqrt(
//                DFFactory.pow(vRod.getMatrix()[0][0], DFFactory.val(new DoubleReal(2))).plus(
//                        DFFactory.pow(vRod.getMatrix()[1][0], DFFactory.val(new DoubleReal(2))).plus(
//                                DFFactory.pow(vRod.getMatrix()[2][0], DFFactory.val(new DoubleReal(2)))))));
//
//        SimpleMatrix vRodNum = vRod.getNumeric();
//
//        SimpleMatrix ReNum = Re.getNumeric();
//        double thethaRod = Math.acos((ReNum.get(0, 0) + ReNum.get(1, 1) + ReNum.get(2, 2) - 1) / 2);
//
//        vRodNum=vRodNum.scale(thethaRod);
//
//        SimpleMatrix e = new SimpleMatrix(6, 1);
//        e.insertIntoThis(0, 0, pDes.minus(dhTab.getD()));
//        e.insertIntoThis(3, 0, vRodNum);


        SimpleMatrix qCap, qJ, qCapNew, Jsys;
        qCap = dhTab.getDHVar().get_qVect();
        Jsys = dhTab.getJsys();
        Jsys.print();
        System.out.println("e.normF() = " + e.normF());
//        if (e.normF() > 100.0)  //se errore "grande" uso gradiente
//            qJ = Jsys.transpose().mult(Ke.mult(e));
//        else
//            qJ = Jsys.pseudoInverse().mult(Ke.mult(e));
//
        qJ = Jsys.transpose().mult(e);
        qJ = Ke.mult(qJ);
        System.out.println("e.rows(3,12) = " + e.rows(3,12).normF());
        if(e.rows(3,12).normF()>0.1 && qJ.rows(3,6).normF()<0.001){
            for (int i = 3; i< qJ.numRows(); i++)
                qJ.set(i,qJ.get(i)+(2*Math.random()-1)/10.0);
        }

        System.out.println("qJ:");
        qJ.print();
        qCapNew = qCap.plus(qJ);
        dhTab.getDHVar().setVars(qCapNew);
//        qCapNew.print();
        System.out.println();

    }
}

package robots;

import org.ejml.data.DMatrix4x4;
import javaMisc.DenHart;

public class Puma {

    private DenHart dhTab;
    private DMatrix4x4 Q;

    public Puma(DenHart denHart) {
        this.dhTab = denHart;
        this.Q = new DMatrix4x4();
        Q.zero();
        for (int i = 0; i < Q.getNumCols(); i++) {
            Q.set(i, i, 1);
        }


    }


    public void printDHTab() {
        this.dhTab.printDHTab();
    }

    public void printQ() {
        this.Q.print();
    }

    public static void main(String[] args) {
        Puma puma = new Puma(new DenHart());
    }


}

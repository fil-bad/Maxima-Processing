package robots;

import com.jogamp.opengl.math.Matrix4;
import org.ejml.data.Matrix;
import processingElement.DenHart;

public class Puma {

    private DenHart dhTab;
    private Matrix Q;

    public Puma(DenHart denHart) {
        this.dhTab = denHart;

    }


    public void printDHTab() {
        this.dhTab.printDHTab();
    }

    public static void main(String[] args) {
        Puma puma = new Puma(new DenHart());
    }


}

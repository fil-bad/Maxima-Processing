package javaMisc;

import org.ejml.data.DMatrix3x3;
import org.ejml.data.Matrix;
import org.ejml.interfaces.MatrixType;
import org.ejml.simple.SimpleMatrix;

public abstract class RotMatrix {

    private static Matrix rot;

    public static Matrix getRotX() {
        return RotMatrix.rot;
    }

}

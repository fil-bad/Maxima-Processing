package processingElement;
import org.ejml.simple.SimpleMatrix;
import org.ejml.dense.row.DMatrixComponent;

public interface Obj3D {
    void draw();
    void highlight(boolean b);

    SimpleMatrix getD();         // Position risp R0
    double getR();         // Rotation risp R0
    void setD(SimpleMatrix m);   // Position risp R0
    void setR(double rad);       // Rotation risp R0
}

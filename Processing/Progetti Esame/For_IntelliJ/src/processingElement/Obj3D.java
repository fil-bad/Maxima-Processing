package processingElement;
import org.ejml.simple.SimpleMatrix;
import org.ejml.dense.row.DMatrixComponent;

public interface Obj3D {
    void draw();
    SimpleMatrix getD();         // Position risp R0
    SimpleMatrix getR();         // Rotation risp R0
    void setD(SimpleMatrix m);   // Position risp R0
    void setR(SimpleMatrix m);   // Rotation risp R0
    void highlight();
}

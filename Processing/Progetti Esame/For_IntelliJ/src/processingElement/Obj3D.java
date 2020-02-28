package processingElement;

import org.ejml.simple.SimpleMatrix;

public interface Obj3D {
    void draw();

    void highlight(boolean b);

    void highlight();           //inverte a ogni chiamata

    SimpleMatrix getD();                        // Position risp R0

    double getR();                              // Rotation risp R0

    void setD(SimpleMatrix m);                  // Position risp R0

    void setD(float x, float y, float z);

    void addD(SimpleMatrix m);                  // Position risp R0

    void addD(float x, float y, float z);       // Rotation risp R0

    void setR(double rad);       // Rotation risp R0

    void addR(double rad);       // Rotation risp R0
}

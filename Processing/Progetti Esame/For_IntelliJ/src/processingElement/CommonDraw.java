package processingElement;

import processing.core.PApplet;

public class CommonDraw extends PApplet {

    private static CommonDraw instance = new CommonDraw();

    private CommonDraw() {
    }

    public static CommonDraw getInstance() {
        return instance;
    }

    // Funzione visualizzazione asse
    private int p = 100;   //profondità
    private int b = 10;    //base
    private int h = 10;     //altezza
    private int lF = 10;   //lunghezza semi lato freccia

    public void assi(float alfa) {
        pushStyle();
        strokeWeight((float) 0.5);
        fill(255, 0, 0, alfa); // rosso = x
        pushMatrix();
        rotateY(PI / 2);
        translate(0, 0, p >> 1);  //disegno in base
        box(h, b, p);
        translate(0, 0, p >> 1);  //sposto origine alla fine
        piramide(lF);
        popMatrix();

        fill(0, 255, 0, alfa); // verde = y
        pushMatrix();
        rotateX(-PI / 2);
        translate(0, 0, p >> 1);  //disegno in base
        box(h, b, p);
        translate(0, 0, p >> 1);  //sposto origine alla fine
        piramide(lF);
        popMatrix();

        fill(0, 0, 255, alfa); // blu = z
        pushMatrix();
        translate(0, 0, p >> 1);  //disegno in base
        box(h, b, p);
        translate(0, 0, p >> 1);  //sposto origine alla fine
        piramide(lF);
        popMatrix();
        popStyle();
    }

    private void piramide(int h) {
        beginShape();
        vertex(-h, -h);
        vertex(+h, -h);
        vertex(0, 0, 2 * h);
        endShape(CLOSE);

        beginShape();
        vertex(+h, -h);
        vertex(+h, +h);
        vertex(0, 0, 2 * h);
        endShape(CLOSE);

        beginShape();
        vertex(+h, +h);
        vertex(-h, +h);
        vertex(0, 0, 2 * h);
        endShape(CLOSE);

        beginShape();
        vertex(-h, +h);
        vertex(-h, -h);
        vertex(0, 0, 2 * h);
        endShape(CLOSE);

        beginShape();
        vertex(-h, -h);
        vertex(+h, -h);
        vertex(+h, +h);
        vertex(-h, +h);
        endShape(CLOSE);
    }
}

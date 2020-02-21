package processingElement;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;

import java.util.concurrent.ExecutionException;

public class CommonDraw {

    private static CommonDraw instance = null;
    PApplet win = null;

    private CommonDraw(PApplet win) {
        this.win = win;
    }

    public static CommonDraw getInstance() throws RuntimeException {
        if (instance == null)
            throw new RuntimeException("Impossible generate object, unknown windows (PApplet)");
        return instance;
    }

    public static CommonDraw getInstance(PApplet win) {
        if (instance == null)
            instance = new CommonDraw(win);
        return instance;
    }

    public int[] hexToRGB(String colorStr) {
        // NECESSARIA, Java non può prendere un Hex direttamente, ma deve scomporre
        int[] RGBArray = new int[3];

        RGBArray[0] =  Integer.valueOf( colorStr.substring( 1, 3 ), 16 );
        RGBArray[1] =  Integer.valueOf( colorStr.substring( 3, 5 ), 16 );
        RGBArray[2] =  Integer.valueOf( colorStr.substring( 5, 7 ), 16 );
        return RGBArray;
    }

    // Funzione visualizzazione asse
    private int p = 50;   //profondità
    private int b = 5;    //base
    private int h = 5;     //altezza
    private int lF = 5;   //lunghezza semi lato freccia

    public void assi(float alfa) {
        win.pushStyle();
        win.strokeWeight(0.5f);
        win.fill(255, 0, 0, alfa); // rosso = x
        win.pushMatrix();
        win.rotateY(win.PI / 2);
        win.translate(0, 0, p / 2.0f);  //disegno in base
        win.box(h, b, p);
        win.translate(0, 0, p / 2.0f);  //sposto origine alla fine
        piramide(lF);
        win.popMatrix();

        win.fill(0, 255, 0, alfa); // verde = y
        win.pushMatrix();
        win.rotateX(-win.PI / 2.0f);
        win.translate(0, 0, p / 2.0f);  //disegno in base
        win.box(h, b, p);
        win.translate(0, 0, p / 2.0f);  //sposto origine alla fine
        piramide(lF);
        win.popMatrix();

        win.fill(0, 0, 255, alfa); // blu = z
        win.pushMatrix();
        win.translate(0, 0, p / 2.0f);  //disegno in base
        win.box(h, b, p);
        win.translate(0, 0, p / 2.0f);  //sposto origine alla fine
        piramide(lF);
        win.popMatrix();
        win.popStyle();
    }

    public void piramide(int h) {
        win.beginShape();
        win.vertex(-h, -h);
        win.vertex(+h, -h);
        win.vertex(0, 0, 2 * h);
        win.endShape(win.CLOSE);

        win.beginShape();
        win.vertex(+h, -h);
        win.vertex(+h, +h);
        win.vertex(0, 0, 2 * h);
        win.endShape(win.CLOSE);

        win.beginShape();
        win.vertex(+h, +h);
        win.vertex(-h, +h);
        win.vertex(0, 0, 2 * h);
        win.endShape(win.CLOSE);

        win.beginShape();
        win.vertex(-h, +h);
        win.vertex(-h, -h);
        win.vertex(0, 0, 2 * h);
        win.endShape(win.CLOSE);

        win.beginShape();
        win.vertex(-h, -h);
        win.vertex(+h, -h);
        win.vertex(+h, +h);
        win.vertex(-h, +h);
        win.endShape(win.CLOSE);
    }

    public void setEnvMatrix(SimpleMatrix Q){
        win.applyMatrix((float)Q.get(0,0),(float)Q.get(0,1),(float)Q.get(0,2),(float)Q.get(0,3),
                (float)Q.get(1,0),(float)Q.get(1,1),(float)Q.get(1,2),(float)Q.get(1,3),
                (float)Q.get(2,0),(float)Q.get(2,1),(float)Q.get(2,2),(float)Q.get(2,3),
                (float)Q.get(3,0),(float)Q.get(3,1),(float)Q.get(3,2),(float)Q.get(3,3));
    }
}

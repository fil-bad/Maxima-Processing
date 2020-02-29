package processingElement;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;

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

        RGBArray[0] = Integer.valueOf(colorStr.substring(1, 3), 16);
        RGBArray[1] = Integer.valueOf(colorStr.substring(3, 5), 16);
        RGBArray[2] = Integer.valueOf(colorStr.substring(5, 7), 16);
        return RGBArray;
    }

    // Funzione visualizzazione asse
    private int p = 50;   //profondità
    private int b = 5;    //base
    private int h = 5;     //altezza
    private int lf = 5;   //lunghezza semi lato freccia

    public void axes(float alpha) {
        win.pushStyle();
        win.strokeWeight(0.5f);
        win.fill(255, 0, 0, alpha); // rosso = x
        win.pushMatrix();
        win.rotateY(win.PI / 2);
        win.translate(0, 0, p / 2.0f);  //disegno in base
        win.box(h, b, p);
        win.translate(0, 0, p / 2.0f);  //sposto origine alla fine
        pyramid(lf);
        win.popMatrix();

        win.fill(0, 255, 0, alpha); // verde = y
        win.pushMatrix();
        win.rotateX(-win.PI / 2.0f);
        win.translate(0, 0, p / 2.0f);  //disegno in base
        win.box(h, b, p);
        win.translate(0, 0, p / 2.0f);  //sposto origine alla fine
        pyramid(lf);
        win.popMatrix();

        win.fill(0, 0, 255, alpha); // blu = z
        win.pushMatrix();
        win.translate(0, 0, p / 2.0f);  //disegno in base
        win.box(h, b, p);
        win.translate(0, 0, p / 2.0f);  //sposto origine alla fine
        pyramid(lf);
        win.popMatrix();
        win.popStyle();
    }

    public void pyramid(int h) {
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

    void drawBoxBase(float p, float lb, float lh) {
        //p= lunghezza profondità, in Z
        //lp= larghezza base
        //lh= altezza base
        lb = lb / 2;
        win.strokeWeight(1);
        win.fill(255, 0, 0);
        win.beginShape();
        win.vertex(+lb, +lb, 0);
        win.vertex(+lb, +lb, p);
        win.vertex(+lb, -lb, p);
        win.vertex(+lb, -lb, 0);
        win.endShape(win.CLOSE);

        win.fill(255, 255, 0);
        win.beginShape();
        win.vertex(+lb, -lb, 0);
        win.vertex(+lb, -lb, p);
        win.vertex(-lb, -lb, p);
        win.vertex(-lb, -lb, 0);
        win.endShape(win.CLOSE);

        win.fill(0, 255, 0);
        win.beginShape();
        win.vertex(-lb, -lb, 0);
        win.vertex(-lb, -lb, p);
        win.vertex(-lb, +lb, p);
        win.vertex(-lb, +lb, 0);
        win.endShape(win.CLOSE);

        win.fill(0, 255, 255);
        win.beginShape();
        win.vertex(-lb, +lb, 0);
        win.vertex(-lb, +lb, p);
        win.vertex(+lb, +lb, p);
        win.vertex(+lb, +lb, 0);
        win.endShape(win.CLOSE);

        //bot face
        win.fill(0, 0, 255);
        win.beginShape();
        win.vertex(+lb, +lb, 0);
        win.vertex(+lb, -lb, 0);
        win.vertex(-lb, -lb, 0);
        win.vertex(-lb, +lb, 0);
        win.endShape(win.CLOSE);

        //top face
        win.beginShape();
        win.vertex(+lb, +lb, p);
        win.vertex(+lb, -lb, p);
        win.vertex(-lb, -lb, p);
        win.vertex(-lb, +lb, p);
        win.endShape(win.CLOSE);

        win.translate(0, 0, p);  //sposto origine alla fine
    }

    float drawCylinder(int sides, float r, float h, boolean plane) {
        //ritorna distanza tra origine e lato piano
        win.noStroke();
        win.colorMode(win.HSB, 360, 100, 100);
        int n = 0;
        if (plane)
            n = 4;    //45° piani
//        int n = 4 * win.int(plane); //45° piatti
        float angle = (float) 360.0 / sides;
        float halfHeight = h / 2;
        win.rotateZ(-win.PI / 2);

        // draw top of the tube
        win.beginShape();
        for (int i = n / 2; i < sides - n / 2 + 1; i++) {
            float x = PApplet.cos(PApplet.radians(i * angle)) * r;
            float y = PApplet.sin(PApplet.radians(i * angle)) * r;
            win.vertex(x, y, -halfHeight);
        }
        win.endShape(win.CLOSE);

        // draw bottom of the tube
        win.beginShape();
        for (int i = n / 2; i < sides - n / 2 + 1; i++) {
            float x = PApplet.cos(PApplet.radians(i * angle)) * r;
            float y = PApplet.sin(PApplet.radians(i * angle)) * r;
            win.vertex(x, y, halfHeight);
        }
        win.endShape(win.CLOSE);

        // draw sides
        for (int i = n / 2; i < sides - n / 2; i++) {
            win.fill(i * angle, 100, 100);
            win.beginShape();
            float x = PApplet.cos(PApplet.radians(i * angle)) * r;
            float y = PApplet.sin(PApplet.radians(i * angle)) * r;
            win.vertex(x, y, -halfHeight);
            win.vertex(x, y, +halfHeight);

            x = PApplet.cos(PApplet.radians((i + 1) * angle)) * r;
            y = PApplet.sin(PApplet.radians((i + 1) * angle)) * r;
            win.vertex(x, y, +halfHeight);
            win.vertex(x, y, -halfHeight);
            win.endShape(win.CLOSE);
        }

        //draw trunk plane
        win.beginShape();
        float x = PApplet.cos(PApplet.radians(n / 2.0f * angle)) * r;
        float y = PApplet.sin(PApplet.radians(n / 2.0f * angle)) * r;
        win.vertex(x, y, -halfHeight);
        win.vertex(x, y, +halfHeight);
        x = PApplet.cos(PApplet.radians((sides - n / 2.0f) * angle)) * r;
        y = PApplet.sin(PApplet.radians((sides - n / 2.0f) * angle)) * r;
        win.vertex(x, y, +halfHeight);
        win.vertex(x, y, -halfHeight);
        win.endShape(win.CLOSE);

        win.colorMode(win.RGB);
        win.stroke(0);
        return x;
    }

    void pinza(float w, float h, float p, float open) {
        win.translate(0, 0, h / 2);
        win.box(h, w, h); // Disegno il primo elemento della pinza
        win.translate(0, 0, h / 2);

        float distP = win.map(open, 0, 1, h / 2, w / 2 - h / 2);
        win.pushMatrix(); // Memorizzo il sistema attuale
        win.translate(0, distP, (p - h) / 2);
        win.fill(255, 0, 0);
        win.box(h, h, p - h); // Disegno il primo elemento della pinza
        win.popMatrix();  // Ritorno al sistema di riferimento memorizzato
        win.pushMatrix(); // Memorizzo il sistema attuale
        win.translate(0, -distP, (p - h) / 2);
        win.fill(0, 255, 0);
        win.box(h, h, p - h); // Disegno il primo elemento della pinza
        win.popMatrix();  // Ritorno al sistema di riferimento memorizzato
        win.translate(0, 0, p - h);
    }

    public void setEnvMatrix(SimpleMatrix Q) {
        win.applyMatrix((float) Q.get(0, 0), (float) Q.get(0, 1), (float) Q.get(0, 2), (float) Q.get(0, 3),
                (float) Q.get(1, 0), (float) Q.get(1, 1), (float) Q.get(1, 2), (float) Q.get(1, 3),
                (float) Q.get(2, 0), (float) Q.get(2, 1), (float) Q.get(2, 2), (float) Q.get(2, 3),
                (float) Q.get(3, 0), (float) Q.get(3, 1), (float) Q.get(3, 2), (float) Q.get(3, 3));
    }
}

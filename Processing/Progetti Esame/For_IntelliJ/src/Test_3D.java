import processing.core.*;
import java.lang.Object;

public class Test_3D extends PApplet {

    public static void main(String[] args) {
        PApplet.main("Test_3D");
    }

    //il prof traduce gli spostamenti del mouse come rotazioni
    //nord-sud rotazione lungo X
    //est-ovest rotazione lungo Z
    //così facendo si può montare una qualunque rotazione

    private float angX, angY;        //nuova rotazione
    private float angXst, angYst;    //sommatorie delle rotazioni effettuate

    private float q1, q2, q3;
    private float q1r, q2r, q3r;
    private float dt = (float) 0.02;    // necessario cast esplicito, altrimenti Java
    private float passo = (float) 0.5;  // vede i numeri con la virgola solo come double
    private int giunto = 1;

 //  private int[] hexToRGB(String colorStr) {
 //      // NECESSARIA, Java non può prendere un Hex direttamente, ma deve scomporre
 //      int[] RGBArray = new int[3];
 //
 //      RGBArray[0] =  Integer.valueOf( colorStr.substring( 1, 3 ), 16 );
 //      RGBArray[1] =  Integer.valueOf( colorStr.substring( 3, 5 ), 16 );
 //      RGBArray[2] =  Integer.valueOf( colorStr.substring( 5, 7 ), 16 );
 //      return RGBArray;
 //  }



    @Override
    public void settings() {
        // TODO: Customize screen size and so on here
        size(450, 450, P3D);
    }

    @Override
    public void setup() {
        // TODO: Your custom drawing and setup on applet start belongs here
        clear();
    }

    @Override
    public void draw() {
        // TODO: Do your drawing for each frame here
        clear();
        int[] rgbBackGr = {150, 252, 250};//hexToRGB("#96FCFA");
        background(rgbBackGr[0], rgbBackGr[1], rgbBackGr[2]);
        translate(225, 255, -200);
        rotateY(-angY);
        rotateX(angX);
        rotateX(+PI / 2);

        assi(150);

        directionalLight(223, 126, 126, 0, 0, (float) 0.7);
        ambientLight(200, 200, 200);

        q1 = q1 - dt * (q1 - q1r);
        q2 = q2 - dt * (q2 - q2r);
        q3 = q3 - dt * (q3 - q3r);

        robot(q1, q2, q3, 255);
        robot(q1r, q2r, q3r, 120);
    }

    void robot(float q1, float q2, float q3, int alpha) {
        pushMatrix();
        link(q1, 100, PI / 2, 0, alpha);
        assi(alpha >> 1);
        link(q2, 0, 0, 100, alpha);
        assi(alpha >> 1);
        link(0, q3, 0, 100, alpha);
        assi(alpha >> 1);
        popMatrix();
    }

    void link(float theta, float d, float alfa, float a, int alpha) {
        //disegno seguendo l'ordine dei passi di D-H

        int[] rgbFillBody = {82, 190, 255};//hexToRGB("#52BEFF");
        fill(rgbFillBody[0], rgbFillBody[1], rgbFillBody[2], alpha);
        rotateZ(theta);
        noStroke();
        sphere(25);
        stroke(0);
        translate(0, 0, d / 2);
        box(25, 25, d);
        translate(0, 0, d / 2);
        noStroke();
        sphere(25);
        stroke(0);

        int[] rgbFillHead = {255, 82, 82};//hexToRGB("#FF5252");
        fill(rgbFillHead[0], rgbFillHead[1], rgbFillHead[2], alpha);
        rotateX(alfa);
        noStroke();
        sphere(25);
        stroke(0);
        translate(a / 2, 0, 0);
        box(a, 25, 25);
        translate(a / 2, 0, 0);
        noStroke();
        sphere(25);
        stroke(0);
    }


    //Camera function:
    @Override
    public void mousePressed() {
        angYst = angY + PI * mouseX /500; //old ang+ new ang[radianti*mouseX/fattore_di_scala]
        angXst = angX + PI * mouseY /500; //old ang+ new ang[radianti*mouseX/fattore_di_scala]
    }

    @Override
    public void mouseDragged() {
        angY = angYst - PI * mouseX /500; //old ang+ new ang[radianti*mouseX/fattore_di_scala]
        angX = angXst - PI * mouseY /500; //old ang+ new ang[radianti*mouseX/fattore_di_scala]
    }

    @Override
    public void keyPressed() {
        if (key == 'r') {
            angX = 0;
            angY = 0;
            angXst = 0;
            angYst = 0;
        }
        if (key == '1')
            giunto = 1;
        if (key == '2')
            giunto = 2;
        if (key == '3')
            giunto = 3;

        if (giunto == 1) {
            if (keyCode == LEFT)
                q1r += passo;
            if (keyCode == RIGHT)
                q1r -= passo;
        } else if (giunto == 2) {
            if (keyCode == LEFT)
                q2r += passo;
            if (keyCode == RIGHT)
                q2r -= passo;
        } else if (giunto == 3) {
            if (keyCode == LEFT)
                q3r += passo * 10;
            if (keyCode == RIGHT)
                q3r -= passo * 10;
        }

        //if (key=='q')
        //  q1r+=passo;
        //if (key=='w')
        //  q1r-=passo;
        //if (key=='a')
        //  q2r+=passo;
        //if (key=='s')
        //  q2r-=passo;
        //if (key=='z')
        //  q3r+=passo;
        //if (key=='x')
        //  q3r-=passo;
    }


    int p = 100;   //profondità
    int b = 10;    //base
    int h = 10;     //altezza
    int lF = 10;   //lunghezza semi lato freccia

    private void assi(float alfa) {
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

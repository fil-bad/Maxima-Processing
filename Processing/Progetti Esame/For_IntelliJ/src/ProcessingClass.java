
import processing.core.*;
import processingElement.*;
public class ProcessingClass extends PApplet {



    public static void main(String[] args) {
        PApplet.main("ProcessingClass");
    }

    @Override
    public void settings() {
        // TODO: Customize screen size and so on here
        size(1200, 800, P3D);
    }

    Box ob;
    CommonDraw com;
    @Override
    public void setup() {
        // TODO: Your custom drawing and setup on applet start belongs here
        clear();
        cameraInit();

        ob = new Box(this, 10,10,10);
        com = CommonDraw.getInstance();
    }



    @Override
    public void draw() {
        // TODO: Do your drawing for each frame here
        clear();
        cameraSet();
        pushMatrix();
        if(com == null)
            println("nullo");
        else
            println("ok");
        //com.assi(150);
        popMatrix();

        //Oggetti da graficare
        fill(0,255,0);
        box(400,800,1);
        translate(100,100,0);
        if(ob == null)
            println("nullo");
        ob.draw();
    }

    @Override
    public void keyPressed() {
        if(key == 'r'){
            cameraInit();
        }
    }



    private int[] hexToRGB(String colorStr) {
        // NECESSARIA, Java non può prendere un Hex direttamente, ma deve scomporre
        int[] RGBArray = new int[3];

        RGBArray[0] =  Integer.valueOf( colorStr.substring( 1, 3 ), 16 );
        RGBArray[1] =  Integer.valueOf( colorStr.substring( 3, 5 ), 16 );
        RGBArray[2] =  Integer.valueOf( colorStr.substring( 5, 7 ), 16 );
        return RGBArray;
    }
    private float eyeX,eyeY,eyeZ;
    private float centerX,centerY, centerZ;
    private float Zrot;

    // Set camera e sistema ortonormale destro
    private void cameraInit(){
        eyeX = 0.0f;
        eyeY = 400.f;
        eyeZ = 220.0f;

        centerX = 0.0f;
        centerY = 0.0f;
        centerZ = 0.0f;

        Zrot = 0.0f;
    }
    private void cameraSet(){
        //Fondale da disegnare
        int[] rgbBackGr = hexToRGB("#96FCFA");
        background(rgbBackGr[0], rgbBackGr[1], rgbBackGr[2]);
        directionalLight(223, 126, 126, 0, 0, (float) 0.7);
        ambientLight(200, 200, 200);

        if (mousePressed && (mouseButton == LEFT)) {    // zoom e rotazione
            float x,y,z,dn;
            x = eyeX-centerX;
            y = eyeY-centerY;
            z = eyeZ - centerZ;
            dn = dist(0,0,0,x,y,z);
            x /=dn;
            y /=dn;
            z /=dn;
            float d = dist(eyeX,eyeY,eyeZ,centerX,centerY,centerZ);
            d += mouseY - pmouseY;

            eyeX = centerX + x * d;
            eyeY = centerY + y * d;
            eyeZ = centerZ + z * d;
            Zrot += (mouseX - pmouseX)/50.0;

        } else if (mousePressed && (mouseButton == RIGHT)) {    // traslazione xy
            eyeX -= (mouseX - pmouseX)/2.0;
            centerX -= (mouseX - pmouseX)/2.0;
            eyeY -= (mouseY - pmouseY)/2.0;
            centerY -= (mouseY - pmouseY)/2.0;
        } else if (mousePressed && (mouseButton == CENTER)){    // inclinazione lungo Z
            centerZ += mouseY - pmouseY;
        }
        if (centerZ+10 >eyeZ )
            centerZ = eyeZ -15;

        camera(eyeX, eyeY, eyeZ, // eyeX, eyeY, eyeZ
                centerX, centerY,centerZ, // centerX, centerY, centerZ
                0.0f, 1.0f, 0.0f); // upX, upY, upZ

        //Ortonormale Destro
        rotateZ(PI/2);
        scale(1,-1,1);
        rotateZ(Zrot);
    }

    // Funzione visualizzazione asse
    private int p = 100;   //profondità
    private int b = 10;    //base
    private int h = 10;     //altezza
    private int lF = 10;   //lunghezza semi lato freccia

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

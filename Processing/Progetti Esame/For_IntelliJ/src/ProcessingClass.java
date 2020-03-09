import geometry.Vertex;
import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import processing.event.MouseEvent;
import processingElement.*;
import robots.Cartesian;
import robots.Rover;


public class ProcessingClass extends PApplet {


    // Dati per il disegno del mondo
    CommonDraw com;
    Pointer point;
    SceneExpert scene;
    Obstacle selected = null;
    Vertex addPoint; // variabile per calcolare di quanto spostare il puntatore
    private float eyeX, eyeY, eyeZ;
    private float centerX, centerY, centerZ;
    private float Zrot, XRot, zoom;

    int giunto = 0;

    public static void main(String[] args) {
        PApplet.main("ProcessingClass");
    }

    @Override
    public void settings() {
        // Customize screen size and so on here
        size(1200, 720, P3D);
    }

    @Override
    public void setup() {
        clear();
        cameraInit();
        frameRate(60);
        com = CommonDraw.getInstance(this); // must be the first


        //Cursore a schermo
        point = new Pointer(this, 60, 800, 400);

        //Classe di supporto con funzioni standard

        //Setup della scena
        scene = SceneExpert.getInstance(this);

        scene.addGnd(new Terra(this, 800, 400, color(200, 150, 100)));

        scene.addObstacle(new Box(this, 50, 40, 10, color(255, 150, 0, 100)), 50);
        scene.addObstacle(new Box(this, 50, 40, 10, color(0, 255, 0, 100)), 150, -60, 0);

        scene.setRover(new Rover(this, new Vertex(100, 100), 0.1, 0.15, 0.03));
        scene.setRobot(new Cartesian(this, 10));

    }

    float[] pObj = new float[4];

    @Override
    public void draw() {
        clear();
        cameraSet();

        if (keyPressed) {

            if (key == '+') pObj[giunto] += 1;
            if (key == '-') pObj[giunto] -= 1;

            if (key == 'i' || key == 'I') {
                System.out.println("Iniziato calcolo cinematica inversa");
                SimpleMatrix newQ = scene.getRobot().inverse(pObj[0], pObj[1], pObj[2], radians(pObj[3]));
                scene.getRobot().setqObj(newQ);
//                SimpleMatrix newQ = scene.getRobot().inverse(pObj[0], pObj[1], pObj[2], radians(pObj[3]));
//                scene.getRobot().setq(newQ);
            }
        }

        if (selected != null) {
            selected.setD(point.getX(), point.getY(), 0);
        }

        //Oggetti da graficare
        scene.drawScene();
        point.draw();

        push();
        translate((float) scene.getRover().get().getX() + pObj[0], (float) scene.getRover().get().getY() + pObj[1], pObj[2]);
        fill(180);
        rotateZ(radians(pObj[3]));
        box(10);
        pop();

    }

    @Override
    public void mouseClicked() {
        if (mouseButton == LEFT) {
            if (selected != null) {             // se qualcosa è selezionato
                selected.highlight(false);
                selected.setD(point.getX(), point.getY(), 0);
                selected = null;
            } else {                            // nulla è selezionato
                Obstacle newSelected = scene.getObstacle(point.get());
                if (newSelected == null)
                    selected = null;
                else {
                    selected = newSelected;
                    selected.highlight(true);

                }
            }
        }
        if (mouseButton == RIGHT) {
            if (selected != null) {
                SimpleMatrix dObj;
                dObj = selected.getD().minus(scene.getRover().getD());
                pObj[0] = (float) dObj.get(0);
                pObj[1] = (float) dObj.get(1);
                pObj[2] = (float) -dObj.get(2);
                pObj[3] = degrees((float) selected.getR());
            }
        }
    }


    @Override
    public void mouseWheel(MouseEvent event) {
        if (selected != null) {
            selected.addR(radians(event.getCount() * 5));
        } else
            XRot += event.getCount() / 10.0;
        if (XRot < radians(-45))
            XRot = radians(-45);
        if (XRot > radians(135))
            XRot = radians(135);
    }

    @Override
    public void keyPressed() {
        if (key == 'r' || key == 'R') {
            cameraInit();
        }

        if (key == 'e' || key == 'E') {
            if (scene.freePlace(point.get(), scene.robotR)) {
                scene.getRover().clearCheckPoint();
                scene.getRover().setCheckPoint(scene.getQtGraph().calcVert2Visit(scene.getRover().get(), point.get()));
            } else
                System.err.println("Il punto desiderato ha un ostacolo in un raggio:" + scene.robotR);
        }
        if (key == 'o' || key == 'O') {
            scene.addObstacle(new Box(this, (int) random(10, 100), (int) random(10, 100), (int) random(10, 30), color(random(255), random(255), random(255), 100)));
        }

        if (key >= '1' && key <= '4') giunto = key - '1';
    }

    // Set camera e sistema ortonormale destro
    private void cameraInit() {
        resetMatrix();
        eyeX = 0.0f;
        eyeY = 400.f;
        eyeZ = 400.f;

        centerX = 0.0f;
        centerY = 0.0f;
        centerZ = 0.0f;

        Zrot = radians(0);
        XRot = radians(0);
        zoom = 1;

        addPoint = new Vertex(0, 0);
    }

    private void cameraSet() {
        //Fondale da disegnare
        background(color(0x96FCFA));

        directionalLight(100, 100, 100, 0, 0, (float) 0.5);

        ambientLight(200, 200, 200);
        float d = dist(0, 0, 0, eyeX, eyeY, eyeZ);
//        com.axes(255);
        if (mousePressed && (mouseButton == LEFT)) {

        } else if (mousePressed && (mouseButton == RIGHT)) {    // traslazione xy
            addPoint.set(-(mouseX - pmouseX), -(mouseY - pmouseY));
            addPoint.rotate(Zrot);
            centerX += addPoint.getX() / 2.0;
            centerY += addPoint.getY() / 2.0;
        } else if (mousePressed && (mouseButton == CENTER)) {    // Seleziona
            float x, y, z;
            x = eyeX / d;
            y = eyeY / d;
            z = eyeZ / d;
            d += mouseY - pmouseY;
            if (d < 150)
                d = 150;
            eyeX = x * d;
            eyeY = y * d;
            eyeZ = z * d;

            Zrot += (mouseX - pmouseX) / 200.0;
        } else {
            addPoint.set((mouseX - pmouseX), -(mouseY - pmouseY));
            addPoint.rotate(-Zrot);
            point.addX((float) addPoint.getX() * d / 800);
            point.addY((float) addPoint.getY() * d / 800);
        }

        camera(eyeX, eyeY, eyeZ, // eyeX, eyeY, eyeZ
                0, 0, 0, // centerX, centerY, centerZ
                0.0f, 1.0f, 0.0f); // upX, upY, upZ

        //Ortonormale Destro
        //rotateZ(PI/2);
        scale(1, -1, 1);
        rotateX(XRot);
        rotateZ(Zrot);
        translate(-centerX, centerY, 0);

    }
}

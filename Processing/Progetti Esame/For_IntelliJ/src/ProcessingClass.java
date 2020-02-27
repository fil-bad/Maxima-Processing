import geometry.Vertex;
import graph.QTGraph;
import processing.core.*;
import processing.event.MouseEvent;
import processingElement.*;
import quadtree.Boundary;
import quadtree.QuadTree;


public class ProcessingClass extends PApplet {


    public static void main(String[] args) {
        PApplet.main("ProcessingClass");
    }

    @Override
    public void settings() {
        // TODO: Customize screen size and so on here
        size(1200, 720, P3D);
    }

    // Dati per il disegno del mondo
    CommonDraw com;
    Terra gnd;
    Pointer point;
    SceneExpert scene;

    // Dati per la pianificazione del percorso
    Vertex roverStart, roverEnd;
    Rover rover;


    QuadTree qt;
    QTGraph qtGraph;

    @Override
    public void setup() {
        clear();
        cameraInit();
        frameRate(60);


        com = CommonDraw.getInstance(this);
        gnd = new Terra(this, 800, 400, color(200, 150, 100));
        point = new Pointer(this, 60, 800, 400);
        scene = SceneExpert.getInstance();
        scene.addObstacle(new Box(this, 50, 40, 10, color(255, 150, 0, 100)), 50);
        scene.addObstacle(new Box(this, 50, 40, 10, color(0, 255, 0, 100)), 150, -60, 0);

        roverStart = new Vertex(-100, 100);
        roverEnd = new Vertex(100, -100);
        rover = new Rover(this, new Vertex(100, 100), 0.5, 0.5, 0.1);

    }

    Obstacle selected = null;

    @Override
    public void draw() {
        clear();
        cameraSet();

        //Oggetti da graficare
        gnd.draw();
        com.axes(255);
        point.draw();

        if (selected != null) {
            selected.setD(point.getX(), point.getY(), 0);
        }


        qt = new QuadTree(scene.getObstacles(), new Boundary(-400, -200, 400, 200), 10);
        qtGraph = new QTGraph(this, qt, SceneExpert.getInstance().robotR, SceneExpert.getInstance().getObstacles());


        QuadTree.dfs(qt, this);
        qtGraph.calcVert2Visit(rover.get(), roverEnd);

        qtGraph.printGraph(this, 10);
        qtGraph.printPath(this, 15);

        scene.drawScene();
        rover.draw();
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
//        if (key == 'd' || key == 'D') {
//
//        }
        if (key == 'e' || key == 'E') {
            roverEnd.set(point.get());
            qtGraph.calcVert2Visit(rover.get(), roverEnd);
            rover.clearObjs();
            rover.setObjs(qtGraph.getCheckPoint());
        }
        if (key == 'o' || key == 'O') {
            scene.addObstacle(new Box(this, (int) random(10, 100), (int) random(10, 100), (int) random(10, 30), color(random(255), random(255), random(255), 100)));
        }

    }


    private float eyeX, eyeY, eyeZ;
    private float centerX, centerY, centerZ;
    private float Zrot, XRot, zoom;
    Vertex addPoint; // variabile per calcolare di quanto spostare il puntatore

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
            centerX -= (mouseX - pmouseX) / 2.0;
            centerY -= (mouseY - pmouseY) / 2.0;
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

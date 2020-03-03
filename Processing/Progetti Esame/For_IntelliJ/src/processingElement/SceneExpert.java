package processingElement;

import geometry.Sat;
import geometry.Vertex;
import graph.QTGraph;
import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;
import quadtree.QuadTree;
import robots.Robot;
import robots.Rover;

import java.util.Vector;


public class SceneExpert implements Observer {
    protected static SceneExpert instance = null;
    public float robotR;
    PApplet win = null;
    CommonDraw com = CommonDraw.getInstance();
    boolean sceneChange = true;
    // Oggetti grafici della scena
    private Vector<Obj3D> bodies;
    private Vector<Obstacle> obs;
    private Terra gnd = null;
    // Informazioni della scena
    private QuadTree qt;
    private QTGraph qtGraph;
    //Parametri Robot
    private Rover rover;

    private Robot robot;


    protected SceneExpert(PApplet win) {
        bodies = new Vector<>();
        obs = new Vector<>();
        this.win = win;
    }

    public static SceneExpert getInstance() throws RuntimeException {
        if (instance == null)
            throw new RuntimeException("Impossible generate object, unknown windows (PApplet)");
        return instance;
    }

    public static SceneExpert getInstance(PApplet win) {
        if (instance == null)
            instance = new SceneExpert(win);
        return instance;
    }

    public void drawScene() {
        if (this.gnd != null)
            gnd.draw();
        for (Obj3D obj : bodies) {
            obj.draw();
        }
        if (sceneChange) {
            sceneChange = false;
            qt = new QuadTree(getObstacles(), getGnd().getBoundary(), robotR/3);
            qtGraph = new QTGraph(win, qt, robotR, getObstacles());
        }

        QuadTree.dfs(qt, win);
        qtGraph.printGraph(win, 10);
        qtGraph.printPath(win, 15);

        win.push();
        rover.draw();
        SimpleMatrix transl = rover.getD();
        win.translate((float) transl.get(0), (float) transl.get(1), (float) transl.get(2));
//        com.axes(255);
        robot.draw();
        win.pop();
    }

    public void addGnd(Terra g) {
        this.gnd = g;
    }

    public Terra getGnd() {
        return this.gnd;
    }

    public void setRover(Rover r) {
        this.rover = r;
        robotR = (float) r.getRadius();
    }

    public Rover getRover() {
        return this.rover;
    }

    public void setRobot(Robot r) {
        this.robot = r;
    }

    public Robot getRobot() {
        return this.robot;
    }

    public void addObstacle(Obstacle ob) {
        obs.add(ob);
        bodies.add(ob);
        ob.attachScene(this);
        sceneChange = true;
    }

    public void addObstacle(Obstacle ob, float x, float y, float z) {
        addObstacle(ob, x, y, z, 0);

    }

    public void addObstacle(Obstacle ob, float rad) {
        addObstacle(ob, 0, 0, 0, rad);
    }

    public void addObstacle(Obstacle ob, float x, float y, float z, float rad) {
        addObstacle(ob);
        ob.setD(x, y, z);
        ob.setR(rad);
    }
    // return the most high obstacle that have p inside
    // null if obstacle not exist at that point

    public Obstacle getObstacle(Vertex p) {
        Obstacle o = null;
        for (Obstacle ob : obs) {
            if (ob.getPoly().contains(p))
                if (o != null) {
                    if (o.getD().get(2) < ob.getD().get(2))
                        o = ob;
                } else {
                    o = ob;
                }
        }
        return o;
    }
    // true if the place is free from obstacle in a r radius

    public Obstacle[] getObstacles() {
        return obs.toArray(new Obstacle[0]);
    }

    public boolean freePlace(Vertex c, double r) {
        for (Obstacle ob : obs) {
            if (Sat.haveCollided(ob.getPoly(), c, r))
                return false;
        }
        return true;
    }

    public QTGraph getQtGraph() {
        return qtGraph;
    }


    @Override
    public void updateChange() {
        sceneChange = true;
    }
}

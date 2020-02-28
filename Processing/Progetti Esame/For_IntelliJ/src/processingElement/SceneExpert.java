package processingElement;

import java.util.Vector;

import geometry.Sat;
import geometry.Vertex;
import graph.QTGraph;
import processing.core.PApplet;
import quadtree.QuadTree;


public class SceneExpert implements Observer {
    protected static SceneExpert instance = null;
    PApplet win = null;

    // Oggetti grafici della scena
    private Vector<Obj3D> bodies;
    private Vector<Obstacle> obs;
    private Terra gnd = null;

    boolean sceneChange = true;

    // Informazioni della scena
    QuadTree qt;
    QTGraph qtGraph;

    //todo: Quando si avrà robot calcolarlo parametricamente
    public float robotR = 30;


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

    public void addGnd(Terra g) {
        this.gnd = g;
    }

    public Terra getGnd() {
        return this.gnd;
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
    public boolean freePlace(Vertex c, double r) {
        for (Obstacle ob : obs) {
            if (Sat.haveCollided(ob.getPoly(), c, r))
                return false;
        }
        return true;
    }

    public Obstacle[] getObstacles() {
        return obs.toArray(new Obstacle[0]);
    }

    public QTGraph getQtGraph() {
        return qtGraph;
    }


    public void drawScene() {
        if (this.gnd != null)
            gnd.draw();
        for (Obj3D obj : bodies) {
            obj.draw();
        }
        if (sceneChange) {
            sceneChange = false;
            qt = new QuadTree(getObstacles(), getGnd().getBoundary(), robotR);
            qtGraph = new QTGraph(win, qt, robotR, getObstacles());
        }

        QuadTree.dfs(qt, win);
        qtGraph.printGraph(win, 10);
        qtGraph.printPath(win, 15);


    }

    @Override
    public void updateChange() {
        sceneChange = true;
    }
}

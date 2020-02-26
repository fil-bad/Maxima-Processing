package processingElement;

import geometry.Sat;
import geometry.Vertex;
import processing.core.PApplet;

import java.util.Vector;

public class SceneExpert {
    Vector<Obj3D> bodies;
    Vector<Obstacle> obs;

    public SceneExpert() {
        bodies = new Vector<>();
        obs = new Vector<>();
    }

    public void addObstacle(Obstacle ob) {
        obs.add(ob);
        bodies.add(ob);
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

    public Obstacle[] getObstacles() {
        return obs.toArray(new Obstacle[0]);
    }


    public void drawScene() {
        for (Obj3D obj : bodies) {
            obj.draw();
        }
    }
}

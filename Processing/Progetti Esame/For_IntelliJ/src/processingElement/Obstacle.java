package processingElement;

import geometry.Polygon;

public interface Obstacle extends Obj3D, Subject {
    Polygon getPoly();

}

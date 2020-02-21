package quadtree;
import javaMisc.Polygon;
import javaMisc.Vertex;

/* Using two points of Rectangular (Top,Left) & (Bottom , Right)*/
class Boundry {


    public Boundry(double xMin, double yMin, double xMax, double yMax) {
        super();
        /*
         *  Storing two diagonal points
         */
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    public boolean inRange(int x, int y) {
        return (x >= this.getxMin() && x <= this.getxMax()
                && y >= this.getyMin() && y <= this.getyMax());
    }

    double xMin, yMin, xMax, yMax;

    public double getxMin() {
        return xMin;
    }

    public double getyMin() {
        return yMin;
    }

    public double getxMax() {
        return xMax;
    }

    public double getyMax() {
        return yMax;
    }

    public double getW(){ return (xMax-xMin);}
    public double getH(){ return (yMax-yMin);}

    public double getX(){ return getW()/2.0;}
    public double getY(){ return getH()/2.0;}

    public Polygon getPoly() throws Exception{
        Vertex[] v_s = {
                new Vertex(xMin, yMin),     // Sud-Ovest
                new Vertex(xMax, yMin),     // Sud-Est
                new Vertex(xMax, yMax),     // Nord-Est
                new Vertex(xMin, yMax),     // Nord-Ovest
        };
        Polygon p = new Polygon(v_s);
        return p;
    }

}
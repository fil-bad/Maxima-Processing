package quadTree;
import javaMisc.Polygon;
import javaMisc.Vertex;

/* Using two points of Rectangular (Top,Left) & (Bottom , Right)*/
public class Boundary {

    private double xMin, yMin, xMax, yMax;

    public Boundary(double xMin, double yMin, double xMax, double yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

//    public Boundary(double x, double y, double w, double h) {
//        this.xMin = x-w/2.0;
//        this.yMin = y-h/2.0;
//        this.xMax = x+w/2.0;
//        this.yMax = y+h/2.0;
//    }

    public boolean inRange(int x, int y) {
        return (x >= this.getxMin() && x <= this.getxMax()
                && y >= this.getyMin() && y <= this.getyMax());
    }


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

    public double getX(){ return xMin + getW()/2.0;}
    public double getY(){ return yMin + getH()/2.0;}

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
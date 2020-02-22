package quadtree;
import geometry.Polygon;
import geometry.Vertex;

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

    public Boundary getSector(Coord c){
        switch (c){
            case NE:
                return new Boundary(getX(), getY(), getxMax(),getyMax());
            case NW:
                return new Boundary(getxMin(), getY(), getX(), getyMax());
            case SW:
                return new Boundary(getxMin(), getyMin(), getX(), getY());
            case SE:
                return new Boundary(getX(), getyMin(), getxMax(), getY());
        }
        return null;
    }


    public double getxMin() {
        return xMin;
    }

    public double getMinExtension() {
        return Math.min(getW(),getH());
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

    public Polygon getPoly() throws RuntimeException{
        Vertex[] v_s = {
                new Vertex(xMin, yMin),     // Sud-Ovest
                new Vertex(xMax, yMin),     // Sud-Est
                new Vertex(xMax, yMax),     // Nord-Est
                new Vertex(xMin, yMax),     // Nord-Ovest
        };
        Polygon p = new Polygon(v_s);
        return p;
    }

    public String dataBoundary(){
        String a = String.format("[X1=%.2f Y1=%.2f] \t[X2=%.2f Y2=%.2f]\n",
                getxMin(), getyMin(), getxMax(), getyMax());
        return a;
    }

}
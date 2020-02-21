package quadTree;
import javaMisc.Polygon;
import javaMisc.Vertex;

/* Using two points of Rectangular (Top,Left) & (Bottom , Right)*/
class Boundry {
    public int getxMin() {
        return xMin;
    }

    public int getyMin() {
        return yMin;
    }

    public int getxMax() {
        return xMax;
    }

    public int getyMax() {
        return yMax;
    }

    public int getW(){ return (xMax-xMin);}
    public int getH(){ return (yMax-yMin);}

    public double getX(){ return getW()/2.0;}
    public double getY(){ return getH()/2.0;}

    public Polygon getPoly(){
        Vertex[] v_s = {
                new Vertex(xMin, yMin),     // Sud-Ovest
                new Vertex(xMax, yMin),     // Sud-Est
                new Vertex(xMax, yMax),     // Nord-Est
                new Vertex(xMin, yMax),     // Nord-Ovest
        };
        Polygon p = new Polygon(v_s);
        return p;
    }

    public Boundry(int xMin, int yMin, int xMax, int yMax) {
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

    int xMin, yMin, xMax, yMax;

}
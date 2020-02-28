package processingElement;

import processing.core.PApplet;
import quadtree.Boundary;

public class Terra {
    private PApplet win;
    private CommonDraw com;
    private int w, h;
    private int color;

    private int h_wall = 10;
    private int w_wall = 5;

    public Terra(PApplet win, int w, int h, int color) {
        this.win = win;
        this.w = w;
        this.h = h;
        this.color = win.color(color);
        this.color = color;
        this.com = CommonDraw.getInstance(win);
    }

    public void draw() {
        win.pushMatrix();
        win.pushStyle();

        //Pavimento
        win.fill(color);
        win.translate(0, 0, -1);
        win.box(w, h, 1);

        //Muri
        win.fill(156);
        win.translate(-w / 2.0f - w_wall / 2.0f, 0, h_wall / 2.0f);
        win.box(w_wall, h + 2 * w_wall, h_wall);

        win.translate(w + w_wall, 0);
        win.box(w_wall, h + 2 * w_wall, h_wall);

        win.translate(-w / 2.0f - w_wall / 2.0f, h / 2.0f + w_wall / 2.0f);
        win.box(w, w_wall, h_wall);

        win.translate(0, -h - w_wall);
        win.box(w, w_wall, h_wall);

        win.popMatrix();
        win.popStyle();
        com.axes(255);
        //Torna origine su faccia superiore
        //si può disegnare in 2D sulla faccia superiore
    }

    public Boundary getBoundary() {
        return new Boundary(-w / 2.0, -h / 2.0, w / 2.0, h / 2.0);
    }
}

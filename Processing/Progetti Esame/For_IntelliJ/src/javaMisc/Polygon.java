package javaMisc;

public class Polygon {

    private Vertex[] vertices;

    public Polygon(Vertex... vertexes){
        this.vertices = vertexes;
        /*
        Vertex[] arrVert = new Vertex[vertexes.length];
        int i = 0;
        for (Vertex elem: vertexes){
            arrVert[i] = elem;
            i++;
        }
        this.edges = arrVert;
    */
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public int numVertices(){
        return this.vertices.length;
    }

    public void printVertices(){
        System.out.print("{");
        for (Vertex elem: this.vertices){
            elem.printVertex();
        }
        System.out.println("}");
    }

    public static void main(String[] args){
        Vertex v1 = new Vertex(50, 12);
        Vertex v2 = v1.orthogonal();
        Polygon p1 = new Polygon(v1,v2);
        System.out.println("p1 = " + p1.numVertices());
        p1.printVertices();
        Vertex[] v_s = {v1, v2};
        Polygon p2 = new Polygon(v_s);
        System.out.println("p2 = " + p2.numVertices());
        p2.printVertices();
    }

}

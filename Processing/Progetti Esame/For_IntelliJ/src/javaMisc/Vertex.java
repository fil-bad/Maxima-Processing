package javaMisc;

public class Vertex {
        private double x, y;

        public Vertex(double x, double y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Returns a new vector which is orthogonal to the current vector
         */
        public Vertex orthogonal() {
            return new Vertex(y, -x);
        }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void printVertex() {
            System.out.print("[" + this.x + ";" + this.y + "]");
        }

        public static void main(String[] args){
            Vertex v1 = new Vertex(50, 12);
            v1.printVertex();
            Vertex v2 = v1.orthogonal();
            System.out.println("");
            System.out.print("orthogonal of v1:");
            v2.printVertex();
        }
}


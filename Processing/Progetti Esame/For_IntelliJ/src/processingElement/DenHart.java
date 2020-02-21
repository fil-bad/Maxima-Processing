package processingElement;
import static java.lang.Math.*;
import java.util.ArrayList;

public class DenHart {

    private ArrayList<Link> denHartTab;

    public DenHart(){
        this.denHartTab = new ArrayList<Link>(0);
    }

    public DenHart(ArrayList<Link> denHartTab){
        this.denHartTab = denHartTab;
    }

    public void addLink(Link link){
        //append a new link to D-H table
        this.denHartTab.add(link);
    }

    public void removeLink(){
        //remove last link (-> entry of D-H table)
        this.denHartTab.remove(denHartTab.size() - 1);
    }

    public int getNumDOF(){
        return this.denHartTab.size();
    }

    public void printDHTab() {
        for (Link l: this.denHartTab){
            l.printLink();
        }
    }

    public static void main(String[] args) {
        DenHart dh = new DenHart();
        dh.addLink(new RotLink("q1", 50, (float) PI, 0));
        dh.addLink(new PrismLink((float) PI/2, "q2", 0,20));
        dh.printDHTab();
    }

}

package robots;

import processingElement.DenHart;

public class Puma {

    private DenHart dhTab;

    public Puma(DenHart denHart) {
        this.dhTab = denHart;
    }

    public static void main(String[] args) {
        Puma puma = new Puma(new DenHart());
    }

    public DenHart getDhTab() {
        return dhTab;
    }

}

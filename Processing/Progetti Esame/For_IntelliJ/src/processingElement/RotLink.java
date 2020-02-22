package processingElement;

public class RotLink implements Link {

    private String theta_qi;
    private float theta = 0.0f;
    private float d;
    private float alpha;
    private float a;

    public RotLink(String qi, float d, float alpha, float a){
        this.theta_qi = qi;
        this.d = d;
        this.alpha = alpha;
        this.a = a;
    }

    @Override
    public float[] getCurrValues() {
        return new float[]{this.theta, this.d, this.alpha, this.a};
    }

    @Override
    public void setQ_iValue(float theta) {
        this.theta = theta;
    }

    @Override
    public void printLink() {
        System.out.printf("[%.3f;%.3f;%.3f;%.3f]\n", this.theta, this.d, this.alpha, this.a);
    }

    @Override
    public String whichQ_iIs() {
        return this.theta_qi;
    }
}

package javaMisc;

import javaMisc.Link;

public class PrismLink implements Link {

    private String d_qi;
    private float theta;
    private float d = 0.0f;
    private float alpha;
    private float a;

    public PrismLink(float theta, String qi, float alpha, float a) {
        this.theta = theta;
        this.d_qi = qi;
        this.alpha = alpha;
        this.a = a;
    }

    @Override
    public float[] getCurrValues() {
        return new float[]{this.theta, this.d, this.alpha, this.a};
    }

    @Override
    public void setQ_iValue(float q_i) {
        this.d = q_i;
    }

    @Override
    public void printLink() {
        System.out.printf("[%.3f;%.3f;%.3f;%.3f]\n", this.theta, this.d, this.alpha, this.a);
    }

    @Override
    public String whichQ_iIs() {
        return this.d_qi;
    }

    @Override
    public void update() {

    }
}

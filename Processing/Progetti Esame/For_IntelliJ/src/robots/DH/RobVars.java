package robots.DH;

import org.ejml.simple.SimpleMatrix;
import robots.DH.math.DoubleReal;
import robots.DH.math.autodiff.Variable;

import java.util.LinkedList;

public class RobVars {

    private LinkedList<Variable<DoubleReal>> vars = null;


    public RobVars() {
        vars = new LinkedList<Variable<DoubleReal>>();
    }

    public void addVar(Variable<DoubleReal> v) {
        vars.addLast(v);
    }

    public void addVarIndex(Variable<DoubleReal> v, int i) {
        vars.add(i, v);
    }

    public void setVars(double... vals) {
        assert (vals.length == this.vars.size()); //we have to update all variables at once
        int i = 0;
        for (Variable<DoubleReal> var : this.vars) {
            var.set(new DoubleReal(vals[i]));
            i++;
        }
    }

    public void setVars(String qi, double val) {
        getVar(qi).set(new DoubleReal(val));
//        for (Variable<DoubleReal> var : this.vars) {
//            if (qi.equals(var.toString())) {
//                var.set(new DoubleReal(val));
//                break;
//            }
//        }
//        System.err.println("Variable not Found!");
    }

    public void setVars(int index, double val) {
        getVar(index).set(new DoubleReal(val));
    }

    public Variable<DoubleReal>[] getVar() {
        return vars.toArray(new Variable[0]);
    }

    public SimpleMatrix get_qVect() {
        SimpleMatrix q = new SimpleMatrix(vars.size(), 1);
        Variable<DoubleReal>[] var = getVar();
        for (int i = 0; i < var.length; i++) {
            Variable<DoubleReal> v = var[i];
            q.set(i, v.getValue().doubleValue());
        }
        return q;
    }

    public Variable<DoubleReal> getVar(int index) {
        assert (index < this.vars.size()); //we have to update all variables at once
        return vars.get(index);
    }

    public Variable<DoubleReal> getVar(String qi) {
        for (Variable<DoubleReal> var : this.vars) {
            if (qi.equals(var.toString()))
                return var;
        }
        System.err.println("Variable not Found!");
        return null;
    }

    public int varSize() {
        return vars.size();
    }

    public void printVar() {
        get_qVect().print();
    }


}

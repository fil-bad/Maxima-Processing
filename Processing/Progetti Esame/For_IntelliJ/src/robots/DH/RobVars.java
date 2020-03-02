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

    /**
     * Manipolate variable space
     **/
    public void addVar(Variable<DoubleReal> v) {
        vars.addLast(v);
    }

    public void addVarIndex(Variable<DoubleReal> v, int i) {
        vars.add(i, v);
    }

    public void mergeVar_s(RobVars new_vars) {
        if (new_vars == null) return;
        for (Variable<DoubleReal> n_v : new_vars.getVar()) {
            boolean occur = false;
            for (Variable<DoubleReal> v : this.getVar()) {
                if (n_v.equals(v)) occur = true;
            }
            if (!occur) this.addVar(n_v);
        }
    }

    public Variable<DoubleReal> removeLastVar() {
        return vars.removeLast();
    }

    public void removeVar(Variable<DoubleReal> v) {
        vars.remove(v);
    }

    public Variable<DoubleReal> removeVarIndex(int i) {
        return vars.remove(i);
    }

    public void clearVar() {
        vars.clear();
    }

    /**
     * Direct setting variable
     **/
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
    }

    public void setVars(int index, double val) {
        getVar(index).set(new DoubleReal(val));
    }

    /**
     * Get the Variable reference
     **/
    public Variable<DoubleReal>[] getVar() {
        return vars.toArray(new Variable[0]);
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

    public Variable<DoubleReal> getVar(Variable<DoubleReal> v) {
        return vars.get(vars.indexOf(v));
    }


    /**
     * Query at variable
     **/

    public SimpleMatrix get_qVect() {
        SimpleMatrix q = new SimpleMatrix(vars.size(), 1);
        Variable<DoubleReal>[] var = getVar();
        for (int i = 0; i < var.length; i++) {
            Variable<DoubleReal> v = var[i];
            q.set(i, v.getValue().doubleValue());
        }
        return q;
    }

    public String[] getVarsName() {
        if (this.vars.size() == 0) return null;
        return this.vars.toArray(new String[vars.size()]);
    }

    public int varSize() {
        return vars.size();
    }


    /**
     * Debug
     **/
    public void printVar() {
        get_qVect().print();
    }


}

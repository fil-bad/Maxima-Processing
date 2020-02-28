package javaMisc;

import javaMisc.math.DoubleReal;
import javaMisc.math.DoubleRealFactory;
import javaMisc.math.autodiff.*;

import java.util.ArrayList;

public class MatrixQ implements DifferentialMatrixFunction {

    private static final DoubleRealFactory RNFactory = DoubleRealFactory.instance();
    private static final DifferentialRealFunctionFactory<DoubleReal> DFFactory = new DifferentialRealFunctionFactory<DoubleReal>(RNFactory);

    // 4x4 dimension, as the below one
    //  ________________
    // |       .        |
    // |  R(q) . R(q)*d |
    // |................|
    // | 0 0 0 .   1    |
    // |_______.________|

    // matrix[row][col]
    private int row;
    private int col;
    private DifferentialFunction<DoubleReal>[][] matrix;

    private ArrayList<Variable<DoubleReal>> var_s = new ArrayList<Variable<DoubleReal>>(0);

    public MatrixQ() {
        this(4, 4);
    }

    public MatrixQ(int row, int col, String... var_s) {
        // default, 4x4 row

        this.row = row;
        this.col = col;
        this.matrix = new DifferentialFunction[row][col];

        Constant<DoubleReal> zero = DFFactory.val(new DoubleReal(0));

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = zero;
            }
        }
        if (var_s != null) {
            for (String var : var_s) {
                if (!var.isEmpty()) this.var_s.add(DFFactory.var(var, new DoubleReal(0)));
            }
        }

    }

    public MatrixQ(MatrixQ mat) {
        this(mat.getRowDim(), mat.getColDim(), mat.getVar_s());
        this.matrix = mat.getMatrix();
    }

    /**
     * The first part of this class contains method that return a copy to be saved locally with the operation done
     */

    @Override
    public MatrixQ mul(Object i_v) {

        DifferentialFunction<DoubleReal>[][] mat2mul = ((MatrixQ) i_v).getMatrix();

        int col_b = ((MatrixQ) i_v).getColDim();
        MatrixQ tmp = new MatrixQ(this.getRowDim(), col_b);


        for (int i = 0; i < this.getRowDim(); i++) { // aRow
            for (int j = 0; j < ((MatrixQ) i_v).getColDim(); j++) { // bColumn
                for (int k = 0; k < this.getColDim(); k++) { // aColumn
                    tmp.matrix[i][j] = tmp.matrix[i][j].plus(this.matrix[i][k].mul(mat2mul[k][j]));
                }
            }
        }
//        tmp.addVar_s(this.getVar_s()); //adding variables from 1st matrix
//        tmp.addVar_s(((MatrixQ) i_v).getVar_s()); //adding variables from 2nd matrix
        return tmp;
    }

    @Override
    public MatrixQ pow(int i_n) {

        assert (this.getRowDim() == this.getColDim());

        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim()).setIdentity();

        for (int i = 0; i < i_n; i++) {
            tmp = tmp.mul(this);
        }
        return tmp;
    }

    @Override
    public MatrixQ negate() {

        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim());

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                tmp.matrix[i][j] = this.matrix[i][j].negate();
            }
        }
        return tmp;
    }

    @Override
    public MatrixQ plus(Object i_v) {
        DifferentialFunction<DoubleReal>[][] mat2add = ((MatrixQ) i_v).getMatrix();

        assert (this.getRowDim() == ((MatrixQ) i_v).getRowDim() &&
                this.getColDim() == ((MatrixQ) i_v).getColDim());

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = this.matrix[i][j].plus(mat2add[i][j]);
            }
        }
        // this.addVar_s(((MatrixQ) i_v).getVar_s());
        return this;
    }

    @Override
    public MatrixQ minus(Object i_v) {

        DifferentialFunction<DoubleReal>[][] mat2sub = ((MatrixQ) i_v).getMatrix();

        assert (this.getRowDim() == ((MatrixQ) i_v).getRowDim() &&
                this.getColDim() == ((MatrixQ) i_v).getColDim());

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = this.matrix[i][j].minus(mat2sub[i][j]);
            }
        }
        //  this.addVar_s(((MatrixQ) i_v).getVar_s());
        return this;
    }

    @Override
    public MatrixQ mul(long i_n) {
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = this.matrix[i][j].mul(i_n);
            }
        }
        return this;
    }

    @Override
    public MatrixQ diff(Variable i_v) {
        assert (this.getColDim() == 1); // trattiamo solo i vettori colonna

        for (int i = 0; i < this.row; i++) {
            this.matrix[i][1] = this.matrix[i][1].diff(i_v);
        }
        return this;
    }

    public MatrixQ jacobian() { // todo: complete (to verify)
        assert (this.getColDim() == 1); // trattiamo solo i vettori colonna

        String[] qi_s = this.getVar_s();

        // lo jacobiano di un vettore costante è un vettore nullo
        if (qi_s == null) return new MatrixQ(this.getRowDim(), 1, "");

        int num_var = qi_s.length;

        MatrixQ tmp = new MatrixQ(this.getRowDim(), num_var, qi_s);

        this.printVar_s();

        Variable<DoubleReal>[] tmp_vars = new Variable[num_var];

        System.out.println(this.row);

        for (int j = 0; j < num_var; j++) { //questo per ogni colonna
            tmp_vars[j] = DFFactory.var(qi_s[j], new DoubleReal(0));

            for (int i = 0; i < this.row; i++) {
                tmp.matrix[i][j] = this.matrix[i][0].diff(tmp_vars[j]);
            }
        }
        return tmp;
    }

    /**
     * In this part, instead, we set the calling object to a new state
     */

    public MatrixQ negateOnSelf() {
        MatrixQ tmp = this.negate();
        this.matrix = tmp.matrix;
        return this;
    }

    public MatrixQ setIdentity() {
        assert this.row == this.col;

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                if (i == j) {
                    this.matrix[i][j] = DFFactory.val(new DoubleReal(1));
                } else {
                    this.matrix[i][j] = DFFactory.val(new DoubleReal(0));
                }
            }
        }
        this.var_s.clear(); //we remove all the variables
        return this;
    }

    public MatrixQ setRotX(String q_i, double value) {

        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim()).setIdentity();

        if (!q_i.isEmpty()) { //we have a variable
            Variable<DoubleReal> q = DFFactory.var(q_i, new DoubleReal(value));
            tmp.var_s.add(q);
            tmp.matrix[1][1] = DFFactory.cos(q);
            tmp.matrix[1][2] = DFFactory.sin(q).negate();
            tmp.matrix[2][1] = DFFactory.sin(q);
            tmp.matrix[2][2] = DFFactory.cos(q);
        } else { //we have a constant
            Constant<DoubleReal> c = DFFactory.val(new DoubleReal(value));
            tmp.matrix[1][1] = DFFactory.cos(c);
            tmp.matrix[1][2] = DFFactory.sin(c).negate();
            tmp.matrix[2][1] = DFFactory.sin(c);
            tmp.matrix[2][2] = DFFactory.cos(c);
        }
        return tmp;
    }

    public MatrixQ setRotZ(String q_i, double value) {

        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim()).setIdentity();

        if (!q_i.isEmpty()) { //we have a variable
            Variable<DoubleReal> q = DFFactory.var(q_i, new DoubleReal(value));
            tmp.var_s.add(q);
            tmp.matrix[0][0] = DFFactory.cos(q);
            tmp.matrix[0][1] = DFFactory.sin(q).negate();
            tmp.matrix[1][0] = DFFactory.sin(q);
            tmp.matrix[1][1] = DFFactory.cos(q);
        } else { //we have a constant
            Constant<DoubleReal> c = DFFactory.val(new DoubleReal(value));
            tmp.matrix[0][0] = DFFactory.cos(c);
            tmp.matrix[0][1] = DFFactory.sin(c).negate();
            tmp.matrix[1][0] = DFFactory.sin(c);
            tmp.matrix[1][1] = DFFactory.cos(c);
        }
        return tmp;
    }

    public MatrixQ setTraslZ(String q_i, double value) {

        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim()).setIdentity();

        if (!q_i.isEmpty()) { //we have a variable
            Variable<DoubleReal> q = DFFactory.var(q_i, new DoubleReal(value));
            tmp.var_s.add(q);
            tmp.matrix[2][3] = q;
        } else { //we have a constant
            Constant<DoubleReal> c = DFFactory.val(new DoubleReal(value));
            tmp.matrix[2][3] = c;
        }
        return tmp;
    }

    public MatrixQ setTraslX(String q_i, double value) {

        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim()).setIdentity();

        if (!q_i.isEmpty()) { //we have a variable
            Variable<DoubleReal> q = DFFactory.var(q_i, new DoubleReal(value));
            tmp.var_s.add(q);
            tmp.matrix[0][3] = q;
        } else { //we have a constant
            Constant<DoubleReal> c = DFFactory.val(new DoubleReal(value));
            tmp.matrix[0][3] = c;
        }
        return tmp;
    }

    /**
     * Getter & Setter part
     */

    public MatrixQ getVPos() { // todo: fare metodi che tornino una sottomatrice
        assert (this.getRowDim() == this.getColDim() && this.getRowDim() == 4); // for Rot&Trasl in 3D
        MatrixQ tmp = new MatrixQ(3, 1, this.getVar_s());

        for (int i = 0; i < 3; i++) {
            tmp.matrix[i][0] = this.matrix[i][3];
        }
        return tmp;
    }

    public MatrixQ getMatRot() {
        assert (this.getRowDim() == this.getColDim() && this.getRowDim() == 4); // for Rot&Trasl in 3D
        MatrixQ tmp = new MatrixQ(3, 3, this.getVar_s());

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tmp.matrix[i][j] = this.matrix[i][j];
            }
        }
        return tmp;
    }

    public DifferentialFunction<DoubleReal>[][] getMatrix() {
        return this.matrix;
    }

    public void setMatrix(DifferentialFunction<DoubleReal>[][] new_mat) {
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = new_mat[i][j];
            }
        }
    }

    public void setMatrix(MatrixQ new_mat) {

        DifferentialFunction<DoubleReal>[][] tmp = new_mat.getMatrix();

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = tmp[i][j];
            }
        }
    }

    public String[] getVar_s() {
        if (this.var_s.size() == 0) return null;
        return (String[]) this.var_s.toArray(new String[var_s.size()]);
    }

    public int getRowDim() {
        return this.row;
    }

    public int getColDim() {
        return this.col;
    }

    private void addVar_s(Variable<DoubleReal>[] vars) {
        if (vars == null) return;
        for (Variable<DoubleReal> s : vars) {
            if (!this.var_s.contains(s)) this.var_s.add(s);
        }
    }


    public void printSymMat() {
        for (DifferentialFunction<DoubleReal>[] row : this.matrix) {
            for (DifferentialFunction<DoubleReal> col : row) {
                System.out.print(col.toString() + " \t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printMatValue() {
        for (DifferentialFunction<DoubleReal>[] row : this.matrix) {
            for (DifferentialFunction<DoubleReal> col : row) {
                System.out.print(col.getValue() + "\t\t");
            }
            System.out.println();
        }
        System.out.println();
    }


    public void printVar_s() {
        System.out.print("[");
        for (Variable<DoubleReal> v : this.var_s) {
            System.out.print(v.toString() + " \t");
        }
        System.out.println("]\n");
    }

    public static void main(String[] args) {

        //todo: fare più test case, almeno uno per ogni funzione

        MatrixQ m1 = new MatrixQ();

        m1.setIdentity();
        m1.printSymMat();

        m1.negate();
        m1.printSymMat();
        m1.mul(12);

        System.out.println("M1:");
        m1.printSymMat();

        MatrixQ m2 = new MatrixQ(4, 4, "q1", "q2").setIdentity().mul(5).plus(m1);
        System.out.println("M2:");
        m2.printSymMat();

        MatrixQ m3 = m1.mul(m2);
        System.out.println("M3:");
        m3.printSymMat();


        MatrixQ m4 = new MatrixQ(m2);
        m4.printVar_s();

        MatrixQ m5 = new MatrixQ().setTraslZ("q2", 0);
        m5.printSymMat();

        MatrixQ m6 = m4.setIdentity().setRotZ("q1", 0).mul(m5);
        m6.printSymMat();

        MatrixQ jac = m6.getVPos().jacobian();
        jac.printSymMat();

    }
}

package robots.DH;

import robots.DH.math.DoubleReal;
import robots.DH.math.DoubleRealFactory;
import robots.DH.math.autodiff.*;
import org.ejml.simple.SimpleMatrix;

import static java.lang.Math.PI;

public class MatrixQ implements DifferentialMatrixFunction {

    /**
     * Attributes
     */

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
    private RobVars var_s;

    /**
     * Constructors
     */

    public MatrixQ() {
        this(4, 4);
    }

    public MatrixQ(int row, int col, String... var_s) {
        // default, 4x4 row

        this.row = row;
        this.col = col;
        this.matrix = new DifferentialFunction[row][col];
        this.var_s = new RobVars();

        Constant<DoubleReal> zero = DFFactory.val(new DoubleReal(0));

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = zero;
            }
        }
        if (var_s != null) {
            for (String var : var_s) {
                if (!var.isEmpty()) this.var_s.addVar(DFFactory.var(var, new DoubleReal(0)));
            }
        }

    }

    public MatrixQ(MatrixQType Mtype, String var, double value) {
        this();
        switch (Mtype) {
            case RotX:
                this.setRotX(var, value);
                break;
            case TslX:
                this.setTslX(var, value);
                break;
            case RotZ:
                this.setRotZ(var, value);
                break;
            case TslZ:
                this.setTslZ(var, value);
                break;
        }
    }

    public MatrixQ(MatrixQ mat) {
        this(mat.getRowDim(), mat.getColDim());
        this.matrix = mat.getMatrix();
        this.var_s = mat.getRobVars();
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
        tmp.mergeVar_s(this.getRobVars()); //adding variables from 1st matrix
        tmp.mergeVar_s(((MatrixQ) i_v).getRobVars()); //adding variables from 2nd matrix
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
        assert (this.getRowDim() == ((MatrixQ) i_v).getRowDim() &&
                this.getColDim() == ((MatrixQ) i_v).getColDim());

        DifferentialFunction<DoubleReal>[][] mat2add = ((MatrixQ) i_v).getMatrix();
        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim());

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                tmp.matrix[i][j] = this.matrix[i][j].plus(mat2add[i][j]);
            }
        }
        tmp.mergeVar_s(((MatrixQ) i_v).getRobVars());
        return tmp;
    }

    @Override
    public MatrixQ minus(Object i_v) {
        assert (this.getRowDim() == ((MatrixQ) i_v).getRowDim() &&
                this.getColDim() == ((MatrixQ) i_v).getColDim());

        DifferentialFunction<DoubleReal>[][] mat2sub = ((MatrixQ) i_v).getMatrix();
        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim());

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                tmp.matrix[i][j] = this.matrix[i][j].minus(mat2sub[i][j]);
            }
        }
        tmp.mergeVar_s(((MatrixQ) i_v).getRobVars());
        return tmp;
    }

    @Override
    public MatrixQ mul(long i_n) {
        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim());

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                tmp.matrix[i][j] = this.matrix[i][j].mul(i_n);
            }
        }
        return tmp;
    }

    @Override
    public MatrixQ diff(Variable i_v) {
        // we can differentiate even a matrix if we use only a variable, since it became a cubic matrix NxNx1
        // -> it remains a square matrix

        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim());

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                tmp.matrix[i][j] = this.matrix[i][j].diff(i_v);
            }
        }
        return tmp;
    }

    public MatrixQ transpose() {
        MatrixQ tmp = new MatrixQ(this.getColDim(), this.getRowDim());
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                tmp.matrix[j][i] = this.matrix[i][j];
            }
        }
        return tmp;
    }


    public MatrixQ jacobian() {
        assert (this.getColDim() == 1); // trattiamo solo i vettori colonna

        RobVars qi_s = this.getRobVars();
        if (qi_s == null)
            return new MatrixQ(this.getRowDim(), 1); // lo jacobiano di un vettore costante è un vettore nullo

        int num_var = qi_s.varSize();

        MatrixQ tmp = new MatrixQ(this.getRowDim(), num_var);

        RobVars tmp_vars = this.getRobVars();
        tmp.mergeVar_s(this.getRobVars());

        for (int j = 0; j < num_var; j++) { //questo per ogni colonna
            for (int i = 0; i < this.row; i++) {
                tmp.matrix[i][j] = this.matrix[i][0].diff(tmp_vars.getVar(j));
            }
        }
        return tmp;
    }


    /**
     * Setter for 3D matrix
     */

    public MatrixQ setRotZ(String q_i, double value) {

        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim()).setIdentity();

        if (!q_i.isEmpty()) { //we have a variable
            Variable<DoubleReal> q = DFFactory.var(q_i, new DoubleReal(value));
            tmp.var_s.addVar(q);
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

    public MatrixQ setTslZ(String q_i, double value) {

        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim()).setIdentity();

        if (!q_i.isEmpty()) { //we have a variable
            Variable<DoubleReal> q = DFFactory.var(q_i, new DoubleReal(value));
            tmp.var_s.addVar(q);
            tmp.matrix[2][3] = q;
        } else { //we have a constant
            Constant<DoubleReal> c = DFFactory.val(new DoubleReal(value));
            tmp.matrix[2][3] = c;
        }
        return tmp;
    }

    public MatrixQ setAvvZ(String q_i, double val, AvvType type) {
        switch (type) {
            case RotVariable:
                MatrixQ tmp_rot = new MatrixQ().setRotZ(q_i, 0);
                MatrixQ tmp_tsl = new MatrixQ().setTslZ("", val);
                tmp_rot.setVPos(tmp_tsl.getVPos());
                return tmp_rot;

            case TslVariable:
                MatrixQ tmp2_rot = new MatrixQ().setRotZ("", val);
                MatrixQ tmp2_tsl = new MatrixQ().setTslZ(q_i, 0);
                tmp2_rot.setVPos(tmp2_tsl.getVPos());
                return tmp2_rot;
            default:
                return null;
        }
    }

    public MatrixQ setRotX(String q_i, double value) {

        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim()).setIdentity();

        if (!q_i.isEmpty()) { //we have a variable
            Variable<DoubleReal> q = DFFactory.var(q_i, new DoubleReal(value));
            tmp.var_s.addVar(q);
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

    public MatrixQ setTslX(String q_i, double value) {

        MatrixQ tmp = new MatrixQ(this.getRowDim(), this.getColDim()).setIdentity();

        if (!q_i.isEmpty()) { //we have a variable
            Variable<DoubleReal> q = DFFactory.var(q_i, new DoubleReal(value));
            tmp.var_s.addVar(q);
            tmp.matrix[0][3] = q;
        } else { //we have a constant
            Constant<DoubleReal> c = DFFactory.val(new DoubleReal(value));
            tmp.matrix[0][3] = c;
        }
        return tmp;
    }

    public MatrixQ setAvvX(double alpha, double a) {
        MatrixQ tmp_rot = new MatrixQ().setRotZ("", alpha);
        MatrixQ tmp_tsl = new MatrixQ().setTslZ("", a);
        tmp_rot.setVPos(tmp_tsl.getVPos());
        return tmp_rot;
    }

    /**
     * In this part, instead, we set the calling object to a new state
     */

    public MatrixQ mulOnSelf(Object i_v) {
        MatrixQ tmp = this.mul(i_v);
        this.matrix = tmp.getMatrix();
        this.row = tmp.getRowDim();
        this.col = tmp.getColDim();
        this.var_s = tmp.getRobVars();
        return this;
    }

    public MatrixQ powOnSelf(int i_n) {
        MatrixQ tmp = this.pow(i_n);
        this.matrix = tmp.getMatrix();
        return this;
    }

    public MatrixQ negateOnSelf() {
        MatrixQ tmp = this.negate();
        this.matrix = tmp.getMatrix();
        return this;
    }

    public MatrixQ plusOnSelf(Object i_v) {
        MatrixQ tmp = this.plus(i_v);
        this.matrix = tmp.getMatrix();
        this.var_s = tmp.getRobVars();
        return this;
    }

    public MatrixQ minusOnSelf(Object i_v) {
        MatrixQ tmp = this.minus(i_v);
        this.matrix = tmp.getMatrix();
        this.var_s = tmp.getRobVars();
        return this;
    }

    public MatrixQ mulOnSelf(long i_n) {
        MatrixQ tmp = this.mul(i_n);
        this.matrix = tmp.getMatrix();
        return this;
    }

    public MatrixQ diffOnSelf(Variable i_v) {
        MatrixQ tmp = this.diff(i_v);
        this.matrix = tmp.getMatrix();
        return this;
    }

    public MatrixQ transposeOnSelf() {
        MatrixQ tmp = this.transpose();
        this.matrix = tmp.getMatrix();
        this.col = tmp.getRowDim();
        this.row = tmp.getColDim();
        return this;
    }

    public MatrixQ setIdentity() {
        assert this.row == this.col;

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                if (i == j) this.matrix[i][j] = DFFactory.val(new DoubleReal(1));
                else this.matrix[i][j] = DFFactory.val(new DoubleReal(0));
            }
        }
        this.var_s.clearVar(); //we remove all the variables
        return this;
    }

    /**
     * Getter & Setter part
     */

    public SimpleMatrix getNumeric() {

        double[][] dataMat = new double[this.getRowDim()][this.getColDim()];
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                dataMat[i][j] = this.matrix[i][j].getValue().doubleValue();
            }
        }
        return new SimpleMatrix(dataMat);
    }

    public MatrixQ getSubMat(int row_s, int row_e, int col_s, int col_e) {
        MatrixQ tmp = new MatrixQ(row_e - row_s + 1, col_e - col_s + 1);
        tmp.mergeVar_s(this.getRobVars());
        for (int i = row_s; i < row_e + 1; i++) {
            for (int j = col_s; j < col_e + 1; j++) {
                tmp.matrix[i][j] = this.matrix[i][j];
            }
        }
        return tmp;
    }

    public MatrixQ getVPos() {
        assert (this.getRowDim() == this.getColDim() && this.getRowDim() == 4); // for Rot&Trasl in 3D
        MatrixQ tmp = new MatrixQ(3, 1);
        tmp.mergeVar_s(this.getRobVars());
        for (int i = 0; i < 3; i++) {
            tmp.matrix[i][0] = this.matrix[i][3];
        }
        return tmp;
    }

    public void setVPos(MatrixQ pos) {
        assert (pos.getRowDim() == 3 && pos.getColDim() == 1); // for Rot&Trasl in 3D
        for (int i = 0; i < 3; i++) {
            this.matrix[i][3] = pos.matrix[i][0];
        }
    }

    public MatrixQ getMatRot() {
        assert (this.getRowDim() == this.getColDim() && this.getRowDim() == 4); // for Rot&Trasl in 3D
        MatrixQ tmp = new MatrixQ(3, 3);
        tmp.mergeVar_s(this.getRobVars());
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tmp.matrix[i][j] = this.matrix[i][j];
            }
        }
        return tmp;
    }

    public void setMatRot(MatrixQ rot) {
        assert (rot.getRowDim() == 3 && rot.getColDim() == 3); // for Rot&Trasl in 3D
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.matrix[i][j] = rot.matrix[i][j];
            }
        }
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

    public String[] getVarsName() {
        return this.var_s.getVarsName();
    }

    public RobVars getRobVars() {
        return this.var_s;
    }

    public int getRowDim() {
        return this.row;
    }

    public int getColDim() {
        return this.col;
    }

    private void mergeVar_s(RobVars new_vars) {
        if (new_vars == null) return;
        for (Variable<DoubleReal> n_v : new_vars.getVar()) {
            boolean occur = false;
            for (Variable<DoubleReal> v : this.var_s.getVar()) {
                if (n_v.equals(v)) occur = true;
            }
            if (!occur) this.var_s.addVar(n_v);
        }
    }

    /**
     * Print section
     */


    public void printMatSym() {
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
                System.out.printf("%.3f\t ", col.getValue().doubleValue());
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printVar_s() {
        System.out.print("[");
        if (this.var_s.varSize() > 0) {
            for (Variable<DoubleReal> v : this.var_s.getVar()) {
                System.out.print(v.toString() + " \t");
            }
        }
        System.out.println("]\n");
    }

    public static void main(String[] args) {

        MatrixQ m1 = new MatrixQ();

        m1.setIdentity();
        m1.printMatSym();

        m1.negate();
        m1.printMatSym();
        m1.mul(12);

        System.out.println("M1:");
        m1.printMatSym();

        MatrixQ m2 = new MatrixQ(4, 4, "q1", "q2").setIdentity().mul(5).plus(m1);
        System.out.println("M2:");
        m2.printMatSym();

        MatrixQ m3 = m1.mul(m2);
        System.out.println("M3:");
        m3.printMatSym();


        MatrixQ m4 = new MatrixQ(m2);
        m4.printVar_s();

        MatrixQ m5 = new MatrixQ().setTslZ("q2", 15);
        m5.printMatSym();

        MatrixQ m6 = m4.setIdentity().setRotZ("q1", PI / 2).mul(m5);
        m6.printMatSym();

        MatrixQ jac = m6.getVPos().jacobian();
        jac.printMatSym();
        jac.printMatValue();

        MatrixQ m7 = m6.pow(2);
        m7.printMatSym();
        m7.printMatValue();

        m7.printVar_s();

        MatrixQ m8 = m6.setIdentity().setAvvZ("q1", 10, AvvType.RotVariable);
        MatrixQ m9 = m6.setIdentity().setAvvZ("q1", 10, AvvType.TslVariable);

        m8.printMatSym();
        m9.printMatSym();
    }
}

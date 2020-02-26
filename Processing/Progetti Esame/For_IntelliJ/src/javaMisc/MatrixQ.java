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

    private ArrayList<String> var_s = new ArrayList<String>(0);

    public MatrixQ() {
        this(4, 4, "");
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
        for (String var : var_s) {
            if (var.isEmpty()) continue;
            this.var_s.add(var);
        }

    }

    public MatrixQ(MatrixQ mat) {
        this(mat.getRowDim(), mat.getColDim(), mat.getVar_s());
        this.matrix = mat.getMatrix();
    }

    @Override
    public MatrixQ mul(Object i_v) { // todo: add var_s

        DifferentialFunction<DoubleReal>[][] mat2mul = ((MatrixQ) i_v).getMatrix();

        int col_b = ((MatrixQ) i_v).getColDim();
        MatrixQ tmp = new MatrixQ(this.getRowDim(), col_b);

        DifferentialFunction<DoubleReal>[][] tmpMat = tmp.getMatrix();

        for (int i = 0; i < this.getRowDim(); i++) { // aRow
            for (int j = 0; j < ((MatrixQ) i_v).getColDim(); j++) { // bColumn
                for (int k = 0; k < this.getColDim(); k++) { // aColumn
                    tmpMat[i][j] = tmpMat[i][j].plus(this.matrix[i][k].mul(mat2mul[k][j]));
                }
            }
        }
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
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = this.matrix[i][j].negate();
            }
        }
        return this;
    }

    @Override
    public MatrixQ plus(Object i_v) { // todo: add var_s
        DifferentialFunction<DoubleReal>[][] mat2add = ((MatrixQ) i_v).getMatrix();

        assert (this.getRowDim() == ((MatrixQ) i_v).getRowDim() &&
                this.getColDim() == ((MatrixQ) i_v).getColDim());

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = this.matrix[i][j].plus(mat2add[i][j]);
            }
        }
        return this;
    }

    @Override
    public MatrixQ minus(Object i_v) { // todo: add var_s

        DifferentialFunction<DoubleReal>[][] mat2sub = ((MatrixQ) i_v).getMatrix();

        assert (this.getRowDim() == ((MatrixQ) i_v).getRowDim() &&
                this.getColDim() == ((MatrixQ) i_v).getColDim());

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = this.matrix[i][j].minus(mat2sub[i][j]);
            }
        }
        return this;
    }

    @Override
    public MatrixQ mul(long i_n) { // todo: add var_s
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

    public MatrixQ jacobian() { // todo: complete
        assert (this.getColDim() == 1); // trattiamo solo i vettori colonna

        String[] qi_s = this.getVar_s();
        int num_var = qi_s.length;

        Variable<DoubleReal>[] tmp_vars = new Variable[num_var];

        for (int j = 0; j < num_var; j++) { //questo per ogni colonna
            tmp_vars[j] = DFFactory.var(qi_s[j], new DoubleReal(0));

            for (int i = 0; i < this.row; i++) {
                this.matrix[i][j] = this.matrix[i][j].diff(tmp_vars[j]);
            }
        }
        return this;
    }

    public MatrixQ setIdentity() { // todo: clean var_s
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
        return this;
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

    public String[] getVar_s() {
        return (String[]) this.var_s.toArray(new String[var_s.size()]);
    }

    public int getRowDim() {
        return this.row;
    }

    public int getColDim() {
        return this.col;
    }


    public void printMatrix() {
        for (DifferentialFunction<DoubleReal>[] row : this.matrix) {
            for (DifferentialFunction<DoubleReal> col : row) {
                System.out.print(col.getValue() + " \t");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    public void printVar_s() {
        System.out.print("[");
        for (String v : this.var_s) {
            System.out.print(v + " \t");
        }
        System.out.println("]");
    }

    public static void main(String[] args) {

        //todo: fare più test case, almeno uno per ogni funzione

        MatrixQ m1 = new MatrixQ();

        m1.setIdentity();
        m1.printMatrix();

        m1.negate();
        m1.printMatrix();
        m1.mul(12);

        System.out.println("M1:");
        m1.printMatrix();

        MatrixQ m2 = new MatrixQ(4, 4, "q1", "q2").setIdentity().mul(5).plus(m1);
        System.out.println("M2:");
        m2.printMatrix();

        MatrixQ m3 = m1.mul(m2);
        System.out.println("M3:");
        m3.printMatrix();


        MatrixQ m4 = new MatrixQ(m2);
        m4.printVar_s();
    }
}

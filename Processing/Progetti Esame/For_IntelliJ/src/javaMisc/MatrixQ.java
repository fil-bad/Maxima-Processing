package javaMisc;

import javaMisc.math.DoubleReal;
import javaMisc.math.DoubleRealFactory;
import javaMisc.math.autodiff.*;
import org.ejml.data.Matrix;

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

    public MatrixQ() {
        this(4, 4);
    }

    public MatrixQ(int row, int col) {
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
    }

    public MatrixQ(DifferentialFunction<DoubleReal>[][] mat) {
        this(mat.length, mat[0].length);
        this.matrix = mat;
    }


    public MatrixQ(MatrixQ mat) {
        this(mat.getRowDim(), mat.getColDim());
        this.matrix = mat.getMatrix();
    }

    @Override
    public MatrixQ mul(Object i_v) {

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
    public MatrixQ plus(Object i_v) {
        DifferentialFunction<DoubleReal>[][] mat2sub = ((MatrixQ) i_v).getMatrix();

        assert (this.getRowDim() == ((MatrixQ) i_v).getRowDim() &&
                this.getColDim() == ((MatrixQ) i_v).getColDim());

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = this.matrix[i][j].plus(mat2sub[i][j]);
            }
        }
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
    public MatrixQ diff(Variable i_v) { //todo: fare la derivata di vettore
        return null;
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
        return this;
    }

    public DifferentialFunction<DoubleReal>[][] getMatrix() {
        return this.matrix;
    }

    public void setMatrix(MatrixQ mat) {

        DifferentialFunction<DoubleReal>[][] new_mat = mat.getMatrix();

        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                this.matrix[i][j] = new_mat[i][j];
            }
        }
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
                System.out.print(col.getValue() + "\t");
            }
            System.out.println("");
        }
        System.out.println("");
    }


    public static void main(String[] args) {
        MatrixQ mat = new MatrixQ();

        mat.setIdentity();
        mat.printMatrix();

        mat.negate();
        mat.printMatrix();

        mat.mul(10);
        mat.printMatrix();

        //

    }
}

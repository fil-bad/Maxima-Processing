package robots.DH;

import org.ejml.simple.SimpleMatrix;
import robots.DH.math.DoubleReal;
import robots.DH.math.DoubleRealFactory;
import robots.DH.math.autodiff.*;

import java.util.Arrays;

import static java.lang.Math.PI;

public class MatrixQ implements DifferentialMatrixFunction {

    /**
     * Attributes
     */

    private static final DoubleRealFactory RNFactory = DoubleRealFactory.instance();
    private static final DifferentialRealFunctionFactory<DoubleReal> DFFactory = new DifferentialRealFunctionFactory<DoubleReal>(RNFactory);

    private static final Constant<DoubleReal> zero = DFFactory.val(new DoubleReal(0));
    private static final Constant<DoubleReal> one = DFFactory.val(new DoubleReal(1));


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

    public MatrixQ(MatrixQType type, String var, double value) {
        this();
        switch (type) {
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
            case AvvZ:
            case AvvX:
                throw new RuntimeException("Wrong constructor");
        }
    }

    public MatrixQ(MatrixQType type, String rotName, double rot, String traslName, double trasl) {
        this();
        switch (type) {
            case RotX:
                this.setRotX(rotName, rot);
                break;
            case TslX:
                this.setTslX(traslName, trasl);
                break;
            case RotZ:
                this.setRotZ(rotName, rot);
                break;
            case TslZ:
                this.setTslZ(traslName, trasl);
                break;
            case AvvX:
                this.setAvvX(rot, trasl);
                break;
            case AvvZ:
                if (rotName.isBlank() && !traslName.isBlank())   // voglio una matrice di traslazione e rotazione fissa
                    this.setAvvZ(traslName, rot, AvvType.TslVariable);
                else if (!rotName.isBlank() && traslName.isBlank()) // voglio una matrice di rotazione e traslazione fissa
                    this.setAvvZ(rotName, trasl, AvvType.RotVariable);
                else
                    throw new RuntimeException("Impossible to determinate the type of wanted matrix");
                break;
        }
    }

    // create another obj with same variable
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
//        DifferentialFunction<DoubleReal> camp = zero;

        for (int i = 0; i < this.getRowDim(); i++) { // aRow
            for (int j = 0; j < ((MatrixQ) i_v).getColDim(); j++) { // bColumn
                for (int k = 0; k < this.getColDim(); k++) { // aColumn
//                    camp = camp.plus(this.matrix[i][k].mul(mat2mul[k][j]));
                    tmp.matrix[i][j] = tmp.matrix[i][j].plus(this.matrix[i][k].mul(mat2mul[k][j]));
                }
            }
        }
        tmp.var_s.mergeVar_s(this.getRobVars()); //adding variables from 1st matrix
        tmp.var_s.mergeVar_s(((MatrixQ) i_v).getRobVars()); //adding variables from 2nd matrix
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
        tmp.var_s.mergeVar_s(((MatrixQ) i_v).getRobVars());
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
        tmp.var_s.mergeVar_s(((MatrixQ) i_v).getRobVars());
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
        tmp.var_s.mergeVar_s(this.getRobVars());

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
            if (value == PI / 2.0) {
                tmp.matrix[0][0] = zero;
                tmp.matrix[0][1] = one.negate();
                tmp.matrix[1][0] = one;
                tmp.matrix[1][1] = zero;
            } else if (value == -PI / 2.0) {
                tmp.matrix[0][0] = zero;
                tmp.matrix[0][1] = one;
                tmp.matrix[1][0] = one.negate();
                tmp.matrix[1][1] = zero;
            } else if (value == PI || value == -PI) {
                tmp.matrix[0][0] = one.negate();
                tmp.matrix[0][1] = zero;
                tmp.matrix[1][0] = one.negate();
                tmp.matrix[1][1] = zero;
            } else if (value != 0) {
                Constant<DoubleReal> c = DFFactory.val(new DoubleReal(value));
                tmp.matrix[0][0] = DFFactory.cos(c);
                tmp.matrix[0][1] = DFFactory.sin(c).negate();
                tmp.matrix[1][0] = DFFactory.sin(c);
                tmp.matrix[1][1] = DFFactory.cos(c);
            }
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
                tmp2_tsl.setMatRot(tmp2_rot.getMatRot());
                return tmp2_tsl;
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
            if (value == PI / 2.0) {
                tmp.matrix[1][1] = zero;
                tmp.matrix[1][2] = one.negate();
                tmp.matrix[2][1] = one;
                tmp.matrix[2][2] = zero;
            } else if (value == -PI / 2.0) {
                tmp.matrix[1][1] = zero;
                tmp.matrix[1][2] = one;
                tmp.matrix[2][1] = one.negate();
                tmp.matrix[2][2] = zero;
            } else if (value == PI || value == -PI) {
                tmp.matrix[1][1] = one.negate();
                tmp.matrix[1][2] = zero;
                tmp.matrix[2][1] = one.negate();
                tmp.matrix[2][2] = zero;
            } else if (value != 0) {
                Constant<DoubleReal> c = DFFactory.val(new DoubleReal(value));
                tmp.matrix[1][1] = DFFactory.cos(c);
                tmp.matrix[1][2] = DFFactory.sin(c).negate();
                tmp.matrix[2][1] = DFFactory.sin(c);
                tmp.matrix[2][2] = DFFactory.cos(c);
            }
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
        MatrixQ tmp_rot = new MatrixQ().setRotX("", alpha);
        MatrixQ tmp_tsl = new MatrixQ().setTslX("", a);
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
        tmp.var_s.mergeVar_s(this.getRobVars());
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
        tmp.var_s.mergeVar_s(this.getRobVars());
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
        tmp.var_s.mergeVar_s(this.getRobVars());
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

    /**
     * Print section
     */


    public void printMatSym() {
        System.out.println("printMatSym:");
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

        System.out.println("###Test moltiplicazione 2 matrixi 2x2###");
        MatrixQ A = new MatrixQ(2, 2, "a1", "b1", "c1", "d1");
        A.getMatrix()[0][0] = A.getRobVars().getVar("a1");
        A.getMatrix()[0][1] = A.getRobVars().getVar("b1");
        A.getMatrix()[1][0] = A.getRobVars().getVar("c1");
        A.getMatrix()[1][1] = A.getRobVars().getVar("d1");
        A.printMatSym();
        MatrixQ B = new MatrixQ(2, 2, "a2", "b2", "c2", "d2");
        B.getMatrix()[0][0] = B.getRobVars().getVar("a2");
        B.getMatrix()[0][1] = B.getRobVars().getVar("b2");
        B.getMatrix()[1][0] = B.getRobVars().getVar("c2");
        B.getMatrix()[1][1] = B.getRobVars().getVar("d2");
        B.printMatSym();
        MatrixQ C = A.mul(B);
        C.printMatSym();

        System.out.println("###Test moltiplicazione 2 matrixi 2x2 con Self###");
        A.mulOnSelf(B);
        A.printMatSym();

        System.out.println("###Test Creazione matrice###");
        MatrixQ m1 = new MatrixQ();
        m1.printMatSym();

        System.out.println("###Test SetIdentity###");
        m1.setIdentity();
        m1.printMatSym();

        System.out.println("###Test Negazione###");
        MatrixQ app;
        app = m1.negate();
        app.printMatSym();

        System.out.println("###Test moltiplicazione scalare###");
        //Moltiplicazione bene ma nel simbolico scrive male (non aggiunge le parentesi necessarie)
        app = m1.mul(12);
        app.printMatSym();
        app.printMatValue();


        System.out.println("###Test creazione di variabili###");
        MatrixQ m2 = new MatrixQ(4, 4, "q1", "q2").setIdentity().mul(5).plus(m1);
        System.out.println("M2:");
        m2.printMatSym();

        System.out.println("###Test Copia matrice###");
        //todo: così non serve a nulla, duplica i campi ma non cambia gli oggetti
        MatrixQ m4 = new MatrixQ(m2);
        m4.printVar_s();

        System.out.println("###Test Traslazione Z###");
        MatrixQ m5 = new MatrixQ().setTslZ("q2", 15);
        m5.printMatSym();

        System.out.println("###Test Traslazione*Rotazione Z###");
        MatrixQ m6 = new MatrixQ().setRotZ("q1", PI / 2).mul(m5);
        m6.printMatSym();

        System.out.println("###Test jacobiano della posizioe di una Rotazione Z###");
        MatrixQ jac = m6.getVPos().jacobian();
        jac.printMatSym();
        jac.printMatValue();

        System.out.println("###Test potenza di matrice###");
        MatrixQ m7 = m6.pow(2);
        m7.printMatSym();
        m7.printMatValue();
        m7.printVar_s();


        System.out.println("### TEST MulOnSelf: ###");
        MatrixQ m10 = new MatrixQ().setTslX("q1", 10);
        MatrixQ m11 = new MatrixQ().setTslZ("q2", 20);
        m10.mulOnSelf(m11);
        m10.printMatSym();
        m10.getRobVars().printVar();
        System.out.println(Arrays.toString(m10.getRobVars().getVarsName()));

        System.out.println("### TEST Jacobiano nella m10 con q1 e q2: ###");
        m10.getVPos().printMatSym();
        m10.getVPos().jacobian().printMatSym();

        System.out.println("### TEST Jacobiano del vettore q1,q2,q3: ###");
        MatrixQ vect = new MatrixQ(3, 1, "q1", "q2", "q3");
        vect.matrix[0][0] = vect.getRobVars().getVar("q1");
        vect.matrix[1][0] = vect.getRobVars().getVar("q2");
        vect.matrix[2][0] = vect.getRobVars().getVar("q3");

        vect.printMatSym();
        vect.printMatValue();
        vect.jacobian().printMatSym();

        System.out.println("### TEST Jacobiano di 2 trasl si Z: ###");
        MatrixQ trl1 = new MatrixQ(4, 4).setTslZ("q1", 5);
        MatrixQ trl2 = new MatrixQ(4, 4).setTslX("q2", 10);
        trl1.mulOnSelf(trl2).printMatValue();
        trl1.printMatSym();
        trl1.getVPos().printMatSym();
        trl1.getVPos().jacobian().printMatSym();


    }
}

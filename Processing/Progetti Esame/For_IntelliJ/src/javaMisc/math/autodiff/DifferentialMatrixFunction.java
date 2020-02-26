package javaMisc.math.autodiff;

import javaMisc.math.Field;
import javaMisc.math.Ring;

public interface DifferentialMatrixFunction<X extends Field<X>> extends
        Ring<DifferentialMatrixFunction<X>>, Differential<X, DifferentialMatrixFunction<X>> {

}

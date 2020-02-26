package math.autodiff;

import math.Field;
import math.Ring;

public interface DifferentialMatrixFunction<X extends Field<X>> extends
        Ring<DifferentialMatrixFunction<X>>, Differential<X, DifferentialMatrixFunction<X>> {

}

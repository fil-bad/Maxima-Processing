package robots.DH.math.autodiff;

import robots.DH.math.Field;
import robots.DH.math.Ring;

public interface DifferentialMatrixFunction<X extends Field<X>> extends
        Ring<DifferentialMatrixFunction<X>>, Differential<X, DifferentialMatrixFunction<X>> {

}

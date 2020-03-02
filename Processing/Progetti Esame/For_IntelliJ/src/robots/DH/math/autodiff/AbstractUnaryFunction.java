package robots.DH.math.autodiff;

import robots.DH.math.Field;

/**
 * An abstract function requires one argument in X.
 *
 * @param <X> A set forms a field.
 * @author uniker9
 */
public abstract class AbstractUnaryFunction<X extends Field<X>> extends
        DifferentialFunction<X> {

    private DifferentialFunction<X> m_x;

    /**
     * Constructs a new AbstractUnaryFunction.
     *
     * @param i_v the argument.
     */
    public AbstractUnaryFunction(DifferentialFunction<X> i_v) {

        if (i_v != null) {
            m_x = i_v;
        } else {
            throw new IllegalArgumentException("Input not null variable.");
        }
    }

    /**
     * Returns the argument of this function.
     *
     * @return the argument.
     */
    public DifferentialFunction<X> arg() {
        return m_x;
    }
}

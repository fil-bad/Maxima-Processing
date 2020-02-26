package javaMisc.math.autodiff;

import javaMisc.math.Field;


public interface VectorDifferential<X extends Field<X>, D> {

    public D diff(VariableVector<X> i_v);
}

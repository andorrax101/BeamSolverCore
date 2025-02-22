package solver.FunctionalBaseSolver;

import java.util.function.UnaryOperator;

public class Utils_Functional {
    protected static UnaryOperator<Double> singularityFunction(double constant, int exponent){
        return x-> {
            if(exponent == -1 || exponent == 0){
                return x < constant ? 0.0 : 1.0;
            } else if (exponent < -1) {
                throw new IllegalArgumentException("Cannot solve for negative exponents");
            } else {
                if(x <= constant){
                    return 0.0;
                } else {
                    return Math.pow(x - constant, exponent);
                }
            }
        };
    }
}

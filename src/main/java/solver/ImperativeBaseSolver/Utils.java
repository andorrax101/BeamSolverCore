package solver.ImperativeBaseSolver;

public class Utils {
    private Utils(){}

    /**
     * Returns the result of &lt x-constant &gt ^exponent.
     * <p>Refer link for more details: <a href="https://en.wikipedia.org/wiki/Singularity_function">LINK</a></p>
     */
    protected static double getSingularityFunctionResult(double constant, int exponent, double x){
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
    }
}

package solver.Objects;

/**
 * A load pair completely defines a distributed load acting at two different points.
 * <p>Load magnitudes at start and end need not be equal. E.g. for triangular loadAssembly,
 * the load at start may be defined with a zero magnitude, while the load at end may be the required
 * magnitude for the input triangular load</p>
 */
public record LoadPair(LoadInstance start, LoadInstance end) {
    public static LoadPair of(LoadInstance start, LoadInstance end){
        return new LoadPair(start, end);
    }
}

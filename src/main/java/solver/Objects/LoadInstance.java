package solver.Objects;

/**
 * Container for applied load vectors.
 * <p>Loading is assumed to apply along +/- Z direction. Magnitudes with positive signs are aligned along +Z
 * and those with negative sign along -Z</p>
 * @param magnitude
 * @param distanceFromBeamStart
 */
public record LoadInstance(double magnitude, double distanceFromBeamStart) {

}

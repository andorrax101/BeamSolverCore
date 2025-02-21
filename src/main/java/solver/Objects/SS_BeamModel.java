package solver.Objects;

/**
 * Defines a straight beam placed with its start point at the world origin.
 * @param length
 * @param modulusOfElasticity
 * @param momentOfInertia
 * @param loadAssembly
 */
public record SS_BeamModel(double length, double modulusOfElasticity, double momentOfInertia, LoadAssembly loadAssembly) {
}

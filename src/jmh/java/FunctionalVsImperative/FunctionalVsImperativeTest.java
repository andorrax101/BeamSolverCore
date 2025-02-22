package FunctionalVsImperative;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import solver.FunctionalBaseSolver.BeamSolver_SS_Functional;
import solver.ImperativeBaseSolver.BeamSolver_SS;
import solver.Objects.AnalysisModel;
import solver.Objects.LoadAssembly;
import solver.Objects.LoadInstance;

public class FunctionalVsImperativeTest {
    @State(Scope.Benchmark)
    public static class StateData{
        static AnalysisModel model;
        static LoadAssembly assembly = new LoadAssembly();
        static {
            double beamLength = 10.0; // meters
            double modulus = 32E9; // Pascals
            double momentOfInertia = 0.3*Math.pow(0.6,3)/12; // 0.3m x 0.6m beam
            LoadInstance pointLoad = new LoadInstance(-10, 5.0);
            assembly.addPointForce(pointLoad);
            model = new AnalysisModel(beamLength, modulus, momentOfInertia, assembly);
        }
        public static final BeamSolver_SS imperativeSolver = new BeamSolver_SS(model);
        public static final BeamSolver_SS_Functional functionalSolver = new BeamSolver_SS_Functional(model);
    }

    @Benchmark
    @Fork(value = 1)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    public void testingImperativeSolving(Blackhole bh) {
        double startShear = StateData.imperativeSolver.getShear(0.0);
        double endShear = StateData.imperativeSolver.getShear(10.0);
        double midMoment = StateData.imperativeSolver.getMoment(5.0);
        double midDispl = StateData.imperativeSolver.getDeflection(5.0);
        bh.consume(startShear);
        bh.consume(endShear);
        bh.consume(midMoment);
        bh.consume(midDispl);
    }

    @Benchmark
    @Fork(value = 1)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    public void testingFunctionalSolving(Blackhole bh) {
        double startShear = StateData.functionalSolver.getShearFunction().apply(0.0);
        double endShear = StateData.functionalSolver.getShearFunction().apply(10.0);
        double midMoment = StateData.functionalSolver.getMomentFunction().apply(5.0);
        double midDispl = StateData.functionalSolver.getDeflectionFunction().apply(5.0);
        bh.consume(startShear);
        bh.consume(endShear);
        bh.consume(midMoment);
        bh.consume(midDispl);
    }
}

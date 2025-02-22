package solver;

import org.junit.jupiter.api.Test;
import solver.ImperativeBaseSolver.BeamSolver_SS;
import solver.Objects.LoadAssembly;
import solver.Objects.LoadInstance;
import solver.Objects.AnalysisModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImperativeSolverTests {
    @Test
    void testPointLoads(){
        double beamLength = 10.0; // meters
        double modulus = 32E9; // Pascals
        double momentOfInertia = 0.3*Math.pow(0.6,3)/12; // 0.3m x 0.6m beam
        LoadInstance pointLoad = new LoadInstance(-10, 5.0);

        double midPointDisplacement = 10*Math.pow(beamLength,3)/(48*modulus*momentOfInertia);

        LoadAssembly loads = new LoadAssembly();
        loads.addPointForce(pointLoad);
        AnalysisModel model = new AnalysisModel(beamLength, modulus, momentOfInertia, loads);

        BeamSolver_SS solver = new BeamSolver_SS(model);

        double startShear = solver.getShear(0.0);
        double endShear = solver.getShear(10.0);
        double midMoment = solver.getMoment(5.0);
        double midDispl = solver.getDeflection(5.0);

        assertEquals(-5.0, startShear, 1E-10);
        assertEquals(5.0, endShear, 1E-10);
        assertEquals(-25.0, midMoment, 1E-10);
        assertEquals(midPointDisplacement, midDispl, 1E-10);
    }

    @Test
    void testUniformlyDistributedLoads(){
        double beamLength = 10.0; // meters
        double modulus = 32E9; // Pascals
        double momentOfInertia = 0.3*Math.pow(0.6,3)/12; // 0.3m x 0.6m beam
        double midPointDisplacement = 5*10*Math.pow(beamLength,4)/(384*modulus*momentOfInertia);

        LoadInstance start = new LoadInstance(-10, 0.0);
        LoadInstance end = new LoadInstance(-10, 10.0);

        LoadAssembly loads = new LoadAssembly();
        loads.addDistributedForce(start, end);
        AnalysisModel model = new AnalysisModel(beamLength, modulus, momentOfInertia, loads);
        var solver = new BeamSolver_SS(model);

        double startShear = solver.getShear(0.0);
        double endShear = solver.getShear(10.0);
        double midMoment = solver.getMoment(5.0);
        double midDispl = solver.getDeflection(5.0);

        assertEquals(-50.0, startShear, 1E-10);
        assertEquals(50.0, endShear, 1E-10);
        assertEquals(-10*100.0/8.0, midMoment, 1E-10);
        assertEquals(midPointDisplacement, midDispl, 1E-10);
    }

    @Test
    void testTriangularLoads(){
        double beamLength = 10.0; // meters
        double modulus = 32E9; // Pascals
        double momentOfInertia = 0.3*Math.pow(0.6,3)/12; // 0.3m x 0.6m beam
        LoadInstance start = new LoadInstance(0.0, 0.0);
        LoadInstance mid = new LoadInstance(-10, 5.0);
        LoadInstance end = new LoadInstance(0.0, 10.0);

        LoadAssembly loads = new LoadAssembly();
        loads.addDistributedForce(start, mid);
        loads.addDistributedForce(mid, end);
        AnalysisModel model = new AnalysisModel(beamLength, modulus, momentOfInertia, loads);
        var solver = new BeamSolver_SS(model);

        double startShear = solver.getShear(0.0);
        double endShear = solver.getShear(10.0);
        double midMoment = solver.getMoment(5.0);

        assertEquals(-25.0, startShear, 1E-10);
        assertEquals(25.0, endShear, 1E-10);
        assertEquals(-10*100.0/12.0, midMoment, 1E-10);
    }
}

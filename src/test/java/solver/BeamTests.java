package solver;

import org.junit.jupiter.api.Test;
import solver.ImperativeBaseSolver.BeamSolver_SS_Imperative;
import solver.Objects.LoadAssembly;
import solver.Objects.LoadInstance;
import solver.Objects.SS_BeamModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeamTests {
    @Test
    void testPointLoads(){
        double beamLength = 10.0; // meters
        double modulus = 32E9; // Pascals
        double momentOfInertia = 0.3*Math.pow(0.6,3)/12; // 0.3m x 0.6m beam
        LoadInstance pointLoad = new LoadInstance(-10, 5.0);

        LoadAssembly loads = new LoadAssembly();
        loads.addPointLoad(pointLoad);
        SS_BeamModel model = new SS_BeamModel(beamLength, modulus, momentOfInertia, loads);

        double startShear = BeamSolver_SS_Imperative.getShear(model, 0.0);
        double endShear = BeamSolver_SS_Imperative.getShear(model, 10.0);
        double midMoment = BeamSolver_SS_Imperative.getMoment(model, 5.0);

        assertEquals(-5.0, startShear, 1E-10);
        assertEquals(5.0, endShear, 1E-10);
        assertEquals(-25.0, midMoment, 1E-10);
    }

    @Test
    void testUniformlyDistributedLoads(){
        double beamLength = 10.0; // meters
        double modulus = 32E9; // Pascals
        double momentOfInertia = 0.3*Math.pow(0.6,3)/12; // 0.3m x 0.6m beam
        LoadInstance start = new LoadInstance(-10, 0.0);
        LoadInstance end = new LoadInstance(-10, 10.0);

        LoadAssembly loads = new LoadAssembly();
        loads.addDistributedLoad(start, end);
        SS_BeamModel model = new SS_BeamModel(beamLength, modulus, momentOfInertia, loads);

        double startShear = BeamSolver_SS_Imperative.getShear(model, 0.0);
        double endShear = BeamSolver_SS_Imperative.getShear(model, 10.0);
        double midMoment = BeamSolver_SS_Imperative.getMoment(model, 5.0);

        assertEquals(-50.0, startShear, 1E-10);
        assertEquals(50.0, endShear, 1E-10);
        assertEquals(-10*100.0/8.0, midMoment, 1E-10);
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
        loads.addDistributedLoad(start, mid);
        loads.addDistributedLoad(mid, end);
        SS_BeamModel model = new SS_BeamModel(beamLength, modulus, momentOfInertia, loads);

        double startShear = BeamSolver_SS_Imperative.getShear(model, 0.0);
        double endShear = BeamSolver_SS_Imperative.getShear(model, 10.0);
        double midMoment = BeamSolver_SS_Imperative.getMoment(model, 5.0);

        assertEquals(-25.0, startShear, 1E-10);
        assertEquals(25.0, endShear, 1E-10);
        assertEquals(-10*100.0/12.0, midMoment, 1E-10);
    }
}

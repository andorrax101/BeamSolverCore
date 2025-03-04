package solver.ImperativeBaseSolver;

import solver.Objects.AnalysisModel;

/**
 * Solver class housing logic for solving simply supported beams.
 * <p>Refer source link for details : <a href="https://icjong.hosted.uark.edu/docu/09.ijee.paper.pdf">LINK</a></p>
 */
public class BeamSolver_SS {
  private static final double TOLERANCE = 1E-10;
  private final AnalysisModel model;
  public BeamSolver_SS(AnalysisModel inputModel){
    model = inputModel;
  }
  private int getDenominator(int exponent){
      return switch (exponent) {
          case -2, -1, 0, 1 -> 1;
          case 2 -> 2;
          case 3 -> 6;
          case 4 -> 24;
          case 5 -> 120;
          default -> throw new IllegalArgumentException("Cannot solve for exponent " + exponent);
      };
  }

  private double pointLoadAndMomentsProcessor(double loadVal,
                                                     double EI, double distanceFromOrigin, int exponent, double x){
    return (-loadVal/(getDenominator(exponent)* EI))
              * Utils.getSingularityFunctionResult(distanceFromOrigin,exponent,x);
  }

  private double distributedLoadsProcessor(
      double loadValAtStart,double loadValAtEnd,
      double EI,
      double distanceFromOriginAtStart, double distanceFromOriginAtEnd,
      int exponent, double x){
    return (1/(getDenominator(exponent)*EI))
        *(-Utils.getSingularityFunctionResult(distanceFromOriginAtStart,exponent,x)*loadValAtStart
          +
        Utils.getSingularityFunctionResult(distanceFromOriginAtEnd,exponent, x)*loadValAtEnd)
        +
        (loadValAtEnd - loadValAtStart)
            /(getDenominator(exponent +1)*EI
            * (distanceFromOriginAtEnd - distanceFromOriginAtStart ))
        *
          (-Utils.getSingularityFunctionResult(distanceFromOriginAtStart,exponent+1, x)
          +
              Utils.getSingularityFunctionResult(distanceFromOriginAtEnd,exponent+1, x));
  }

  private double distributedMomentsProcessor(
      double loadVal,
      double EI,
      double distanceFromOriginAtStart, double distanceFromOriginAtEnd,
      int exponent, double x){
    return (loadVal/(getDenominator(exponent)*EI))
        *(Utils.getSingularityFunctionResult(distanceFromOriginAtStart,exponent,x)
        - Utils.getSingularityFunctionResult(distanceFromOriginAtEnd,exponent,x));
  }

  private double startShear(){
    double length = model.length();
    return -1* baseModelProcessor(0, length)*model.modulusOfElasticity()*model.momentOfInertia()/length;
  }

  private double baseModelProcessor(int minExponent, double x){
    var pointLoad = model.loadAssembly().getPointLoads();
    var pointMoment = model.loadAssembly().getPointMoments();
    var distributedLoad = model.loadAssembly().getDistributedLoads();
    var distributedMoment = model.loadAssembly().getDistributedMoments();

    double EI = model.momentOfInertia() * model.modulusOfElasticity();

    double output = 0.0;
    for(var pl : pointLoad){
      output += pointLoadAndMomentsProcessor(pl.magnitude(), EI, pl.distanceFromBeamStart(), minExponent+1, x);
    }

    for(var pm : pointMoment){
      output += pointLoadAndMomentsProcessor(pm.magnitude(), EI, pm.distanceFromBeamStart(), minExponent, x);
    }

    for(var dl : distributedLoad){
      var instances = dl.getLoadInstances();
      for(int i = 0;i< instances.length-1; i++){
        // if distance values at i and i+1 are equal, continue to next value
        double distDiff = Math.abs(instances[i].distanceFromBeamStart() - instances[i+1].distanceFromBeamStart());
        if(distDiff <= TOLERANCE){
          continue;
        }

        output += distributedLoadsProcessor(instances[i].magnitude(), instances[i+1].magnitude(),
                EI, instances[i].distanceFromBeamStart(), instances[i+1].distanceFromBeamStart(),
                minExponent+2, x);
      }
    }

    for(var dm : distributedMoment) {
      var instances = dm.getLoadInstances();
      for (int i = 0; i < instances.length - 1; i++) {
        // if distance values at i and i+1 are equal, continue to next value
        double distDiff = Math.abs(instances[i].distanceFromBeamStart() - instances[i+1].distanceFromBeamStart());
        if(distDiff <= TOLERANCE){
          continue;
        }

        output += distributedMomentsProcessor(instances[i].magnitude(), EI,
                instances[i].distanceFromBeamStart(), instances[i+1].distanceFromBeamStart(), minExponent + 1, x);
      }
    }
    return output;
  }

  private double startSlope(){
    double length = model.length();
    return
        (- startShear()* Math.pow(length,3)/(6*model.momentOfInertia()*model.modulusOfElasticity())
         -
        baseModelProcessor(2, length))/length;
  }

  public double getDeflection(double x){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();
    return
            startSlope() * x
            +
            startShear()* Math.pow(x,3)/(getDenominator(3)*EI)
            +
            baseModelProcessor(2, x);
  }

  public double getSlope(double x){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();
    return
            startSlope()
            +
            startShear()* Math.pow(x,2)/(2*EI)
            +
            baseModelProcessor(1, x);
  }

  public double getMoment(double x){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();
    return
            startShear() *x
            +
            EI * baseModelProcessor(0, x);
  }

  public double getShear(double x){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();
    return
            startShear()
            +
            EI * baseModelProcessor(-1, x);
  }
}
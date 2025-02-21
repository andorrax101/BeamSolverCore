package solver.ImperativeBaseSolver;

import solver.Objects.SS_BeamModel;

public class BeamSolver_SS_Imperative {
  private BeamSolver_SS_Imperative(){}
  private static int getDenominator(int exponent){
      return switch (exponent) {
          case -2, -1, 0, 1 -> 1;
          case 2 -> 2;
          case 3 -> 6;
          case 4 -> 24;
          case 5 -> 120;
          default -> throw new IllegalArgumentException("Cannot solve for exponent " + exponent);
      };
  }

  private static double pointLoadAndMomentsProcessor(double loadVal,
                                                     double EI, double distanceFromOrigin, int exponent, double x){
    return (-loadVal/(getDenominator(exponent)* EI))
              * Utils_Imperative.getSingularityFunctionResult(distanceFromOrigin,exponent,x);
  }

  private static double distributedLoadsProcessor(
      double loadValAtStart,double loadValAtEnd,
      double EI,
      double distanceFromOriginAtStart, double distanceFromOriginAtEnd,
      int exponent, double x){
    return (1/(getDenominator(exponent)*EI))
        *(-Utils_Imperative.getSingularityFunctionResult(distanceFromOriginAtStart,exponent,x)*loadValAtStart
          +
        Utils_Imperative.getSingularityFunctionResult(distanceFromOriginAtEnd,exponent, x)*loadValAtEnd)
        +
        (loadValAtEnd - loadValAtStart)
            /(getDenominator(exponent +1)*EI
            * (distanceFromOriginAtEnd - distanceFromOriginAtStart ))
        *
          (-Utils_Imperative.getSingularityFunctionResult(distanceFromOriginAtStart,exponent+1, x)
          +
              Utils_Imperative.getSingularityFunctionResult(distanceFromOriginAtEnd,exponent+1, x));
  }

  private static double distributedMomentsProcessor(
      double loadVal,
      double EI,
      double distanceFromOriginAtStart, double distanceFromOriginAtEnd,
      int exponent, double x){
    return (loadVal/(getDenominator(exponent)*EI))
        *(Utils_Imperative.getSingularityFunctionResult(distanceFromOriginAtStart,exponent,x)
        - Utils_Imperative.getSingularityFunctionResult(distanceFromOriginAtEnd,exponent,x));
  }

  private static double startShear(SS_BeamModel model){
    double length = model.length();
    return -1* baseModelProcessor(model,0, length)*model.modulusOfElasticity()*model.momentOfInertia()/length;
  }

  private static double baseModelProcessor(SS_BeamModel model, int minExponent, double x){
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
      output += pointLoadAndMomentsProcessor(pm.magnitude(), EI, pm.distanceFromBeamStart(), minExponent+1, x);
    }

    for(var dl : distributedLoad){
      output += distributedLoadsProcessor(dl.start().magnitude(), dl.end().magnitude(),
              EI, dl.start().distanceFromBeamStart(), dl.end().distanceFromBeamStart(),
              minExponent+2, x);
    }

    for(var dm : distributedMoment){
      output += distributedMomentsProcessor(dm.start().magnitude(), EI,
              dm.start().distanceFromBeamStart(), dm.end().distanceFromBeamStart(), minExponent+1, x);
    }
    return output;
  }

  private static double startSlope(SS_BeamModel model){
    double length = model.length();
    return
        (- startShear(model)* Math.pow(length,3)/(6*model.momentOfInertia()*model.modulusOfElasticity())
         -
        baseModelProcessor(model,2, length))/length;
  }

  public static double getDeflection(SS_BeamModel model, double x){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();
    return
            startSlope(model) * x
            +
            startShear(model)* Math.pow(x,3)/(getDenominator(3)*EI)
            +
            baseModelProcessor(model,2, x);
  }

  public static double getSlope(SS_BeamModel model, double x){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();
    return
            startSlope(model)
            +
            startShear(model)* Math.pow(x,2)/(2*EI)
            +
            baseModelProcessor(model,1, x);
  }

  public static double getMoment(SS_BeamModel model, double x){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();
    return
            startShear(model) *x
            +
            EI * baseModelProcessor(model,0, x);
  }

  public static double getShear(SS_BeamModel model, double x){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();
    return
            startShear(model)
            +
            EI * baseModelProcessor(model,-1, x);
  }
}
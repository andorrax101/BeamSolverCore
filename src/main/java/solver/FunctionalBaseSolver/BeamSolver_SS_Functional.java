package solver.FunctionalBaseSolver;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import solver.Objects.AnalysisModel;

/**
 * @deprecated
 * Solver class housing logic for solving simply supported beams. This class adopts a functional approach
 * <p>Refer source link for details : <a href="https://icjong.hosted.uark.edu/docu/09.ijee.paper.pdf">LINK</a></p>
 * <p>Note: marked as deprecated since it is much slower than the imperative approach</p>
 */
@Deprecated(since="0.01", forRemoval = true)
public class BeamSolver_SS_Functional {
  private final AnalysisModel model;

  public BeamSolver_SS_Functional(AnalysisModel inputModel){
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

  private UnaryOperator<Double> pointLoadAndMomentFunc(double loadVal,
                                                              double EI, double distanceFromOrigin, int exponent){
    return x-> (-loadVal/(getDenominator(exponent)* EI))
              * Utils_Functional.singularityFunction(distanceFromOrigin,exponent).apply(x);
  }

  private UnaryOperator<Double> distributedLoadFunc(
      double loadValAtStart,double loadValAtEnd,
      double EI,
      double distanceFromOriginAtStart, double distanceFromOriginAtEnd,
      int exponent){
    return x-> (1/(getDenominator(exponent)*EI))
        *(-Utils_Functional.singularityFunction(distanceFromOriginAtStart,exponent).apply(x)*loadValAtStart
          +
        Utils_Functional.singularityFunction(distanceFromOriginAtEnd,exponent).apply(x)*loadValAtEnd)
        +
        (loadValAtEnd - loadValAtStart)
            /(getDenominator(exponent +1)*EI
            * (distanceFromOriginAtEnd - distanceFromOriginAtStart ))
        *
          (-Utils_Functional.singularityFunction(distanceFromOriginAtStart,exponent+1).apply(x)
          +
              Utils_Functional.singularityFunction(distanceFromOriginAtEnd,exponent+1).apply(x));
  }

  private UnaryOperator<Double> distributedMomentFunc(
      double loadVal,
      double EI,
      double distanceFromOriginAtStart, double distanceFromOriginAtEnd,
      int exponent){
    return x-> (loadVal/(getDenominator(exponent)*EI))
        *(Utils_Functional.singularityFunction(distanceFromOriginAtStart,exponent).apply(x)
        - Utils_Functional.singularityFunction(distanceFromOriginAtEnd,exponent).apply(x));
  }

  private UnaryOperator<Double> startShear(){

    return x-> -1* baseFunc(0).apply(x)*model.modulusOfElasticity()*model.momentOfInertia()/x;
  }

  private UnaryOperator<Double> baseFunc(int minExponent){
    var pointLoad = model.loadAssembly().getPointLoads();
    var pointMoment = model.loadAssembly().getPointMoments();
    var distributedLoad = model.loadAssembly().getDistributedLoads();
    var distributedMoment = model.loadAssembly().getDistributedMoments();

    double EI = model.momentOfInertia() * model.modulusOfElasticity();

    var combo = Stream.of(
      pointLoad.stream().filter(Objects::nonNull)
          .map(data -> pointLoadAndMomentFunc(data.magnitude(), EI,
                  data.distanceFromBeamStart(), minExponent+1)),

      pointMoment.stream().filter(Objects::nonNull)
          .map(data -> pointLoadAndMomentFunc(data.magnitude(), EI,
                  data.distanceFromBeamStart(), minExponent)),

      distributedLoad.stream().filter(Objects::nonNull)
          .map(data ->
          distributedLoadFunc(data.start().magnitude(), data.end().magnitude(),
                  EI, data.start().distanceFromBeamStart(), data.end().distanceFromBeamStart(),
              minExponent+2)),

      distributedMoment.stream().filter(Objects::nonNull)
          .map(data ->
          distributedMomentFunc(data.start().magnitude(), EI,
              data.start().distanceFromBeamStart(), data.end().distanceFromBeamStart(), minExponent+1))

    ).reduce(Stream::concat)
    .orElseGet(Stream::empty);

    List<Function<Double, Double>> functionList = new ArrayList<>(combo.toList());

    if(functionList.isEmpty()){
      throw new IllegalArgumentException("No functions could be composed. Please check load inputs");
    }

    return x-> functionList.stream().map(func -> func.apply(x)).reduce(0.0, Double::sum);
  }

  private UnaryOperator<Double> startSlope(){
    return x->
        (- startShear().apply(x)* Math.pow(x,3)/(6*model.momentOfInertia()*model.modulusOfElasticity())
         -
        baseFunc(2).apply(x))/x;
  }

  public UnaryOperator<Double> getDeflectionFunction(){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();

    return x->
        (   startSlope().apply(model.length()) * x
            +
            startShear().apply(model.length())* Math.pow(x,3)/(getDenominator(3)*EI)
            +
            baseFunc(2).apply(x));
  }

  public UnaryOperator<Double> getSlopeFunction(){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();

    return x->
        (   startSlope().apply(model.length())
            +
            startShear().apply(model.length())* Math.pow(x,2)/(2*EI)
            +
            baseFunc(1).apply(x));
  }

  public UnaryOperator<Double> getMomentFunction(){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();
    return x->
        (   startShear().apply(model.length()) *x
            +
            EI * baseFunc(0).apply(x));
  }

  public UnaryOperator<Double> getShearFunction(){
    double EI = model.momentOfInertia() * model.modulusOfElasticity();

    return x->
        (   startShear().apply(model.length())
            +
            EI * baseFunc(-1).apply(x));
  }
}
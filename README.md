# BEAM SOLVER CORE
A simple 2D beam solver for simply supported beams capable of solving any possible vertical load arrangement,
using the model formula approach noted in I.C.Jong's paper (LINK: https://icjong.hosted.uark.edu/docu/09.ijee.paper.pdf)

# Feature Set
* Linear static analysis of 2D simply supported beams
* Support for load cases and combinations (WIP)
* Support for any arbitrary arrangement of vertical loads, including
  * Arbitrarily placed concentrated forces and moments
  * Uniformly distributed loads and load segments
  * Triangular loads
  * Trapezoidal loads
  * etc.

# Limitations
* No support (yet) for forces and moments that aren't perpendicular to the beam span
* No support for non-simply supported beams (although this may change in future)
* No support for nonlinear analyses
* Non-uniformly distributed moments are currently not supported
* The theoretical basis for this solver uses the bernoulli beam theory, and therefore ignores the effects of shear deformations

# Usage
See user notes (TBD)

# Examples
## Concentrated Loads
Concentrated forces and moments may be defined using the `LoadInstance` object:
```java
// instantiates a concentrated load of magnitude 5 units acting 3 units away from the beam start
var pointForce = new LoadInstance(5.0, 3.0);
```
Note that the `LoadInstance` object itself makes no distinction between forces and moments. 
This distinction is enforced when creating the `LoadAssembly` object:
```java
var pointForce = new LoadInstance(5.0, 3.0);
var pointMoment = new LoadInstance(2.0, 3.0);

// a load assembly is a collection of different loads (usually all loads under the same load case)
var loadAssembly = new LoadAssembly();
loadAssembly.addPointForce(pointForce); // LoadInstance object saved as a concentrated load
loadAssembly.addPointMoment(pointMoment); // LoadInstance object saved as a concentrated moemnt
```
## Distributed Loads
Distributed forces and moments may be defined using the `LoadPair` object. Each `LoadPair` object
takes in a start value and an end value of a loadInstance:
```java
// define a trapezoidal distributed force where
// - force value at 3 units away from beam origin is 5.0
// - force value at 6 units away from beam origin is 2.0
var start = new LoadInstance(5.0, 3.0);
var end = new LoadInstance(2.0, 6.0);
var distributedForce = new LoadPair(start, end); // object defining distributed force
```
As with concentrated forces, the `LoadPair` object makes no distinction between forces and moments.
This distinction is enforced when creating the `LoadAssembly` object:
```java
var startForce = new LoadInstance(5.0, 3.0);
var endForce = new LoadInstance(2.0, 6.0);
var distributedForce = new LoadPair(startForce, endForce);

var startMoment = new LoadInstance(1.0, 3.0);
var endMoment = new LoadInstance(1.5, 6.0);
var distributedMoment = new LoadPair(startMoment, endMoment);

var loadAssembly = new LoadAssembly();
loadAssembly.addDistributedForce(distributedForce); // LoadPair object saved as a distributed force
loadAssembly.addDistributedMoment(distributedMoment); // LoadPair object saved as a distributed moemnt
```
## Model Assembly
Solving the beam requires the generation of an `AnalysisModel` object. This object takes in the following params as method args:
* beam length
* modulus of elasticity
* second moment of area about the strong (bending) axis
* a `LoadAssembly` object denoting the configuration of applied loads

Note that the solver does NOT account for unit systems. It is therefore imperative that the values used when creating the
`AnalysisModel` are consistent.

An example of model assembly (using SI units) is shown below:
```java
double beamLength = 10.0; // meters
double modulus = 32E9; // Pascals
double momentOfInertia = 0.3*Math.pow(0.6,3)/12; // 0.3m x 0.6m rectangular beam
LoadInstance pointLoad = new LoadInstance(10, 5.0); // point load of value 10 applied at mid point of beam

LoadAssembly loads = new LoadAssembly();
loads.addPointForce(pointLoad);
AnalysisModel model = new AnalysisModel(beamLength, modulus, momentOfInertia, loads);
```

# Model Solving
To solve a model, one simply needs to instantiate a solver instance and pass in the `AnalysisModel` object.
```java
  BeamSolver_SS solver = new BeamSolver_SS(model);
```
Each `BeamSolver_SS` instance exposes 4 methods: 
* `getShear()`
* `getMoment()`
* `getSlope()`
* `getDeflection()`

Each of these methods takes in a distance value - specifically the distance away from the origin of the beam.
```java
BeamSolver_SS solver = new BeamSolver_SS(model);
double startShear = solver.getShear(0.0);
double endShear = solver.getShear(10.0);
double midMoment = solver.getMoment(5.0);
double midDispl = solver.getDeflection(5.0);
double midSlope = solver.getSlope(5.0);
```
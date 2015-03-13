package org.sugarj.cleardep.build;

import java.io.IOException;
import java.io.Serializable;

import org.sugarj.cleardep.BuildUnit;

public interface BuildUnitProvider {

  public 
  <In extends Serializable,
   Out extends Serializable,
   B extends Builder<In, Out>,
   F extends BuilderFactory<In, Out, B>> 
 BuildUnit<Out> require(BuildUnit<?> source, BuildRequest<In, Out, B, F> buildReq) throws IOException;
  
  public void tryCompileCycle(BuildCycleException e) throws Throwable;
  
}

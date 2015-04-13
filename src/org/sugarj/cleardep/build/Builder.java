package org.sugarj.cleardep.build;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.sugarj.cleardep.BuildUnit;
import org.sugarj.cleardep.BuildUnit.State;
import org.sugarj.cleardep.dependency.IllegalDependencyException;
import org.sugarj.cleardep.stamp.LastModifiedStamper;
import org.sugarj.cleardep.stamp.Stamp;
import org.sugarj.cleardep.stamp.Stamper;
import org.sugarj.common.path.Path;

public abstract class Builder<In extends Serializable, Out extends Serializable> {
  protected final In input;
  
  public Builder(In input) {
    this.input = input;
  }
  
  public In getInput() {
    return input;
  }
  
  
  /**
   * Provides the task description for the builder and its input.
   * The description is used for console logging when the builder is run.
   * 
   * @return the task description or `null` if no logging is wanted.
   */
  protected abstract String description();
  
  protected abstract Path persistentPath();
  
  protected abstract Out build() throws Throwable;

  protected CycleSupport getCycleSupport() {
    return null;
  }
  
  protected Stamper defaultStamper() {
    return LastModifiedStamper.instance;
  }

  transient BuildUnit<Out> result;
  transient BuildUnitProvider manager;
  private transient Stamper defaultStamper;
  Out triggerBuild(BuildUnit<Out> result, BuildUnitProvider manager) throws Throwable {
    this.result = result;
    this.manager = manager;
    this.defaultStamper = defaultStamper();
    try {
      return build();
    } finally {
      this.result = null;
      this.manager = null;
      this.defaultStamper = null;
    }
  }
  
  
  protected <
  In_ extends Serializable, 
  Out_ extends Serializable, 
  B_ extends Builder<In_,Out_>,
  F_ extends BuilderFactory<In_, Out_, B_>,
  SubIn_ extends In_
  > Out_ requireBuild(F_ factory, SubIn_ input) throws IOException {
    BuildRequest<In_, Out_, B_, F_> req = new BuildRequest<In_, Out_, B_, F_>(factory, input);
    return requireBuild(req);
  }
  
  protected <
  In_ extends Serializable, 
  Out_ extends Serializable, 
  B_ extends Builder<In_,Out_>,
  F_ extends BuilderFactory<In_, Out_, B_>
  > Out_ requireBuild(BuildRequest<In_, Out_, B_, F_> req) throws IOException {
    BuildUnit<Out_> e = manager.require(req);
    result.requires(e);
    return e.getBuildResult();
  }

  protected void requireBuild(Collection<? extends BuildRequest<?, ?, ?, ?>> reqs) throws IOException {
    if (reqs != null)
      for (BuildRequest<?, ?, ?, ?> req : reqs) {
        requireBuild(req);
      }
  }
  protected void requireBuild(BuildRequest<?, ?, ?, ?>[] reqs) throws IOException {
    if (reqs != null)
      for (BuildRequest<?, ?, ?, ?> req : reqs) {
        requireBuild(req);
      }
  }
  
  public void require(Path p) {
    require(p, defaultStamper.stampOf(p));
  }
  public void require(Path p, Stamper stamper) {
    require(p, stamper.stampOf(p));
  }
  public void require(Path p, Stamp stamp) {
    try {
      result.requires(p, stamp);
    } catch (IllegalDependencyException e) {
      if (e.dep.equals(result.getPersistentPath()))
        try {
          requireBuild(result.getGeneratedBy());
        } catch (IOException e1) {
          throw new RuntimeException(e1);
        }
    }
  }
  
  public void provide(Path p) {
    result.generates(p, LastModifiedStamper.instance.stampOf(p));
  }
  public void provide(Path p, Stamper stamper) {
    result.generates(p, stamper.stampOf(p));
  }

  public void setState(State state) {
    result.setState(state);
  }

  public BuildUnit<Out> getBuildUnit() {
    return result;
  }
}

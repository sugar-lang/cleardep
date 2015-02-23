package org.sugarj.cleardep.build;

import java.io.IOException;
import java.io.Serializable;

import org.sugarj.cleardep.CompilationUnit;
import org.sugarj.cleardep.Mode;

public class BuildRequirement<
  T extends Serializable, 
  E extends CompilationUnit, 
  B extends Builder<T, E>,
  F extends BuilderFactory<T, E, B>
> implements Serializable {
  private static final long serialVersionUID = -1598265221666746521L;
  
  final F factory;
  final T input;
  final Mode<E> mode;

  public BuildRequirement(F factory, T input, Mode<E> mode) {
    this.factory = factory;
    this.input = input;
    this.mode = mode;
  }

  public E createBuilderAndRequire(BuildManager manager) throws IOException {
    return manager.require(factory.makeBuilder(input, manager), mode);
  }
}
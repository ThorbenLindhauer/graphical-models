package com.github.thorbenlindhauer.variable;

public class DiscreteVariable {

  protected int cardinality;
  protected String id;
  
  public DiscreteVariable(String id, int cardinality) {
    this.cardinality = cardinality;
    this.id = id;
  }
  
  public int getCardinality() {
    return cardinality;
  }
  public void setCardinality(int cardinality) {
    this.cardinality = cardinality;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + cardinality;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DiscreteVariable other = (DiscreteVariable) obj;
    if (cardinality != other.cardinality)
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
  
  
}

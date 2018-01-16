package org.cloudfoundry.jmxnozzle;

public class Metric {
  private String name= "";
  private Double value= 0d;

  public Metric(String name, Double value) {
    this.name= name;
    this.value= value;
  }

  public String getName() {
    return name;
  }

  public Double getValue() {
    return value;
  }
}

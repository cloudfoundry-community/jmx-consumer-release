package org.cloudfoundry.jmxnozzle;

import java.util.HashMap;
import java.util.Map;

public class Metric {
  private String name= "";
  private Double value= 0d;
  private long timestamp;
  private String deployment;
  private String job;
  private String index;
  private String IP;
  private Map<String, String> tags;

  public Metric(String name, Double value, long timestamp, Map<String, String> tags) {
    this.tags = new HashMap<String, String>(tags);

    this.name= name;
    this.value= value;
    this.timestamp= timestamp;
    this.deployment= this.tags.remove("deployment");
    this.job= this.tags.remove("job");
    this.index= this.tags.remove("index");
    this.IP= this.tags.remove("ip");
  }

  public String getName() {
    return name;
  }

  public Double getValue() {
    return value;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getDeployment() {
    return deployment;
  }

  public String getJob() {
    return job;
  }

  public String getIndex() {
    return index;
  }

  public String getIP() {
    return IP;
  }

  public Map<String, String> getTags() {
    return tags;
  }
}

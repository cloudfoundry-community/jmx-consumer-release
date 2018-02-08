package org.cloudfoundry.jmxnozzle;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Metric {
  private String name= "";
  private Double value= 0d;
  private long timestamp;
  private String deployment;
  private String job;
  private String index;
  private String IP;
  private String origin;
  private Map<String, String> tags;

  public Metric(String name, Double value, long timestamp, Map<String, String> tags) {
    this.tags = new HashMap<String, String>(tags);
    this.tags = this.tags.entrySet()
            .stream()
            .filter(entry -> !entry.getKey().startsWith("__v1"))
            .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

    this.value= value;
    this.timestamp= timestamp;
    this.deployment= this.tags.remove("deployment");
    this.job= this.tags.remove("job");
    this.index= this.tags.remove("index");
    this.IP= this.tags.remove("ip");
    this.origin= this.tags.remove("origin");
    this.tags.remove("id");

    this.name = generateName(name);
  }

  private String generateName(String name) {
    if (origin != null) {
      name = origin + "." + name;
    }
    if (!this.tags.isEmpty()) {
      TreeSet keys = new TreeSet(this.tags.keySet());
      name += new StringBuilder()
              .append("[")
              .append(
                      keys
                        .stream()
                        .map(key -> String.format("%s=%s", key, this.tags.get(key)))
                        .collect(Collectors.joining(","))
              )
              .append("]")
              .toString();
    }
    return name;
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

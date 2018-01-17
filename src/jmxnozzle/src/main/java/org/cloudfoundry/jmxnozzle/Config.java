package org.cloudfoundry.jmxnozzle;

public class Config {
  private final static int serverPort = 44445;
  private final static int registryPort = 44444;

  private final static String rlpHostUrl = "localhost";
  private final static int rlpPort = 12345;

  public static int getServerPort() {
    return (serverPort);
  }

  public static int getRegistryPort() {
    return (registryPort);
  }

  public String getRLPHost() {    return rlpHostUrl;  }

  public int getRLPPort() {    return rlpPort;  }
}

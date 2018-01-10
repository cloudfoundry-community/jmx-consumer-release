package org.cloudfoundry.jmxnozzle;

public class Config {
  private final static int serverPort = 44445;
  private final static int registryPort = 44444;

  public static int getServerPort () {
    return(serverPort);
  }

  public static int getRegistryPort () {
    return(registryPort);
  }

}

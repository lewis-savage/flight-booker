package com.r00174469.db;


public class Route {

  private int routeId;
  private String startLocation;
  private String endLocation;


  public int getRouteId() {
    return routeId;
  }

  public void setRouteId(int routeId) {
    this.routeId = routeId;
  }


  public String getStartLocation() {
    return startLocation;
  }

  public void setStartLocation(String startLocation) {
    this.startLocation = startLocation;
  }


  public String getEndLocation() {
    return endLocation;
  }

  public void setEndLocation(String endLocation) {
    this.endLocation = endLocation;
  }

  public Route(int routeId, String startLocation, String endLocation) {
      this.routeId = routeId;
      this.startLocation = startLocation;
      this.endLocation = endLocation;
  }
}

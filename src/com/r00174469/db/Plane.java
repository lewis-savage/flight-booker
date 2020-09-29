package com.r00174469.db;


public class Plane {

  private int planeId;
  private String model;
  private int capacityFirstClass;
  private int capacityBusiness;
  private int capacityEconomy;


  public int getPlaneId() {
    return planeId;
  }

  public void setPlaneId(int planeId) {
    this.planeId = planeId;
  }


  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }


  public int getCapacityFirstClass() {
    return capacityFirstClass;
  }

  public void setCapacityFirstClass(int capacityFirstClass) {
    this.capacityFirstClass = capacityFirstClass;
  }


  public int getCapacityBusiness() {
    return capacityBusiness;
  }

  public void setCapacityBusiness(int capacityBusiness) {
    this.capacityBusiness = capacityBusiness;
  }


  public int getCapacityEconomy() {
    return capacityEconomy;
  }

  public void setCapacityEconomy(int capacityEconomy) {
    this.capacityEconomy = capacityEconomy;
  }


    public Plane(int planeId, String model, int capacityFirstClass, int capacityBusiness, int capacityEconomy) {
        this.planeId = planeId;
        this.model = model;
        this.capacityFirstClass = capacityFirstClass;
        this.capacityBusiness = capacityBusiness;
        this.capacityEconomy = capacityEconomy;
    }
    public String toStringMin(){
        return "ID: "+planeId+" Model: "+model;
    }
    public String toString(){
        return String.format("ID: %d, Model: %s, Capacity Economy: %d, Capacity Business: %d, Capacity First Class: %d",planeId,model,capacityEconomy,capacityBusiness,capacityFirstClass);
    }
}

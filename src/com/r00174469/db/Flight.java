package com.r00174469.db;


import com.r00174469.ui.Interface;

import java.sql.Time;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

public class Flight {

    private int flightId;
    private double priceFirstClass;
    private Date date;
    private double priceEconomy;
    private double priceBusiness;
    private Time boardingTime;
    private Time departureTime;
    private int routeId;
    private int planeId;

    public int getPlaneId() {
        return planeId;
    }

    public void setPlaneId(int planeId) {
        this.planeId = planeId;
    }

    public int getFlightId() {
      return flightId;
    }

    public void setFlightId(int flightId) {
      this.flightId = flightId;
    }


    public double getPriceFirstClass() {
      return priceFirstClass;
    }

    public void setPriceFirstClass(double priceFirstClass) {
      this.priceFirstClass = priceFirstClass;
    }


    public Date getDate() {
      return date;
    }

    public void setDate(Date date) {
      this.date = date;
    }


    public double getPriceEconomy() {
      return priceEconomy;
    }

    public void setPriceEconomy(double priceEconomy) {
      this.priceEconomy = priceEconomy;
    }


    public double getPriceBusiness() {
      return priceBusiness;
    }

    public void setPriceBusiness(double priceBusiness) {
      this.priceBusiness = priceBusiness;
    }


    public java.sql.Time getBoardingTime() {
      return boardingTime;
    }

    public void setBoardingTime(java.sql.Time boardingTime) {
      this.boardingTime = boardingTime;
    }


    public java.sql.Time getDepartureTime() {
      return departureTime;
    }

    public void setDepartureTime(java.sql.Time departureTime) {
      this.departureTime = departureTime;
    }


    public int getRouteId() {
      return routeId;
    }

    public void setRouteId(int routeId) {
      this.routeId = routeId;
    }

    public Flight(int flightId, double priceFirstClass, Date date, double priceEconomy, double priceBusiness, Time boardingTime, Time departureTime, int routeId, int planeID) {
        this.flightId = flightId;
        this.priceFirstClass = priceFirstClass;
        this.date = date;
        this.priceEconomy = priceEconomy;
        this.priceBusiness = priceBusiness;
        this.boardingTime = boardingTime;
        this.departureTime = departureTime;
        this.routeId = routeId;
        this.planeId = planeID;
    }

    public Flight(int flightId, double priceFirstClass, String date, double priceEconomy, double priceBusiness, String boardingTime, String departureTime, int routeId, int planeID) {
        this.flightId = flightId;
        this.priceFirstClass = priceFirstClass;
        this.date = java.sql.Date.valueOf(date);
        this.priceEconomy = priceEconomy;
        this.priceBusiness = priceBusiness;
        try {
            this.boardingTime = java.sql.Time.valueOf(boardingTime);
        } catch (Exception e){
            Interface.print(boardingTime);
        }
        try {
            this.departureTime = java.sql.Time.valueOf(departureTime);
        } catch (Exception e){
            Interface.print(departureTime);
        }
        this.routeId = routeId;
        this.planeId = planeID;
    }

    public String toString(){
        String out = "";
        LocalDate date = this.date.toLocalDate();
        out += date.toString() +" ";
        out += this.departureTime.toString();
        out += " Ec: €"+this.priceEconomy+" ";
        out += " Bs: €"+this.priceBusiness+" ";
        out += " Fr: €"+this.priceFirstClass;
        return out;
    }

    public String toStringMin(){
        String out = "";
        LocalDate date = this.date.toLocalDate();
        out += date.toString() +" ";
        out += this.departureTime.toString();
        return out;
    }
}

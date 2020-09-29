package com.r00174469.db;

public class Passenger {
    private String name;
    private String email;
    private int age;
    private int passengerID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPassengerID() {
        return passengerID;
    }

    public void setPassenger_id(int passengerID) {
        this.passengerID = passengerID;
    }

    public Passenger(String name, String email, int age, int passengerID) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.passengerID = passengerID;
    }
    public String toString(){
        return String.format("%s: Age %d, Email %s, ID %d",this.name,this.age,this.email,this.passengerID);
    }
    public void print(){
        System.out.println(toString());
    }
}
package com.r00174469.db;

public class Airport {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Airport(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public void print(){
        System.out.println(name);
    }
}

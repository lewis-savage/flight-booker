package com.r00174469.db;

public class Booking {
    private int bookingID;
    private int flightID;
    private String passportNumber;
    private int passengerID;
    public enum BookingType {
        First,
        Business,
        Economy
    }

    private BookingType bookingType;

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }


    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getFlightID() {
        return flightID;
    }

    public void setFlightID(int flightID) {
        this.flightID = flightID;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public int getPassengerID() {
        return passengerID;
    }

    public void setPassengerID(int passengerID) {
        this.passengerID = passengerID;
    }

    public Booking(){

    }

    public Booking(int bookingID, int flightID, String type, String passportNumber, int passengerID) {
        this.bookingID = bookingID;
        this.flightID = flightID;
        this.bookingType = BookingType.valueOf(type);
        this.passportNumber = passportNumber;
        this.passengerID = passengerID;
    }
    public String toString(){
        return String.format("Booking ID: %d flight ID %d for passenger %d with passport number %s",this.bookingID,this.flightID,this.passengerID,this.passportNumber);
    }
    public void print(){
        System.out.println(toString());
    }
}

package com.r00174469.db;

import java.sql.Date;
public class BookingDetails {
    private Date date;
    private String fromLocation;
    private String toLocation;
    private int bookingID;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public BookingDetails(Date date, String fromLocation, String toLocation, int bookingID) {
        this.date = date;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.bookingID = bookingID;
    }
}

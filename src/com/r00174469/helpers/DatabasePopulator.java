package com.r00174469.helpers;

import com.r00174469.db.*;
import com.r00174469.ui.Interface;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DatabasePopulator {

    private static String[] locations = new String[]{
            "Cork",
            "Dublin",
            "Stanstead",
            "Gatwick",
            "Heathrow",
            "Luton",
            "Amsterdam",
            "JFK",
            "LAX",
            "Aberdeen",
            "Sydney",
            "Budapest",
            "Dubai"
    };

    private static String[] firstNames = new String[]{"John","Robert","Jorden","James","Dean","Micheal","William","David","Richard","Joseph","Thomas","Charles"};
    private static String[] lastNames = new String[]{"Jones","Smith","Taylor","Davies","Brown","Wilson","Evans"};

    private static Plane[] planes = new Plane[]{
            new Plane(0,"Boeing 747",4,30,150),
            new Plane(0,"Boeing 777",6,30,125),
            new Plane(0,"Boeing 767",4,32,175),
            new Plane(0,"Boeing 737-800",4,30,150),
            new Plane(0,"Airbus A380",20,100,450)
    };

    public static AirlineDatabaseManipulator manipulator;
    public static void main(String[] args) {

        manipulator = new AirlineDatabaseManipulator("jdbc:mysql://localhost:3306/r00174469", "connector", "connector");
        //Add calls to the various "addN" functions to populate the database"
    }

    public static void addNPlanes(int numPlanes){
        for (int i = 0; i < numPlanes; i++) {
            manipulator.addPlane(generatePlane());
        }
    }
    public static void addNPassengers(int numPassengers){

        for (int i = 0; i < numPassengers; i++) {
            manipulator.addPassenger(generatePassenger());
        }

    }
    public static void addNFlights(int numFlights){
        Route[] routes = manipulator.getRoutes();
        Plane[] planes = manipulator.getPlanesByName("");

        for (int i = 0; i < numFlights; i++) {
            manipulator.addFlight(generateFlight(planes,routes));
        }
    }
    public static void addNBookings(int numBookings){
        Flight[] flights = manipulator.getFlights();
        Passenger[] passengers = manipulator.getPassengersByName("");

        for (int i = 0; i < numBookings; i++) {
            manipulator.addBooking(generateBooking(passengers,flights));
        }
    }

    public static void addNRoutes(int numRoutes){
        for (int i = 0; i < numRoutes; i++) {
            manipulator.addRoute(generateRoute());
        }
    }

    public static Route generateRoute(){
        Random r = new Random();
        int startIndex = r.nextInt(locations.length);
        String start = locations[startIndex];
        int endIndex = -1;
        boolean foundEnd = false;
        while(!foundEnd){
            endIndex = r.nextInt(locations.length);
            if(endIndex!=startIndex){
                foundEnd = true;
            }
        }
        String end = locations[endIndex];
        return new Route(0,start,end);
    }

    public static Flight generateFlight(Plane[] planeIDs, Route[] routeIDs){
        Random r = new Random();
        LocalDate flightDate = createRandomDate(2020,2025);
        LocalTime boardTime = randomTime().toLocalTime();
        LocalTime departureTime = randomTime().toLocalTime().plusMinutes(30);
        double priceEc = (r.nextDouble() + 0.5) * 50;
        double priceBs = (r.nextDouble() + 0.5) * 150;
        double priceFr = (r.nextDouble() + 0.5) * 250;
        int planeID = planeIDs[r.nextInt(planeIDs.length)].getPlaneId();
        int routeID = routeIDs[r.nextInt(routeIDs.length)].getRouteId();
        return new Flight(0,priceFr,flightDate.toString(),priceEc,priceBs,boardTime.toString(),departureTime.toString(),routeID,planeID);
    }

    public static Booking generateBooking(Passenger[] passengers, Flight[] flights){
        Random r = new Random();
        int passengerID = passengers[r.nextInt(passengers.length)].getPassengerID();
        int flightID = flights[r.nextInt(flights.length)].getFlightId();
        String passportNumber = ""+r.nextInt(10)+r.nextInt(10)+r.nextInt(10)+r.nextInt(10)+r.nextInt(10)+r.nextInt(10)+r.nextInt(10)+r.nextInt(10)+r.nextInt(10)+r.nextInt(10);
        Booking.BookingType type = null;
        int bookingType = r.nextInt(3);
        switch (bookingType){
            case 0:
                type = Booking.BookingType.Economy;
                break;
            case 1:
                type = Booking.BookingType.Business;
                break;
            case 2:
                type = Booking.BookingType.First;
                break;
        }
        Booking b = new Booking(0,flightID,type.toString(),passportNumber,passengerID);
        return b;
    }

    public static Passenger generatePassenger(){
        Random r = new Random();
        String firstName = firstNames[r.nextInt(firstNames.length)];
        String lastName = lastNames[r.nextInt(lastNames.length)];
        String email = firstName.substring(0,1) + lastName +r.nextInt(9+1)+r.nextInt(9+1)+r.nextInt(9+1)+r.nextInt(9+1)+"@gmail.com";
        Passenger passenger = new Passenger(firstName+" "+lastName,email,r.nextInt(50)+12,0);
        return passenger;
    }

    public static Plane generatePlane(){
        Random r = new Random();
        return planes[r.nextInt(planes.length)];
    }

    public static int createRandomIntBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    public static LocalDate createRandomDate(int startYear, int endYear) {
        int day = createRandomIntBetween(1, 28);
        int month = createRandomIntBetween(1, 12);
        int year = createRandomIntBetween(startYear, endYear);
        return LocalDate.of(year, month, day);
    }

    public static Time randomTime(){
        int millisInDay = 24*60*60*1000;
        Random random = new Random();
        Time time = new Time((long)random.nextInt(millisInDay));
        return time;
    }
}

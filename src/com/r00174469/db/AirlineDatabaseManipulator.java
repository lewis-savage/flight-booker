package com.r00174469.db;
import com.r00174469.ui.Interface;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AirlineDatabaseManipulator extends DatabaseConnector  {

    public AirlineDatabaseManipulator(String url, String user, String password) {
        super(url, user, password);
    }

    //Section: Passengers

    private Passenger createPassenger(ResultSet results) throws  java.sql.SQLException{
        results.next();
        String pName = results.getString(2);
        int pAge = results.getInt(4);
        String pEmail = results.getString(3);
        int pID = results.getInt(1);
        return new Passenger(pName, pEmail, pAge, pID);
    }

    public boolean addPassenger(String name, String email, int age){
        String statement = "INSERT INTO passengers (name, email, age) VALUES (?,?,?)";
        int rows = (int)query(statement,name,email, age);
        return rows > 0;
    }
    public boolean addPassenger(Passenger p){
        return addPassenger(p.getName(),p.getEmail(),p.getAge());
    }

    public Passenger getPassengerByID(int ID){
        String statement = "SELECT * FROM passengers where passenger_id = ?";
        ResultSet results = (ResultSet)query(statement,ID);
        try {
            results.beforeFirst();
            return createPassenger(results);
        } catch (java.sql.SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    public boolean removePassengerByID(int ID){
        String statement = "DELETE from passengers where passenger_id = ?";
        Object query = query(statement, ID);
        if(query instanceof String){
            if(query.equals("FKError")){
                return false;
            }
        }
        int deleted = (int)query;
        return deleted > 0;
    }

    public Passenger[] getPassengersByName(String name){
        name = "%" + name + "%";
        String statement = "SELECT * FROM passengers where name like ?";
        ResultSet results = (ResultSet)query(statement,name);
        Passenger[] passengers = new Passenger[getRowCount(results)];
        for (int i = 0; i < passengers.length; i++) {
            try {
                passengers[i] = createPassenger(results);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return passengers;
    }

    //Section: Bookings

    public BookingDetails createBookingDetails(ResultSet results) throws java.sql.SQLException{
        results.next();
        int bID = results.getInt(1);
        java.sql.Date date = results.getDate(2);
        String from = results.getString(3);
        String to = results.getString(4);
        return new BookingDetails(date, from, to, bID);
    }

    public BookingDetails bookingDetailsFromID (int ID){
        String statement = "SELECT  b.booking_id, f.date, r.start_location, r.end_location\n" +
                "From flights f\n" +
                "join bookings b on f.flight_id = b.flight_id\n" +
                "join routes r on r.route_id = f.route_id\n" +
                "where b.booking_id = ?";
        ResultSet results = (ResultSet)query(statement,ID);
        try {
            return createBookingDetails(results);

        } catch (java.sql.SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean removeAllBookingsForPassenger(int ID){
        String statement = "DELETE FROM bookings WHERE passenger_id = ?";
        int rows = (int)query(statement,ID);
        return rows > 0;
    }

    public Booking createBooking(ResultSet results) throws java.sql.SQLException{

        results.next();
        int bID = results.getInt(1);
        int fID = results.getInt(2);
        String pNum = results.getString(3);
        String type = results.getString(4);
        int pID = results.getInt(5);
        return new Booking(bID, fID, type, pNum, pID);

    }

    public Booking getBookingFromBookingID(int ID) {
        String statement = "SELECT * FROM bookings WHERE booking_id = ?";
        ResultSet results = (ResultSet) query(statement, ID);
        try {
            return createBooking(results);

        } catch (java.sql.SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean cancelBooking(int ID){
        String statement = "DELETE FROM bookings WHERE booking_id = ?";
        int updated = (int)query(statement,ID);
        return updated > 0;
    }

    public boolean addBooking(Booking booking) {
        //Get the flight we're trying to book for
        Flight flight = getFlightFromID(booking.getFlightID());
        Plane plane = getPlaneFromPlaneID(flight.getPlaneId());
        Booking[] existingBookings = getBookingsFromFlightID(flight.getFlightId());
        int numEc = 0;
        int numBs = 0;
        int numFr = 0;
        //Check if there are any seats available on the current flight
        for (Booking book : existingBookings){
            switch (book.getBookingType()){

                case First:
                    numFr++;
                    break;
                case Business:
                    numBs++;
                    break;
                case Economy:
                    numEc++;
                    break;
            }
        }
        boolean canMakeBooking = true;
        switch (booking.getBookingType()){

            case First:
                if(numFr >= plane.getCapacityFirstClass()){
                    canMakeBooking = false;
                }
                break;
            case Business:
                if(numBs >= plane.getCapacityBusiness()){
                    canMakeBooking = false;
                }
                break;
            case Economy:
                if(numEc >= plane.getCapacityEconomy()){
                    canMakeBooking = false;
                }
                break;
        }
        int updated = 0;
        if(canMakeBooking) {
            //Finally add the booking
            String statement = "INSERT INTO bookings (flight_id, passport_number, booking_type, passenger_id) VALUES (?,?,?,?)";
            updated = (int) query(statement, booking.getFlightID(), booking.getPassportNumber(), booking.getBookingType().toString(), booking.getPassengerID());
        }
        return updated > 0;
    }

    private Booking[] createBookingsFromResultSet(ResultSet results){
        Booking[] bookings = new Booking[getRowCount(results)];
        for (int i = 0; i < bookings.length; i++) {
            try {
                bookings[i] = createBooking(results);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bookings;
    }

    private Booking[] getBookingsFromFlightID(int ID) {
        String statement = "SELECT * FROM bookings WHERE flight_id = ?";
        ResultSet results = (ResultSet)query(statement,ID);
        return createBookingsFromResultSet(results);
    }

    public Booking[] getBookingsFromPassengerID(int ID){
        String statement = "SELECT * FROM bookings WHERE passenger_id = ?";
        ResultSet results = (ResultSet)query(statement,ID);
        return createBookingsFromResultSet(results);
    }

    //Section: Locations

    private Airport createAirport(ResultSet results) throws java.sql.SQLException {

        results.next();
        String locationName = results.getString(1);
        return new Airport(locationName);

    }

    public Airport[] getDepartureLocations() {
        return getDepartureLocations("");
    }

    public Airport[] getDepartureLocations(String name){
        name = "%"+name+"%";
        String statement = "SELECT DISTINCT start_location FROM routes WHERE start_location like ?";
        ResultSet results = (ResultSet)query(statement,name);
        return createAirportsFromResultSet(results);
    }

    private Airport[] createAirportsFromResultSet(ResultSet results){
        Airport[] airports = new Airport[getRowCount(results)];
        for (int i = 0; i < airports.length; i++) {
            try {
                airports[i] = createAirport(results);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return airports;
    }

    public Airport[] getArrivalLocations(Airport departure) {
        return getArrivalLocations(departure,"");
    }

    public Airport[] getArrivalLocations(Airport departure, String name){
        name = "%"+name+"%";
        String statement = "SELECT DISTINCT end_location FROM routes WHERE start_location like ? AND end_location LIKE ?";
        ResultSet results = (ResultSet)query(statement,departure.getName(),name);
        return createAirportsFromResultSet(results);
    }

    //Section: Routes

    public Route createRoute(ResultSet results) throws java.sql.SQLException{

        results.next();
        int routeID = results.getInt(1);
        String startLocation = results.getString(2);
        String endLocation = results.getString(3);
        return new Route(routeID,startLocation,endLocation);

    }

    private Route[] createRoutesFromResultSet(ResultSet results){
        Route[] routes = new Route[getRowCount(results)];
        for (int i = 0; i < routes.length; i++) {
            try {
                routes[i] = createRoute(results);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return routes;
    }

    public boolean addRoute(String fromLocation, String toLocation) {
        //Check if route already exists first
        String checkStatement = "SELECT * FROM routes WHERE start_location = ? AND end_location = ?";
        ResultSet results = (ResultSet)query(checkStatement,fromLocation,toLocation);
        if(getRowCount(results)==0) {
            String statement = "INSERT INTO routes (start_location, end_location) VALUES (?,?)";
            int rows = (int) query(statement, fromLocation, toLocation);
            return rows > 0;
        }else{
            return false;
        }
    }
    public boolean addRoute(Route r) {
        return addRoute(r.getStartLocation(),r.getEndLocation());
    }

    public boolean removeRoute(String from, String to) {
        String statement = "DELETE FROM routes WHERE start_location = ? AND end_location = ?";
        Object query = query(statement,from,to);
        if(query instanceof String){
            if(query.equals("FKError")){
                return false;
            }
        }else{

        }
        return false;
    }

    public Route getRouteFromStartAndEnd(String start, String end){
        String statement = "SELECT * FROM routes WHERE start_location = ? AND end_location = ?";
        ResultSet results = (ResultSet)query(statement,start,end);
        return createRoutesFromResultSet(results)[0];
    }
    public Route[] getRoutes(){
        String statement = "SELECT * FROM routes";
        ResultSet results = (ResultSet)query(statement);
        return createRoutesFromResultSet(results);
    }

    //Section: Flights

    private Flight createFlight(ResultSet results) throws java.sql.SQLException{
        results.next();
        int flightID = results.getInt(1);
        double priceFirstClass = results.getDouble(2);
        java.sql.Date flightDate = null;
        try {
            flightDate = results.getDate(3);
        }catch (Exception e){
            Interface.print(results.getString(3));
        }
        double priceEconomy = results.getDouble(4);
        double priceBusiness = results.getDouble(5);
        java.sql.Time boardingTime = results.getTime(6);
        java.sql.Time departureTime  = results.getTime(7);
        int routeID = results.getInt(8);
        int planeID = results.getInt(9);
        return new Flight(flightID,priceFirstClass,flightDate,priceEconomy,priceBusiness,boardingTime,departureTime,routeID,planeID);
    }

    public boolean addFlight(Flight flight) {
        String statement = "INSERT INTO flights (price_first_class, date, price_economy, price_business, boarding_time, departure_time, route_id, plane_id) VALUES (?,?,?,?,?,?,?,?)";
        float priceFr = (float) flight.getPriceFirstClass();
        float priceEc = (float) flight.getPriceEconomy();
        float priceBs = (float) flight.getPriceBusiness();
        String date = flight.getDate().toLocalDate().toString();
        String depTime = flight.getDepartureTime().toString();
        String boardingTime = flight.getBoardingTime().toString();
        int routeID = flight.getRouteId();
        int planeID = flight.getPlaneId();
        int rows = (int)query(statement,priceFr,date,priceEc,priceBs,boardingTime,depTime,routeID,planeID);
        return rows > 0;
    }

    public Flight getFlightFromID(int ID){
        String statement = "SELECT * FROM flights WHERE flight_id = ?";
        ResultSet results = (ResultSet)query(statement,ID);
        return createFlightsFromResultSet(results)[0];
    }

    public Flight[] getFlights(){
        String statement = "SELECT * FROM flights";
        ResultSet results = (ResultSet)query(statement);
        return createFlightsFromResultSet(results);
    }

    private Flight[] createFlightsFromResultSet(ResultSet results){
        Flight[] flights = new Flight[getRowCount(results)];
        for (int i = 0; i < flights.length; i++) {
            try {
                flights[i] = createFlight(results);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flights;
    }

    public boolean removeFlight(int ID) {
        String statement = "DELETE from flights where flight_id = ?";
        Object query = query(statement, ID);
        if(query instanceof String){
            if(query.equals("FKError")){
                return false;
            }
        }
        int deleted = (int)query;
        return deleted > 0;
    }

    public boolean removeAllBookingsForFlight(int ID) {
        String statement = "DELETE FROM bookings WHERE flight_id = ?";
        int rows = (int)query(statement,ID);
        return rows > 0;
    }

    public Flight[] getFlightsFromRouteID(int ID){
        String statement = "SELECT * FROM flights WHERE flight_id = ?";
        ResultSet results = (ResultSet)query(statement,ID);
        return createFlightsFromResultSet(results);
    }

    public Flight[] getFlightsFromRoute(Airport to, Airport from){
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String statement = "SELECT * FROM flights WHERE route_id IN (SELECT route_id FROM routes WHERE start_location like ? AND end_location LIKE ?) AND date >= ?";
        ResultSet results = (ResultSet)query(statement,to.getName(),from.getName(),date);
        return createFlightsFromResultSet(results);
    }

    public Flight[] getFlightsFromRouteAndDate(Airport to, Airport from, String dateConfirm) {
        String statement = "SELECT * FROM flights WHERE route_id IN (SELECT route_id FROM routes WHERE start_location like ? AND end_location LIKE ?) AND date = ?";
        ResultSet results = (ResultSet)query(statement,to.getName(),from.getName(), dateConfirm);
        return createFlightsFromResultSet(results);
    }

    //Section: Planes

    private Plane createPlane (ResultSet results) throws java.sql.SQLException{
        results.next();
        int planeID = results.getInt(1);
        String model = results.getString(2);
        int capEc = results.getInt(3);
        int capBus = results.getInt(4);
        int capFirst = results.getInt(5);
        return new Plane(planeID,model,capEc,capBus,capFirst);
    }

    public boolean addPlane(Plane plane) {
        String statement = "INSERT INTO planes (model, capacity_first_class, capacity_business, capacity_economy) VALUES (?,?,?,?)";
        int rows = (int)query(statement,plane.getModel(),plane.getCapacityFirstClass(),plane.getCapacityBusiness(),plane.getCapacityEconomy());
        return rows > 0;
    }

    private Plane[] createPlanesFromResultSet(ResultSet results){
        Plane[] planes = new Plane[getRowCount(results)];
        for (int i = 0; i < planes.length; i++) {
            try {
                planes[i] = createPlane(results);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return planes;
    }

    public boolean removePlane(int planeId) {

        String statement = "DELETE FROM planes WHERE plane_id = ?";
        Object query = query(statement,planeId);
        if(query instanceof String){
            if(query.equals("FKError")){
                return false;
            }else{
                return false;
            }
        }else{
            return (int)query > 0;
        }
    }

    public Plane getPlaneByID(int potentialID) {
        String statement = "SELECT * FROM planes WHERE plane_id = ?";
        ResultSet results = (ResultSet)query(statement,potentialID);
        return createPlanesFromResultSet(results)[0];
    }

    public Plane[] getPlanesByName(String input) {
        input = "%"+input+"%";
        String statement = "SELECT * FROM planes WHERE model like ?";
        ResultSet results = (ResultSet)query(statement,input);
        return createPlanesFromResultSet(results);
    }

    private Plane getPlaneFromPlaneID(int ID) {
        String statement = "SELECT * FROM planes WHERE plane_id = ?";
        ResultSet results = (ResultSet)query(statement,ID);
        return createPlanesFromResultSet(results)[0];
    }

    //Section: Utility

    private int getRowCount(ResultSet resultSet) {
        if (resultSet == null) {
            return 0;
        }
        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            try {
                resultSet.beforeFirst();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }
        return 0;
    }

}


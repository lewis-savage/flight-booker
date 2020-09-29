package com.r00174469.ui;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Scanner;
import com.r00174469.db.*;

public class Interface {
    private static AirlineDatabaseManipulator manipulator;
    private static Scanner scanner;
    private static final int PAGE_SIZE = 25;
    private static final int LOG_LEVEL = 0;
    //LOG_LEVELS:
    //0: Prompts
    //1: Test messages
    public static void main(String[] args) {
        //Create a database connection
        manipulator = new AirlineDatabaseManipulator("jdbc:mysql://localhost:3306/r00174469", "connector", "connector");
        scanner = new Scanner(System.in);
        displayGreeting();
        displayActions();
        close();
    }

    private static void clearConsole(){
        for (int i = 0; i < PAGE_SIZE; i++) {
            System.out.println();
        }
    }

    private static void displayGreeting(){
        print("                 __|__\n" +
                "          --@--@--(_)--@--@--");
        banner("Airline Database Manager");

    }

    private static void displayActions(){
        boolean done = false;
        banner("Categories");
        System.out.println("1. Passengers");
        System.out.println("2. Bookings");
        System.out.println("3. Routes");
        System.out.println("4. Planes");
        System.out.println("5. Flights");
        System.out.println("6. Close");
        int parsedInt = validatedInput(Integer.class, "Select an action (1-6)", CheckType.Range,1,6);

        switch(parsedInt) {
            case 1:
                displayPassengerSubMenu();
                break;
            case 2:
                displayBookingsSubMenu();
                break;
            case 3:
                displayRoutesSubmenu();
                break;
            case 4:
                displayPlanesSubmenu();
                break;
            case 5:
                displayFlightsSubmenu();
                break;
            case 6:
                done = true;
                break;
        }
        if(!done){
            print("Press enter to continue");
            scanner.nextLine();
            clearConsole();
            displayActions();
        }
    }

    private static void displayBookingsSubMenu() {
        clearConsole();
        banner("Bookings");
        System.out.println("1. Start a booking");
        System.out.println("2. Cancel a booking");
        System.out.println("3. Get a passengers booking");
        System.out.println("4. Back");
        int parsedInt = validatedInput(Integer.class, "Select an action (1-4)",CheckType.Range,1,4);
        switch(parsedInt) {
            case 1:
                startBooking();
                break;
            case 2:
                cancelBooking();
                break;
            case 3:
                findBookingsByPassenger();
            case 4:
                break;
        }
    }

    private static void displayRoutesSubmenu() {
        clearConsole();
        banner("Routes");
        System.out.println("1. Add a new route");
        System.out.println("2. Remove a route");
        System.out.println("3. Check a route");
        System.out.println("4. Back");
        int parsedInt = validatedInput(Integer.class, "Select an action (1-4)",CheckType.Range,1,4);
        switch(parsedInt) {
            case 1:
                addRoute();
                break;
            case 2:
                removeRoute();
                break;
            case 3:
                checkRoute();
            case 4:
                break;
        }
    }
    private static void displayPlanesSubmenu() {
        clearConsole();
        banner("Planes");
        System.out.println("1. Add a new plane");
        System.out.println("2. Remove a plane");
        System.out.println("3. Get a planes details");
        System.out.println("4. Back");
        int parsedInt = validatedInput(Integer.class, "Select an action (1-4)",CheckType.Range,1,4);
        switch(parsedInt) {
            case 1:
                addPlane();
                break;
            case 2:
                removePlane();
                break;
            case 3:
                checkPlane();
            case 4:
                break;
        }
    }
    private static void displayFlightsSubmenu() {
        clearConsole();
        banner("Flights");
        System.out.println("1. Create a flight");
        System.out.println("2. Remove a flight");
        System.out.println("3. Get a flights details");
        System.out.println("4. Back");
        int parsedInt = validatedInput(Integer.class, "Select an action (1-4)",CheckType.Range,1,4);
        switch(parsedInt) {
            case 1:
                createFlight();
                break;
            case 2:
                removeFlight();
                break;
            case 3:
                checkFlight();
            case 4:
                break;
        }
    }

    private static void createFlight() {
        print("Create a new flight");
        //1. Get route we want to use
        Airport from = queryDepartureAirport("Where does the route start?", false);
        if (from == null) {
            print("Cancelling flight creation");
            return;
        }
        Airport to = queryArrivalAirport("Where does the route end?", from);
        if (to == null) {
            print("Cancelling flight creation");
            return;
        }
        Route route = manipulator.getRouteFromStartAndEnd(from.getName(),to.getName());
        int routeID = route.getRouteId();

        //2. Get the plane we want to use
        Plane plane = queryPlane("Enter name or ID of plane you would like to assign to this flight");
        int planeID = plane.getPlaneId();

        //3. Set departure date
        String departureDate = dateFromInput("What date is the flight departing? (yyyy-mm-dd) (Empty to cancel)");
        if(departureDate.equals("")){
            print("Cancelled flight creation");
            return;
        }
        //4. Set boarding time
        String boardingTime = timeFromInput("What time is this flight boarding? (HH:MM:SS) (Empty to cancel)");
        if(boardingTime.equals("")){
            print("Cancelled flight creation");
            return;
        }
        //5. Set departure time
        String departureTime = timeFromInput("What time is this flight departing? (HH:MM:SS) (Empty to cancel)");
        if(departureTime.equals("")){
            print("Cancelled flight creation");
            return;
        }
        //6. Set prices
        float priceEc = validatedInput(Float.class,"Enter the price for economy seats: ",CheckType.Above,0);
        float priceBs = validatedInput(Float.class,"Enter the price for business seats: ",CheckType.Above,0);
        float priceFr = validatedInput(Float.class,"Enter the price for first class seats: ",CheckType.Above,0);

        Flight flight = new Flight(0,priceFr,departureDate,priceEc,priceBs,boardingTime,departureTime,routeID,planeID);
        boolean doAdd = confirm("Are you sure you want to add this flight?:\n"+flight.toString());
        if(doAdd){
            boolean added = manipulator.addFlight(flight);
            if(added){
                print("Added flight.");
            }else{
                print("Failed to add flight.");
            }
        } else {
            print("Cancelled flight creation");
        }
    }

    private static void removeFlight() {

        Airport from = queryDepartureAirport("Where is the flight flying from?", false);
        if (from == null) {
            print("Cancelling removal");
            return;
        }
        Airport to = queryArrivalAirport("Where is the flight flying to?", from);
        if (to == null) {
            print("Cancelling removal");
            return;
        }
        String dateConfirm = dateFromInput("What date is the flight? (yyyy-mm-dd) (Enter to show all flights)");
        Flight[] flights = null;
        if (dateConfirm.equals("")) {
            flights = manipulator.getFlightsFromRoute(from, to);
        } else {
            flights = manipulator.getFlightsFromRouteAndDate(from, to, dateConfirm);
        }
        print("" + flights.length + " Flights found");
        for (int i = 0; i < flights.length; i++) {
            print(i + 1 + ". " + from.getName() + "->" + to.getName() + " at " + flights[i].toString());
        }
        int flightIndex = validatedInput(Integer.class, "Which flight would the passenger like to book?", CheckType.Range, 1, flights.length) - 1;
        boolean doRemove = confirm("Are you sure you want to remove this flight?\n"+flights[flightIndex].toString());
        if(doRemove){
            //If removal failed, it is most likely a foreign key issue, if so prompt to remove all bookings with this flight.
            boolean exitFlag = false;
            int flightID = flights[flightIndex].getFlightId();
            while(!exitFlag) {
                if (doRemove) {
                    if (manipulator.removeFlight(flights[flightIndex].getFlightId())){
                        print("Removed flight with ID: " + flightID);
                        exitFlag = true;
                    } else {
                        print("Failed to remove flight, flight has existing bookings.");
                        boolean removeAllBookings = confirm("Would you like to cancel all bookings for this flight remove it?");
                        if(removeAllBookings) {
                            manipulator.removeAllBookingsForFlight(flightID);
                        }else{
                            exitFlag = true;
                        }
                    }
                }
            }
        }else{
            print("Cancelling flight removal");
        }
    }

    private static void checkFlight() {
        Airport from = queryDepartureAirport("Where is the flight flying from?", false);
        if (from == null) {
            print("Cancelling check");
            return;
        }
        Airport to = queryArrivalAirport("Where is the flight flying to?", from);
        if (to == null) {
            print("Cancelling check");
            return;
        }
        String dateConfirm = dateFromInput("What date is the flight? (yyyy-mm-dd) (Enter to show all flights)");
        Flight[] flights = null;
        if (dateConfirm.equals("")) {
            flights = manipulator.getFlightsFromRoute(from, to);
        } else {
            flights = manipulator.getFlightsFromRouteAndDate(from, to, dateConfirm);
        }
        print("" + flights.length + " Flights found");
        for (int i = 0; i < flights.length; i++) {
            print(i + 1 + ". " + from.getName() + "->" + to.getName() + " at " + flights[i].toString());
        }
        int flightIndex = validatedInput(Integer.class, "Which flight would the passenger like to book?", CheckType.Range, 1, flights.length) - 1;
        print(flights[flightIndex].toString());
    }


    private static void addPlane() {
        print("Adding a new plane to our fleet");
        String model = validatedInput(String.class,"Enter planes model: ",CheckType.MinLength,1);
        int capEc = validatedInput(Integer.class,"Enter capacity for economy passengers: ",CheckType.Above,0);
        int capBs = validatedInput(Integer.class,"Enter capacity for business passengers: ",CheckType.Above,0);
        int capFr = validatedInput(Integer.class,"Enter capacity for first class passengers: ",CheckType.Above,0);
        Plane plane = new Plane(0,model,capFr,capBs,capEc);
        boolean added = manipulator.addPlane(plane);
        if(added){
            print("Added plane to fleet");
        }else{
            print("Failed to add plane");
        }

    }

    private static Plane queryPlane(String message){
        print(message);
        String input = scanner.nextLine();
        int potentialID = -1;
        boolean usingID = false;
        try {
            potentialID = Integer.parseInt(input);
            usingID = true;
        }catch (Exception e){
            print("Not a valid ID, using name",1);
        }
        if(usingID){
            return manipulator.getPlaneByID(potentialID);
        }else{
            Plane[] planes = manipulator.getPlanesByName(input);
            if(planes.length>0){
                print("Choose a plane");
                for (int i = 0; i < planes.length; i++) {
                    print((i+1)+". "+planes[i].toStringMin());
                }
                int index = validatedInput(Integer.class, String.format("Select plane (%d-%d)",1,planes.length),CheckType.Range,1,planes.length) - 1;
                return planes[index];
            }else{
                print("No planes matching that name");
            }
        }
        return null;
    }

    private static void removePlane() {
        Plane plane = queryPlane("Enter plane name or ID to remove.");
        boolean doRemove = confirm("Are you sure you want to remove this plane from the fleet?");
        if(doRemove){
            boolean removed = manipulator.removePlane(plane.getPlaneId());
            if(removed){
                print("Plane removed from fleet");
            }else{
                print("Plane being used in existing flights, removal cancelled");
            }
        }else{
            print("Removal cancelled.");
        }
    }

    private static void checkPlane() {
        Plane plane = queryPlane("Enter plane name or ID to check.");
        print(plane.toString());
    }

    private static void addRoute(){
        print("Creating a new route");
        String fromLocation = validatedInput(String.class,"Enter starting location name",CheckType.MinLength,1);
        String toLocation = validatedInput(String.class,"Enter landing location name",CheckType.MinLength,1);
        print("Adding route");
        boolean added = manipulator.addRoute(fromLocation,toLocation);
        if(added){
            print("Route added");
        }else{
            print("Route already exists");
        }
    }

    private static void removeRoute(){
        Airport from = queryDepartureAirport("Where does the route start?", false);
        if (from == null) {
            print("Cancelling removal");
            return;
        }
        Airport to = queryArrivalAirport("Where does the route end?", from);
        if (to == null) {
            print("Cancelling removal");
            return;
        }
        if(manipulator.removeRoute(from.getName(),to.getName())){
            print("Route removed");
        }else{
            print("Failed to remove route, route still in use");
            //TODO: Potentially prompt to remove all flights using route
        }
    }

    private static void checkRoute(){
        Airport from = queryDepartureAirport("Where does the route start?", false);
        if (from == null) {
            print("Route doesn't exist");
            return;
        }
        Airport to = queryArrivalAirport("Where does the route end?", from);
        if (to == null) {
            print("Route doesn't exist");
            return;
        }
    }

    private static void findBookingsByPassenger() {
        Passenger passenger = queryForPassenger("Enter passenger name or ID to find bookings for");
        Booking[] bookings = manipulator.getBookingsFromPassengerID(passenger.getPassengerID());
        if(bookings.length==0){
            print("No bookings for that passenger have been made");
        }
        int i = 0;
        for (Booking booking: bookings) {
            BookingDetails details = manipulator.bookingDetailsFromID(bookings[i].getBookingID());
            print((i+1) + ". "+details.getFromLocation()+"->"+details.getToLocation()+" "+details.getDate().toLocalDate().toString());
            i++;
        }
    }

    private static void cancelBooking() {
        Passenger passenger = queryForPassenger("Enter passenger name or ID");
        if(passenger == null) {
            print("Cancelled, passenger does not exist");
        } else {
            Booking[] bookings = manipulator.getBookingsFromPassengerID(passenger.getPassengerID());
            for (int i = 0; i < bookings.length; i++) {
                BookingDetails details = manipulator.bookingDetailsFromID(bookings[i].getBookingID());
                print((i+1) + ". "+details.getFromLocation()+"->"+details.getToLocation()+" "+details.getDate().toLocalDate().toString());
            }
            int toCancel = validatedInput(Integer.class,"Select a flight to cancel",CheckType.Range,1,bookings.length);
            boolean doCancel = confirm("Are you sure you want to cancel this booking?");
            if(doCancel) {
                boolean cancelled = manipulator.cancelBooking(toCancel);
            }
        }
    }


    private static Airport queryDepartureAirport(String message, boolean arrival){
        print(message);
        String fromLocation = scanner.nextLine();

        Airport[] airports = manipulator.getDepartureLocations(fromLocation);

        if(airports.length > 0 ){
            print("Choose an airport");
            for (int i = 0; i < airports.length; i++) {
                print((i+1)+". "+ airports[i].toString());
            }
            int index = validatedInput(Integer.class, String.format("Select airport (%d-%d)",1,airports.length),CheckType.Range,1,airports.length) - 1;
            return airports[index];
        }else{
            print("No airports matching that name");
            return null;
        }

    }

    private static Airport queryArrivalAirport(String message, Airport from) {
        print(message);
        String fromLocation = scanner.nextLine();

        Airport[] airports = manipulator.getArrivalLocations(from,fromLocation);
        if(airports.length > 0 ){
            print("Choose an airport");
            for (int i = 0; i < airports.length; i++) {
                print((i+1)+". "+ airports[i].toString());
            }
            int index = validatedInput(Integer.class, String.format("Select airport (%d-%d)",1,airports.length),CheckType.Range,1,airports.length) - 1;
            return airports[index];
        }else{
            print("No route available with that departure and arrival location");
            return null;
        }
    }

    private static void startBooking() {
        Passenger passenger = queryForPassenger("Enter passenger name or ID to start booking");
        if(passenger == null){
            print("Booking cancelled, passenger does not exist");
        } else {

            Airport from = queryDepartureAirport("Where is the passenger flying from?", false);
            if (from == null) {
                print("Cancelling booking");
                return;
            }
            Airport to = queryArrivalAirport("Where is the passenger flying to?", from);
            if (to == null) {
                print("Cancelling booking");
                return;
            }
            String dateConfirm = dateFromInput("What date is the passenger travelling on? (yyyy-mm-dd) (Enter to show all flights)");
            Flight[] flights = null;
            if (dateConfirm.equals("")) {
                flights = manipulator.getFlightsFromRoute(from, to);
            } else {
                flights = manipulator.getFlightsFromRouteAndDate(from, to, dateConfirm);
            }
            print("" + flights.length + " Flights found");
            for (int i = 0; i < flights.length; i++) {
                print(i + 1 + ". " + from.getName() + "->" + to.getName() + " at " + flights[i].toString());
            }
            int flightIndex = validatedInput(Integer.class, "Which flight would the passenger like to book?", CheckType.Range, 1, flights.length) - 1;
            Flight selectedFlight = flights[flightIndex];
            print("Prices");
            print("1. Economy: €" + selectedFlight.getPriceEconomy());
            print("2. Business: €" + selectedFlight.getPriceBusiness());
            print("3. First class: €" + selectedFlight.getPriceFirstClass());
            print("4. Cancel booking");
            int selectedFlightClass = validatedInput(Integer.class, "What class is the passenger booking?", CheckType.Range, 1, 4);
            Booking.BookingType type = null;
            switch (selectedFlightClass) {
                case 1:
                    type = Booking.BookingType.Economy;
                    print("Price of the flight: " + selectedFlight.getPriceEconomy());
                    break;
                case 2:
                    type = Booking.BookingType.Business;
                    print("Price of the flight: " + selectedFlight.getPriceBusiness());
                    break;
                case 3:
                    type = Booking.BookingType.First;
                    print("Price of the flight: " + selectedFlight.getPriceFirstClass());
                    break;
                case 4:
                    print("Booking cancelled");
                    return;
            }

            String passportNumber = validatedInput(String.class, "Please enter the passengers passport number.", CheckType.MinLength, 8);
            //This gets thrown away, didn't feel the need to actually implement it
            print("Please enter the passengers credit card number and expiry date");
            scanner.nextLine();
            print("Authorizing card...");
            try {
                Thread.sleep(1000);
            } catch (Exception e){
                e.printStackTrace();
            }
            print("Card successfully authorized");
            print("Creating booking...");
            Booking booking = new Booking(0,selectedFlight.getFlightId(),type.toString(),passportNumber,passenger.getPassengerID());
            boolean madeBooking = manipulator.addBooking(booking);
            if(!madeBooking){
                print("No seats left available for that class, cancelling booking");
            }else{
                print("Booking created.");
            }
        }
    }

    private static String dateFromInput(String prompt){
        boolean createdDate = false;
        LocalDate date = null;
        print(prompt);
        String input = "";
        while(!createdDate){
            try{
                input = scanner.nextLine();
                if(input.equals("")){
                    createdDate = true;
                }else {
                    date = LocalDate.parse(input);
                    createdDate = true;
                }
            } catch (Exception e){
                print("Not a valid date format, use yyyy-mm-dd");
            }
        }
        return input;
    }

    private static String timeFromInput(String prompt){
        boolean createdTime = false;
        Time date = null;
        print(prompt);
        String input = "";
        while(!createdTime){
            try{
                input = scanner.nextLine();
                if(input.equals("")){
                    createdTime = true;
                }else {
                    date = Time.valueOf(input);
                    createdTime = true;
                }
            } catch (Exception e){
                print("Not a valid time format, use HH:MM:SS");
            }
        }
        return input;
    }


    enum CheckType{
        None,
        Range,
        Above,
        MinLength
    }

    private static boolean inRange(int in, int min, int max){
        return in >= min && in <= max;
    }
    private static boolean inRange(float in, float min, float max){
        return in >= min && in <= max;
    }
    private static boolean above(int in, int min){
        return in > min;
    }
    private static boolean above(float in, float min){
        return in > min;
    }
    private static boolean minLength(String in, int minLength){
        return in.length() > minLength;
    }

    private static <T> T validatedInput(Class<T> type, String prompt, CheckType check, Object... params){
        boolean validInput = false;
        Object ret = null;
        String typeName = type.getName();
        while(!validInput){
            System.out.println(prompt);
            String in = scanner.nextLine();
            if(typeName.contains("Integer")){
                try {
                    ret = Integer.parseInt(in);
                    switch (check){
                        case None:
                            validInput = true;
                        case Range:
                            if (!inRange((int) ret, (int) params[0], (int) params[1])) {
                                throw new Exception();
                            }
                            validInput = true;
                        case Above:
                            if (!above((int) ret, (int) params[0])) {
                                throw new Exception();
                            }
                            validInput = true;
                    }
                }
                catch (Exception e){
                    continue;
                }
            }else if(typeName.contains("Float")){
                try {
                    ret = Float.parseFloat(in);
                    switch (check){
                        case None:
                            validInput = true;
                        case Range:
                            if (!inRange((float) ret, (float) params[0], (float) params[1])) {
                                throw new Exception();
                            }
                            validInput = true;
                        case Above:
                            int param = (int)params[0];
                            if (!above((float) ret, (float) ((int)params[0]))) {
                                throw new Exception();
                            }
                            validInput = true;
                    }

                }
                catch (Exception e){
                    continue;
                }
            }else if(typeName.contains("String")){
                try{
                    ret = in;
                    switch (check){
                        case None:
                            validInput = true;
                        case MinLength:
                            if(!minLength((String)ret,(int)params[0])){
                                throw new Exception();
                            }
                    }
                    validInput = true;
                }
                catch (Exception e){
                    continue;
                }
            }
        }
        return type.cast(ret);
    }

    private static void displayPassengerSubMenu(){
        clearConsole();
        banner("Passengers");
        System.out.println("1. Add");
        System.out.println("2. Remove");
        System.out.println("3. Get Details by Name");
        System.out.println("4. Back");
        int parsedInt = validatedInput(Integer.class, "Select an action (1-4)",CheckType.Range,1,4);
        switch(parsedInt) {
            case 1:
                addPassengerPrompt();
                break;
            case 2:
                removePassenger();
                break;
            case 3:
                getPassengerByName();
            case 4:
                break;
        }
    }

    private static void addPassengerPrompt(){
        print("Enter passengers name: ");
        String name = scanner.nextLine();
        print("Enter passenger email: ");
        String email = scanner.nextLine();
        int age = validatedInput(Integer.class, "Enter passenger age:",CheckType.Above,0);
        boolean added = manipulator.addPassenger(name,email, age);
        if(added){
            print("Passenger added");
        }else{
            print("Failed to add passenger");
        }
    }


    private static Passenger queryForPassenger(String message){
        print(message);
        String input = scanner.nextLine();
        int potentialID = -1;
        boolean usingID = false;
        try {
            potentialID = Integer.parseInt(input);
            usingID = true;
        }catch (Exception e){
            print("Not a valid ID, using name",1);
        }
        if(usingID){
            return manipulator.getPassengerByID(potentialID);
        }else{
            Passenger[] passengers = manipulator.getPassengersByName(input);
            if(passengers.length>0){
                print("Choose a passenger");
                for (int i = 0; i < passengers.length; i++) {
                    print((i+1)+". "+passengers[i].toString());
                }
                int index = validatedInput(Integer.class, String.format("Select passenger (%d-%d)",1,passengers.length),CheckType.Range,1,passengers.length) - 1;
                return passengers[index];
            }else{
                print("No passengers matching that name");
            }
        }
        return null;
    }

    private static boolean confirm(String message){

        while(true) {
            print(message + " ([y]/n)");
            String input = scanner.nextLine();
            if(input.length()==0){
                return true;
            }
            if (input.length() > 1) {
                input = input.toLowerCase().substring(0, 1);
            }
            if(input.equals("y") || input.equals("n")){
                return input.equals("y");
            }
        }

    }

    private static void removePassenger(){
        Passenger passenger = queryForPassenger("Enter passenger name or ID to remove");
        if(passenger != null) {
            boolean doRemove = confirm("Are you sure you want to remove this passenger?");
            boolean exitFlag = false;
            while(!exitFlag) {
                if (doRemove) {
                    if (manipulator.removePassengerByID(passenger.getPassengerID())) {
                        print("Removed passenger with ID: " + passenger.getPassengerID());
                        exitFlag = true;
                    } else {
                        print("Failed to remove passenger, passenger has existing bookings.");
                        boolean removeAllBookings = confirm("Would you like to cancel all this passengers existing bookings and remove them?");
                        if(removeAllBookings) {
                            manipulator.removeAllBookingsForPassenger(passenger.getPassengerID());
                        }else{
                            exitFlag = true;
                        }
                    }
                }
            }
        }
    }

    private static void getPassengerByName(){
        print("Enter passengers name: ");
        String name = scanner.nextLine();
        Passenger[] passengers = manipulator.getPassengersByName(name);
        for (Passenger p : passengers){
            p.print();
        }
    }

    public static void print(String s, int logLevel){
        if(LOG_LEVEL >= logLevel) {
            System.out.println(s);
        }
    }


    public static void print(String s){
        System.out.println(s);
    }

    private static void finishOption(){
        clearConsole();
    }

    private static void close(){
        scanner.close();
    }

    private static void banner(String s) {
        int maxSize = 42;
        //42 chars 24 text 18 dashes
        int dashes = ((42 - s.length()) / 2) - 2;
        String banner = "";
        for (int i = 0; i < dashes; i++) {
            banner+="-";
        }
        banner+=" ";
        banner+=s;
        banner += " ";
        for (int i = 0; i < dashes; i++) {
            banner+="-";
        }
        print(banner);
    }
}

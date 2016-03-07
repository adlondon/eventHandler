package com.theironyard;

import jodd.json.JsonSerializer;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Session;
import spark.Spark;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws SQLException, FileNotFoundException {
	// write your code here
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:eventHandler");
        createTables(conn);
        populateDatabase("testData.csv", conn);
        populateMyEventsTable("testAttendingData.csv", conn);
        Spark.externalStaticFileLocation("public");
        Spark.init();
        Server.createWebServer().start();

        Spark.get(
                "/get-all-events",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    ArrayList<Event> events = selectAllEvents(conn);
                    try {
                        ArrayList<Event> myEvents = selectMyEvents(conn, user);
                        if (myEvents != null) {
                            for (Event event : events) {
                                for (Event myEvent : myEvents) {
                                    if (event.getId() == myEvent.getId()) {
                                        event.setGoing(true);
                                    }
                                }
                            }
                        }
                    }
                    catch (NullPointerException e) {
                        logger.error("user not logged in");
                        Spark.halt(400, "user not logged in");
                    }
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(events);
                })
        );
        Spark.post(
                "/add-event",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    if (user == null) {

                        Spark.halt(400, "user not logged in");
                    }
                    String category = request.queryParams("category");
                    String dateStr = request.queryParams("date");
                    String location = request.queryParams("location");
                    String title = request.queryParams("title");

                    if (category.equals("void")) {
                        logger.error("category input is empty");
                        Spark.halt(400, "category input is empty");
                    }
                    if (dateStr.isEmpty()) {
                        logger.error("date input is empty");
                        Spark.halt(400, "date input is empty");
                    }
                    if (location.isEmpty()) {
                        logger.error("location input is empty");
                        Spark.halt(400, "location input is empty");
                    }
                    if (title.isEmpty()) {
                        logger.error("title input is empty");
                        Spark.halt(400, "title input is empty");
                    }

                    LocalDate date = LocalDate.parse(dateStr);
                    Event event = new Event(1, user.getUserName(), category, date, location, title);
                    insertEvent(conn, event, user);
                    System.out.println();
                    return "";
                })
        );
        Spark.post(
                "/login",//accepts input form home page and creates a user which is stored in the user table
                ((request, response) -> {
                    String name = request.queryParams("userName");
                    if (name.isEmpty()) {
                        logger.error("user name input is empty");
                        Spark.halt(400, "user name input is empty");
                    }
                    String password = request.queryParams("password");
                    User user = selectUser(conn, name);
                    if (user == null) {//checks if the user already exists in the database/adds user to database if not
                        insertUser(conn, name, password);
                        user = selectUser(conn, name);
                    }
                    //grabs user data from database to check password entry

                    if (user.getPassword().equals(password)) {
                        Session session = request.session();
                        session.attribute("userName", name);
                        return name;
                    }
                    else {
                        return "login fail";
                    }
                })
        );
        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    return "logged out";
                })
        );
        Spark.post(
                "/update",
                ((request, response) -> {
                    try {
                        int index = Integer.valueOf(request.queryParams("id"));
                        Event event = selectEvent(conn, index);//pulls  data for the id 'index'

                        String category = request.queryParams("category");
                        String date = request.queryParams("date");
                        String location = request.queryParams("location");
                        String title = request.queryParams("title");
                        //3 checks to see if input was given, if no input the value is not changed within the local object
                        if (category != null || !category.equals("void")) {//or void check i cant test what the drop down returns until the js is written
                            event.setCategory(category);
                        }
                        if (!date.isEmpty()) {
                            event.setDate(LocalDate.parse(date));
                        }
                        if (!location.isEmpty()) {
                            event.setLocation(location);
                        }
                        if (!title.isEmpty()) {
                            event.setTitle(title);
                        }
                        updateEvent(conn, event);//takes local object and rewrites in database memory(only those fields that recieved input are changed)
                    }
                    catch (NumberFormatException e) {
                        logger.error("a non int was served as an id");
                        Spark.halt(400, "a non int was served as an id" + e.getMessage());
                    }
                    catch (SQLException e) {
                        logger.error("error updating event");
                        Spark.halt(500, "error updating event" + e.getMessage());
                    }
                    return "";
                })
        );
        Spark.post(
                "/delete",
                ((request, response) -> {
                    try {
                        int index = Integer.valueOf(request.queryParams("id"));//grabs from a hidden type input the id to be deleted
                        deleteEvent(conn, index);
                    }
                    catch (NumberFormatException e) {
                        logger.error("a non int was served as an id");
                        Spark.halt(400, "a non int was served as an id" + e.getMessage());
                    }
                    catch (SQLException e) {
                        logger.error("error deleting event");
                        Spark.halt(500, "error deleting event" + e.getMessage());
                    }
                    return "";
                })
        );
        Spark.get(
                "/get-host-events",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    if (user == null) {
                        logger.error("user not logged in");
                        Spark.halt(400, "user not logged in");
                    }
                    ArrayList<Event> events = selectAllHostEvents(conn, user);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(events);
                })
        );
//        for paging maybe later
//        Spark.get(
//                "/get-events",
//                ((request, response) -> {
//                    ArrayList<Event> events = selectEvents(conn, offset);
//                    JsonSerializer serializer = new JsonSerializer();
//                    return serializer.serialize(events);
//                })
//        );
        Spark.get(
                "/get-attending",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    if (user == null) {
                        logger.error("user not logged in");
                        Spark.halt(400, "user not logged in");
                    }
                    ArrayList<Event> events = selectMyEvents(conn, user);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(events);
                })
        );
        Spark.post(
                "/add-attending",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    if (user == null) {
                        logger.error("user not logged in");
                        Spark.halt(400, "user not logged in");
                    }
                    try {
                        int index = Integer.valueOf(request.queryParams("id"));
                        insertMyEvent(conn, index, user);
                    }
                    catch (NumberFormatException e) {
                        logger.error("a non int was served as an id");
                        Spark.halt(400, "a non int was served as an id" + e.getMessage());
                    }
                    catch (SQLException e) {
                        logger.error("error adding event");
                        Spark.halt(500, "error adding event" + e.getMessage());
                    }
                    return "";
                })
        );
        Spark.post(
                "/delete-attending",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    if (user == null) {
                        logger.error("user not logged in");
                        Spark.halt(400, "user not logged in");
                    }
                    try {
                        int index = Integer.valueOf(request.queryParams("id"));//grabs from a hidden type input the id to be deleted
                        deleteMyEvent(conn, index, user);
                    }
                    catch (NumberFormatException e) {
                        logger.error("a non int was served as an id");
                        Spark.halt(400, "a non int was served as an id" + e.getMessage());
                    }
                    catch (SQLException e) {
                        logger.error("error deleting event");
                        Spark.halt(500, "error deleting event" + e.getMessage());
                    }
                    return "";
                })
        );
    }

    static User getUserFromSession(Session session, Connection conn) throws SQLException {
        String name = session.attribute("userName");
        return selectUser(conn, name);
    }
    static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, user_name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS events (id IDENTITY, user_name VARCHAR, category VARCHAR, date VARCHAR, location VARCHAR, title VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS myEvents (id IDENTITY, attendee VARCHAR, event_id INT)");
    }
    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES(NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }
    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE user_name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, name, password);
        }
        return null;
    }
    public static void insertEvent(Connection conn, Event event, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO events VALUES(NULL, ?, ?, ?, ?, ?)");
        stmt.setString(1, user.getUserName());
        stmt.setString(2, event.getCategory());
        stmt.setString(3, event.getDate().toString());
        stmt.setString(4, event.getLocation());
        stmt.setString(5, event.getTitle());
        stmt.execute();
    }
    public static Event selectEvent(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM events WHERE events.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
            if (results.next()) {
                String userName =results.getString("user_name");
                String category = results.getString("category");
                LocalDate date = LocalDate.parse(results.getString("date"));
                String location = results.getString("location");
                String title = results.getString("title");
            return new Event(id, userName, category, date, location, title);
        }
        return null;
    }
    static void deleteEvent(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM events WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }
    static void updateEvent(Connection conn, Event event) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE events SET category = ?, date = ?, location = ?, title = ? WHERE id = ?");
        stmt.setString(1, event.getCategory());
        stmt.setString(2, event.getDate().toString());
        stmt.setString(3, event.getLocation());
        stmt.setString(4, event.getTitle());
        stmt.setInt(5, event.getId());
        stmt.execute();
    }
    public static ArrayList<Event> selectAllEvents(Connection conn) throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("SELECT * FROM events");
        while (results.next()) {
            int id = results.getInt("id");
            String userName = results.getString("user_name");
            String category = results.getString("category");
            LocalDate date = LocalDate.parse(results.getString("date"));
            String location = results.getString("location");
            String title = results.getString("title");
            Event event = new Event(id, userName, category, date, location, title);
            events.add(event);
        }
        return events;
    }
    public static ArrayList<Event> selectAllHostEvents(Connection conn, User user) throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM events WHERE user_name = ?");
        stmt.setString(1, user.getUserName());
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("id");
            String category = results.getString("category");
            LocalDate date = LocalDate.parse(results.getString("date"));
            String location = results.getString("location");
            String title = results.getString("title");
            Event event = new Event(id, user.getUserName(), category, date, location, title);
            events.add(event);
        }
        return events;
    }
    public static void insertMyEvent(Connection conn, int id, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO myEvents VALUES(NULL, ?, ?)");
        stmt.setString(1, user.getUserName());
        stmt.setInt(2, id);
        stmt.execute();
    }
    public static ArrayList<Event> selectMyEvents(Connection conn, User user) throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM events INNER JOIN myEvents ON events.id = myEvents.event_id WHERE attendee = ?");
        stmt.setString(1, user.getUserName());
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("id");
            String userName = results.getString("user_name");
            String category = results.getString("category");
            LocalDate date = LocalDate.parse(results.getString("date"));
            String location = results.getString("location");
            String title = results.getString("title");
            Event event = new Event(id, userName, category, date, location, title);
            events.add(event);
        }
        return events;
    }
    static void deleteMyEvent(Connection conn, int id, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM myEvents WHERE event_id = ? AND attendee = ?");
        stmt.setInt(1, id);
        stmt.setString(2, user.getUserName());
        stmt.execute();
    }
    static void populateDatabase(String fileName, Connection conn) throws FileNotFoundException, SQLException {
        insertUser(conn, "bob", "");
        insertUser(conn, "bill", "");
        insertUser(conn, "steve", "");

        File f = new File(fileName);
        Scanner fileScanner = new Scanner(f);
        fileScanner.nextLine();
        while (fileScanner.hasNext()) {
            String[] columns = fileScanner.nextLine().split(",");
            Event event = new Event(Integer.valueOf(columns[0]), columns[1], columns[2], LocalDate.parse(columns[3]), columns[4], columns[5]);
            insertTestData(conn, event);
        }
    }
    static void populateMyEventsTable(String fileName, Connection conn) throws FileNotFoundException, SQLException {
        File f = new File(fileName);
        Scanner fileScanner = new Scanner(f);
        fileScanner.nextLine();
        while (fileScanner.hasNext()) {
            String[] columns = fileScanner.nextLine().split(",");
            insertTestMyEvent(conn, columns[1], Integer.valueOf(columns[2]));
        }
    }
    static void insertTestMyEvent(Connection conn, String name, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO myEvents VALUES(NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setInt(2, id);
        stmt.execute();
    }
    static void insertTestData(Connection conn, Event event) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO events VALUES(NULL, ?, ?, ?, ?, ?)");
        stmt.setString(1, event.getUserName());
        stmt.setString(2, event.getCategory());
        stmt.setString(3, event.getDate().toString());
        stmt.setString(4, event.getLocation());
        stmt.setString(5, event.getTitle());
        stmt.execute();
    }
}

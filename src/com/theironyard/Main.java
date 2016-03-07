package com.theironyard;

import jodd.json.JsonSerializer;
import org.h2.tools.Server;
import spark.Session;
import spark.Spark;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws SQLException {
	// write your code here
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:eventHandler");
        createTables(conn);
        Spark.externalStaticFileLocation("public");
        Spark.init();
        Server.createWebServer().start();

        Spark.get(
                "/get-all-events",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    ArrayList<Event> events = selectAllEvents(conn);

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

                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(events);
                })
        );
        Spark.post(
                "/add-event",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    String category = request.queryParams("category");
                    LocalDate date = LocalDate.parse(request.queryParams("date"));
                    String location = request.queryParams("location");
                    String title = request.queryParams("title");
                    Event event = new Event(1, user.userName, category, date, location, title);
                    insertEvent(conn, event, user);
                    System.out.println();
                    return "";
                })
        );
        Spark.post(
                "/login",//accepts input form home page and creates a user which is stored in the user table
                ((request, response) -> {
                    String name = request.queryParams("userName");
                    String password = request.queryParams("password");
                    User user = selectUser(conn, name);
                    if (user == null) {//checks if the user already exists in the database/adds user to database if not
                        insertUser(conn, name, password);
                        user = selectUser(conn, name);
                    }
                    //grabs user data from database to check password entry

                    if (user.password.equals(password)) {
                        Session session = request.session();
                        session.attribute("userName", name);
                        return user.userName;
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
                    int index = Integer.valueOf(request.queryParams("id"));
                    Event event = selectEvent(conn, index);//pulls  data for the id 'index'
                    String category = request.queryParams("category");
                    String date = request.queryParams("date");
                    String location = request.queryParams("location");
                    String title = request.queryParams("title");
                    //3 checks to see if input was given, if no input the value is not changed within the local object
                    if (!category.isEmpty()) {
                        event.setCategory(category);
                    }
                    if (!date.isEmpty()) {
                        event.date = LocalDate.parse(date);
                    }
                    if (!location.isEmpty()) {
                        event.setLocation(location);
                    }
                    if (!title.isEmpty()) {
                        event.setTitle(title);
                    }
                    updateEvent(conn, event);//takes local object and rewrites in database memory(only those fields that recieved input are changed)
                    return "";
                })
        );
        Spark.post(
                "/delete",
                ((request, response) -> {
                    int index = Integer.valueOf(request.queryParams("id"));//grabs from a hidden type input the id to be deleted
                    deleteEvent(conn, index);
                    return "";
                })
        );
        Spark.get(
                "/get-host-events",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
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
                    ArrayList<Event> events = selectMyEvents(conn, user);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(events);
                })
        );
        Spark.post(
                "/add-attending",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    String category = request.queryParams("category");
                    LocalDate date = LocalDate.parse(request.queryParams("date"));
                    String location = request.queryParams("location");
                    String title = request.queryParams("title");
                    Event event = new Event(1, user.userName, category, date, location, title);
                    insertMyEvent(conn, event, user);
                    return "";
                })
        );
        Spark.post(
                "/add-attending",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    String category = request.queryParams("category");
                    LocalDate date = LocalDate.parse(request.queryParams("date"));
                    String location = request.queryParams("location");
                    String title = request.queryParams("title");
                    Event event = new Event(1, user.userName, category, date, location, title);
                    insertMyEvent(conn, event, user);
                    return "";
                })
        );
        Spark.post(
                "/delete-attending",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    int index = Integer.valueOf(request.queryParams("id"));//grabs from a hidden type input the id to be deleted
                    deleteMyEvent(conn, index, user);
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
        stmt.execute("CREATE TABLE IF NOT EXISTS events (id IDENTITY, user_name VARCHAR, category VARCHAR, date VARCHAR, location VARCHAR, title VARCHAR UNIQUE)");
        stmt.execute("CREATE TABLE IF NOT EXISTS myEvents (id IDENTITY, attendee VARCHAR, event_id INT)");
    }
    //selectEvents
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
        stmt.setString(1, user.userName);
        stmt.setString(2, event.category);
        stmt.setString(3, event.date.toString());
        stmt.setString(4, event.location);
        stmt.setString(5, event.title);
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
        stmt.setString(1, event.category);
        stmt.setString(2, event.date.toString());
        stmt.setString(3, event.location);
        stmt.setString(4, event.title);
        stmt.setInt(5, event.id);
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
        stmt.setString(1, user.userName);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("id");
            String category = results.getString("category");
            LocalDate date = LocalDate.parse(results.getString("date"));
            String location = results.getString("location");
            String title = results.getString("title");
            Event event = new Event(id, user.userName, category, date, location, title);
            events.add(event);
        }
        return events;
    }

    public static void insertMyEvent(Connection conn, Event event, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO myEvents VALUES(NULL, ?, ?)");
        stmt.setString(1, user.userName);
        stmt.setInt(2, event.id);
        stmt.execute();
    }
    public static ArrayList<Event> selectMyEvents(Connection conn, User user) throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM events INNER JOIN myEvents ON events.id = myEvents.event_id WHERE attendee = ?");
        stmt.setString(1, user.userName);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("id");
            String category = results.getString("category");
            LocalDate date = LocalDate.parse(results.getString("date"));
            String location = results.getString("location");
            String title = results.getString("title");
            Event event = new Event(id, user.userName, category, date, location, title);
            events.add(event);
        }
        return events;
    }
    static void deleteMyEvent(Connection conn, int id, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM myEvents WHERE event_id = ? AND attendee = ?");
        stmt.setInt(1, id);
        stmt.setString(2, user.userName);
        stmt.execute();
    }
}

package com.theironyard;

import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by alexanderhughes on 3/4/16.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        Main.createTables(conn);
        return conn;
    }

    void endConnection(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE users");
        stmt.execute("DROP TABLE events");
        stmt.execute("DROP TABLE myEvents");
        conn.close();
    }

    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        endConnection(conn);
        assertTrue(user != null);
    }
    @Test
    public void testEvent() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        LocalDate date = LocalDate.now();
        date.now();
        Main.insertEvent(conn, new Event(1, user.getUserName(), "doople", date, "dooplia", "doopleparty"), user);
        Event event = Main.selectEvent(conn, 1);
        assertTrue(event != null);
        endConnection(conn);
    }
    @Test
    public void testDelete() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        LocalDate date = LocalDate.now();
        Main.insertEvent(conn, new Event(1, user.getUserName(), "doople", date, "dooplia", "doopleparty"), user);
        Main.deleteEvent(conn, 1);
        Event event = Main.selectEvent(conn, 1);
        endConnection(conn);
        assertTrue(event == null);
    }
    @Test
    public void testUpdate() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        LocalDate date = LocalDate.now();
        Main.insertEvent(conn, new Event(1, user.getUserName(), "doople", date, "dooplia", "doopleparty"), user);
        Event event = Main.selectEvent(conn, 1);
        event.setLocation("dooplia 2");
        Main.updateEvent(conn, event);
        endConnection(conn);
        assertTrue(event.getLocation().equals("dooplia 2"));
    }
    @Test
    public void testSelectEvents() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        LocalDate date = LocalDate.now();
        Main.insertEvent(conn, new Event(1, user.getUserName(), "doople", date, "dooplia", "dooplepary"), user);
        Main.insertEvent(conn, new Event(1, user.getUserName(), "doople", date, "dooplia", "dooplepaty"), user);
        Main.insertEvent(conn, new Event(1, user.getUserName(), "doople", date, "dooplia", "doopleprty"), user);
        Main.insertEvent(conn, new Event(1, user.getUserName(), "doople", date, "dooplia", "dooplearty"), user);
        ArrayList<Event> events = Main.selectAllEvents(conn);
        endConnection(conn);
        assertTrue(events.size() == 4);
    }
    @Test
    public void testSelectAllHostEvents() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        LocalDate date = LocalDate.now();
        Main.insertEvent(conn, new Event(1, user.getUserName(), "doople", date, "dooplia", "dooplepary"), user);
        Main.insertEvent(conn, new Event(1, user.getUserName(), "doople", date, "dooplia", "dooplepaty"), user);
        Main.insertEvent(conn, new Event(1, user.getUserName(), "doople", date, "dooplia", "doopleprty"), user);
        Main.insertEvent(conn, new Event(1, user.getUserName(), "doople", date, "dooplia", "dooplearty"), user);
        ArrayList<Event> events = Main.selectAllHostEvents(conn, user);
        endConnection(conn);
        assertTrue(events.size() == 4);
    }
//    @Test
//    public void testSelectMyEvents() throws SQLException {
//        Connection conn = startConnection();
//        Main.insertUser(conn, "Alice", "");
//        User user = Main.selectUser(conn, "Alice");
//        LocalDate date = LocalDate.now();
//        Main.insertMyEvent(conn, 1, user);
//        Main.insertMyEvent(conn, 2, user);
//        Main.insertMyEvent(conn, 3, user);
//        Main.insertMyEvent(conn, 4, user);
//        ArrayList<Event> events = Main.selectMyEvents(conn, user);
//        endConnection(conn);
//        assertTrue(events.size() == 4);
//    }

    @Test
    public void testMyDelete() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        LocalDate date = LocalDate.now();
        Main.insertMyEvent(conn, 1, user);
        Main.deleteMyEvent(conn, 1, user);
        Event event = Main.selectEvent(conn, 1);
        endConnection(conn);
        assertTrue(event == null);
    }
}
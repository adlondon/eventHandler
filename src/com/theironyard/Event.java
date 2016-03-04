package com.theironyard;

import java.time.LocalDate;

/**
 * Created by alexanderhughes on 3/3/16.
 */
public class Event {
    int id;
    String category;
    LocalDate date;
    String location;
    String host;
    String title;
    String attendee;

    public Event(int id, String category, String location, LocalDate date, String host, String title, String attendee) {
        this.id = id;
        this.category = category;
        this.date = date;
        this.location = location;
        this.host = host;
        this.title = title;
        this.attendee = attendee;
    }

    public Event(int id, String host, String category, LocalDate date, String location, String title) {
        this.category = category;
        this.date = date;
        this.id = id;
        this.location = location;
        this.host = host;
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

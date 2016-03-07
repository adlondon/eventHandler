package com.theironyard;

import java.time.LocalDate;

/**
 * Created by alexanderhughes on 3/3/16.
 */
public class Event {
    private int id;
    private String category;
    private LocalDate date;
    private String location;
    private String userName;
    private String title;
    private boolean isGoing;

    public boolean isGoing() {
        return isGoing;
    }

    public void setGoing(boolean going) {
        isGoing = going;
    }

    public Event(int id, String userName, String category, LocalDate date, String location, String title) {
        this.id = id;
        this.category = category;
        this.date = date;
        this.location = location;
        this.title = title;
        this.userName = userName;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

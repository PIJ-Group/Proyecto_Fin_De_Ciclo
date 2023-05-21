package com.example.reminder.model;

import java.io.Serializable;

public class Event implements Serializable {

    //Class attributes
    String eventId, userId, userMail, currentDate, title, description, eventDate, eventHour, status;

    public Event() {
    }

    public Event(String eventId, String userId, String userMail, String currentDate, String title, String description, String eventDate, String eventHour, String status) {
        this.eventId = eventId;
        this.userId = userId;
        this.userMail= userMail;
        this.currentDate = currentDate;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.eventHour = eventHour;
        this.status = status;
    }



    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserMail() { return userMail; }

    public void setUserMail(String userMail) {  this.userMail = userMail; }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventHour() {
        return eventHour;
    }

    public void setEventHour(String eventHour) {
        this.eventHour = eventHour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


package com.example.reminder;

//Class for save specifics data to use them in our item list.
public class DataModal {

    private String title;
    private final String eventHour;

    public DataModal(String title, String eventHour) {
        this.eventHour = eventHour;
        this.title = title;
    }

    public String getEventHour() {
        return eventHour;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}

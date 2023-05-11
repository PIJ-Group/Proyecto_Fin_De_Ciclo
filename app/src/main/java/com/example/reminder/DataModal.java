package com.example.reminder;

public class DataModal {

    private String noteHour, title;

    public DataModal() {

    }

    public DataModal(String title, String noteHour) {
        this.noteHour = noteHour;
        this.title = title;
    }

    public String getNoteHour() {
        return noteHour;
    }

    public void setNoteHour(String noteHour) {
        this.noteHour = noteHour;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}

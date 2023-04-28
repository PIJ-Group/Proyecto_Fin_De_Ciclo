package com.example.reminder;

public class Note {

    //Class attributes
    String noteId, userId, userMail, currentDate, title, description, noteDate, noteHour, status;

    public Note() {
    }

    public Note(String noteId, String userId, String userMail, String currentDate, String title, String description, String noteDate, String noteHour, String status) {
        this.noteId = noteId;
        this.userId = userId;
        this.userMail = userMail;
        this.currentDate = currentDate;
        this.title = title;
        this.description = description;
        this.noteDate = noteDate;
        this.noteHour = noteHour;
        this.status = status;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

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

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getNoteHour() {
        return noteHour;
    }

    public void setNoteHour(String noteHour) {
        this.noteHour = noteHour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


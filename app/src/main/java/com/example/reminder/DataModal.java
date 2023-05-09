package com.example.reminder;

public class DataModal {

    private String currentDate, description, noteDate, noteHour, noteId, status, title, userId, userMail;

    public DataModal() {

    }

    public DataModal(String noteHour, String title) {
        this.noteHour = noteHour;
        this.title = title;
    }

    public DataModal(String currentDate, String description, String noteDate, String noteHour, String noteId, String status, String title, String userId, String userMail) {
        this.currentDate = currentDate;
        this.description = description;
        this.noteDate = noteDate;
        this.noteHour = noteHour;
        this.noteId = noteId;
        this.status = status;
        this.title = title;
        this.userId = userId;
        this.userMail = userMail;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
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

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}

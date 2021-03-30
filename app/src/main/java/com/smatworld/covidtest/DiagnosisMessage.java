package com.smatworld.covidtest;

import androidx.annotation.Nullable;

class DiagnosisMessage {
    private static final String TAG = "AppInfo";
    private String message;
    private String timeStamp;
    private String username;
    private String question;
    private int imageResource;
    private int userID;
    private int checkboxResID;
    private int messageID;
    static int COUNT = 0;

    // constructor for radio button messages sent by the chat system
    DiagnosisMessage(int messageID, String timeStamp, String username, String question, int imageResource, int userID) {
        this.timeStamp = timeStamp;
        this.username = username;
        this.question = question;
        this.imageResource = imageResource;
        this.userID = userID;
        this.messageID = messageID;
    }

    // constructor for Checkboxes Question options sent by the chat system
    DiagnosisMessage(int messageID, String timeStamp, String username, String question, int imageResource, int userID, int checkboxResID) {
        this.timeStamp = timeStamp;
        this.username = username;
        this.question = question;
        this.imageResource = imageResource;
        this.userID = userID;
        this.checkboxResID = checkboxResID;
        this.messageID = messageID;
    }

    // constructor for normal messages sent by the chat system
    DiagnosisMessage(int messageID, int userID, String timeStamp, String username, String message, int imageResource) {
        this.timeStamp = timeStamp;
        this.username = username;
        this.message = message;
        this.imageResource = imageResource;
        this.userID = userID;
        this.messageID = messageID;
    }

    // constructor for sent message from user
    DiagnosisMessage(int messageID, String message, String timeStamp, int userID) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.userID = userID;
        this.messageID = messageID;
    }

    String getMessage() {
        return message;
    }

    String getTimeStamp() {
        return timeStamp;
    }

    String getUsername() {
        return username;
    }

    int getImageResource() {
        return imageResource;
    }

    int getUserID() {
        return userID;
    }

    String getQuestion() {
        return question;
    }

    int getCheckboxResID() {
        return checkboxResID;
    }

    int getMessageID() {
        return messageID;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof DiagnosisMessage))
            return false;
        DiagnosisMessage diagnosisMessage = (DiagnosisMessage) object;
        return diagnosisMessage.timeStamp.equals(timeStamp)
                && diagnosisMessage.messageID == messageID
                && diagnosisMessage.message.equals(message)
                && diagnosisMessage.userID == userID;
    }
}

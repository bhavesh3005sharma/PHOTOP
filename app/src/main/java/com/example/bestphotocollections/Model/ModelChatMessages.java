package com.example.bestphotocollections.Model;

public class ModelChatMessages {
    public boolean isMyMessage() {
        return myMessage;
    }

    public ModelChatMessages() {
    }

    public ModelChatMessages(boolean myMessage, boolean isDelivered, String messages) {
        this.myMessage = myMessage;
        this.isDelivered = isDelivered;
        this.messages = messages;
    }

    public void setMyMessage(boolean myMessage) {
        this.myMessage = myMessage;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    boolean myMessage = false;
    boolean isDelivered = false;

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    String messages,profileUri,name,time;
}

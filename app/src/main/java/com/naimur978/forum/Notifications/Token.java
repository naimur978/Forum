package com.naimur978.forum.Notifications;

//utilize gcm and retrofit to have the advantage of notification, first
//check firebase cloud messaging
public class Token {
    String token;

    public Token(String token) {
        this.token = token;
    }

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

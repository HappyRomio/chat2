package com.tele2test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Message {

    @Id
    @GeneratedValue
    @Column(name = "Message_Id", nullable = false)
    private Long messageId;
    @Column(name = "Time", length = 36)
    String time;
    @Column(name = "Sender", length = 36)
    private String from;
    @Column(name = "Message_Text", length = 4000)
    private String message;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Message [from=" + from + ", message=" + message + "]";
    }

}

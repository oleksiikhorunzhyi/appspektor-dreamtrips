package com.messenger.messengerservers.entities;

import java.util.Date;
import java.util.Locale;

public class Message {
    
    private String id;
    private User from;
    private User to;
    private String text;
    private Date date;
    private Locale locale;

    public Message() {
    }

    public Message(User from, User to, String text, String id) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.id = id;
    }

    private Message(Builder builder) {
        setFrom(builder.from);
        setTo(builder.to);
        setText(builder.text);
        date = builder.date;
        locale = builder.locale;
    }


    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public static final class Builder {
        private String id;
        private User from;
        private User to;
        private String text;
        private Date date;
        private Locale locale;

        public Builder() {
        }

        public Builder id(String val){
            this.id = val;
            return this;
        }
        
        public Builder from(User val) {
            from = val;
            return this;
        }

        public Builder to(User val) {
            to = val;
            return this;
        }

        public Builder text(String val) {
            text = val;
            return this;
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public Builder locale(Locale val) {
            locale = val;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}

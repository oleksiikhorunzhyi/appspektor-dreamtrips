package com.messenger.messengerservers.xmpp.entities;

public class MessageBody {

    private String text;
    private String locale;

    public MessageBody(String text, String locale) {
        this.text = text;
        this.locale = locale;
    }

    private MessageBody(Builder builder) {
        setText(builder.text);
        setLocale(builder.locale);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }


    public static final class Builder {
        private String text;
        private String locale;

        public Builder() {
        }

        public Builder text(String val) {
            text = val;
            return this;
        }

        public Builder locale(String val) {
            locale = val;
            return this;
        }

        public MessageBody build() {
            return new MessageBody(this);
        }
    }
}

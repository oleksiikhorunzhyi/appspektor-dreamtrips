package com.messenger.messengerservers.model;

public class MessageBody {
    private String text;
    private String locale;
    private Attachment attachment;

    public MessageBody() {
    }

    public MessageBody(String text, String locale, Attachment attachment) {
        this.text = text;
        this.locale = locale;
        this.attachment = attachment;
    }

    public MessageBody(String locale) {
        this.locale = locale;
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

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }
}

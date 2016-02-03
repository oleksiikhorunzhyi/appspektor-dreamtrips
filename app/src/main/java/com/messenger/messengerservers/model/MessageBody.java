package com.messenger.messengerservers.model;

import java.util.List;

public class MessageBody {
    private String text;
    private String locale;
    private List<AttachmentHolder> attachments;

    public MessageBody() {
    }

    public MessageBody(String text, String locale, List<AttachmentHolder> attachments) {
        this.text = text;
        this.locale = locale;
        this.attachments = attachments;
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

    public List<AttachmentHolder> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentHolder> attachments) {
        this.attachments = attachments;
    }
}

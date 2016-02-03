package com.messenger.messengerservers.model;

public class AttachmentHolder {
    private String type;
    private Attachment item;

    public AttachmentHolder() {
    }

    public AttachmentHolder(String type, Attachment item) {
        this.type = type;
        this.item = item;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Attachment getItem() {
        return item;
    }

    public void setItem(Attachment item) {
        this.item = item;
    }
}

package com.messenger.messengerservers.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageBody {
    private String text;
    @SerializedName("locale")
    private String localeName;
    private List<AttachmentHolder> attachments;

    public MessageBody() {
    }

    public MessageBody(String text, String localeName, List<AttachmentHolder> attachments) {
        this.text = text;
        this.localeName = localeName;
        this.attachments = attachments;
    }

    public MessageBody(String localeName) {
        this.localeName = localeName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    public List<AttachmentHolder> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentHolder> attachments) {
        this.attachments = attachments;
    }
}

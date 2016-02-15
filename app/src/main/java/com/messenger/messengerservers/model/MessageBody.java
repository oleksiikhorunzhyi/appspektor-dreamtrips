package com.messenger.messengerservers.model;

import com.google.gson.annotations.SerializedName;
import com.messenger.entities.DataMessage;

import java.util.List;
import java.util.Locale;

public class MessageBody {
    private String text;
    @SerializedName("locale")
    private String localeName = Locale.getDefault().getDisplayName(); // we should send locale to support release/1.6.0
    private int version;
    private List<AttachmentHolder> attachments;

    public MessageBody() {
    }

    public MessageBody(List<AttachmentHolder> attachments) {
        this(null, attachments);
    }

    public MessageBody(String text) {
        this(text, null);
    }

    public MessageBody(String text, List<AttachmentHolder> attachments) {
        setVersion();
        this.text = text;
        this.attachments = attachments;
    }

    private void setVersion() {
        version = DataMessage.MESSAGE_FORMAT_VERSION;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getVersion() {
        return version;
    }

    public List<AttachmentHolder> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentHolder> attachments) {
        this.attachments = attachments;
    }
}

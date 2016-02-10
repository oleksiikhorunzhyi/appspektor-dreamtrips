package com.messenger.messengerservers.model;

import android.support.annotation.Nullable;

import com.messenger.messengerservers.constant.MessageStatus;

public class Message {
    private String id;
    private String fromId;
    private String toId;
    @Nullable
    private MessageBody messageBody;
    // ms
    private long date;
    private String conversationId;
    private int status;

    public Message() {
    }

    private Message(Builder builder) {
        setId(builder.id);
        setFromId(builder.fromId);
        setToId(builder.toId);
        setMessageBody(builder.messageBody);
        setDate(builder.date);
        setConversationId(builder.conversationId);
        setStatus(builder.status);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    @Nullable
    public MessageBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(@Nullable MessageBody messageBody) {
        this.messageBody = messageBody;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @MessageStatus.Status
    public int getStatus() {
        return status;
    }

    public void setStatus(@MessageStatus.Status int status) {
        this.status = status;
    }

    public static final class Builder {
        private String id;
        private String fromId;
        private String toId;
        private MessageBody messageBody;
        private long date;
        private String conversationId;
        private int status;

        public Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder fromId(String val) {
            fromId = val;
            return this;
        }

        public Builder toId(String val) {
            toId = val;
            return this;
        }

        public Builder messageBody(MessageBody val) {
            messageBody = val;
            return this;
        }

        public Builder date(long val) {
            date = val;
            return this;
        }

        public Builder conversationId(String val) {
            conversationId = val;
            return this;
        }

        public Builder status(@MessageStatus.Status int val) {
            status = val;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}

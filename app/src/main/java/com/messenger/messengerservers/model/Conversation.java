package com.messenger.messengerservers.model;

import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.ConversationType;

import java.util.List;

public class Conversation {
    private final String id;
    private final String subject;
    private final String avatar;
    private final String type;
    private final int unreadMessageCount;
    private final long leftTime;
    private final String status;
    private List<Participant> participants;
    private final Message lastMessage;
    public final long lastActiveDate;
    private String ownerId;

    private Conversation(Builder builder) {
        id = builder.id;
        subject = builder.subject;
        avatar = builder.avatar;
        type = builder.type;
        unreadMessageCount = builder.unreadMessageCount;
        leftTime = builder.leftTime;
        status = builder.status;
        participants = builder.participants;
        lastMessage = builder.lastMessage;
        lastActiveDate = builder.lastActiveDate;
        setOwnerId(builder.ownerId);
    }


    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getAvatar() {
        return avatar;
    }

    @ConversationType.Type
    public String getType() {
        return type;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public long getLeftTime() {
        return leftTime;
    }

    @ConversationStatus.Status
    public String getStatus() {
        return status;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public long getLastActiveDate() {
        return lastActiveDate;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", leftTime=" + leftTime +
                '}';
    }

    public static final class Builder {
        private String id;
        private String subject;
        private String avatar;
        private String type;
        private int unreadMessageCount;
        private long leftTime;
        private String status;
        private List<Participant> participants;
        private Message lastMessage;
        private long lastActiveDate;
        private String ownerId;

        public Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder subject(String val) {
            subject = val;
            return this;
        }

        public Builder avatar(String val) {
            avatar = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder unreadMessageCount(int val) {
            unreadMessageCount = val;
            return this;
        }

        public Builder status(String val) {
            status = val;
            return this;
        }

        public Builder participants(List<Participant> val) {
            participants = val;
            return this;
        }

        public Builder lastMessage(Message val) {
            lastMessage = val;
            return this;
        }

        public Builder lastActiveDate(long val) {
            lastActiveDate = val;
            return this;
        }

        public Builder leftTime(long val) {
            leftTime = val;
            return this;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "id='" + id + '\'' +
                    ", leftTime=" + leftTime +
                    ", status='" + status + '\'' +
                    '}';
        }

        public Conversation build() {
            return new Conversation(this);
        }

        public Builder ownerId(String val) {
            ownerId = val;
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Conversation that = (Conversation) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

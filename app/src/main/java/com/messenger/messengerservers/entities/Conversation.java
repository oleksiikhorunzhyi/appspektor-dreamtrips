package com.messenger.messengerservers.entities;

public class Conversation {

    String id;
    String subject;
    Type type;
    Message lastMessage;

    public Conversation(String id, String subject, Type type, Message lastMessage) {
        this.id = id;
        this.subject = subject;
        this.type = type;
        this.lastMessage = lastMessage;
    }

    public Conversation(String id, String subject, Type type) {
        this.id = id;
        this.subject = subject;
        this.type = type;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public Type getType() {
        return type;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public static enum Type{
        CHAT ("chat"),
        GROUP ("group"),
        RINK("rink"),
        RANK("rank");

        private final String name;

        private Type(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }
}

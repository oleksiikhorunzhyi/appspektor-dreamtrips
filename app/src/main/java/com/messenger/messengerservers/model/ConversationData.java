//package com.messenger.messengerservers.model;
//
//import java.util.List;
//
//@Deprecated
//public class ConversationData {
//    public final Conversation conversation;
//    public final List<Participant> participants;
//    public final Message lastMessage;
//
//    public ConversationData(Conversation conversation, List<Participant> participants, Message lastMessage) {
//        this.lastMessage = lastMessage;
//        this.conversation = conversation;
//        this.participants = participants;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        com.messenger.messengerservers.model.Conversation that = (com.messenger.messengerservers.model.Conversation) o;
//
//        return conversation != null ? conversation.equals(that.conversation) : that.conversation == null;
//
//    }
//
//    @Override
//    public int hashCode() {
//        return conversation != null ? conversation.hashCode() : 0;
//    }
//
//    @Override
//    public String toString() {
//        return "com.messenger.messengerservers.model.Conversation{" +
//                "conversation=" + conversation +
//                ", participants=" + participants +
//                ", lastMessage=" + lastMessage +
//                '}';
//    }
//}

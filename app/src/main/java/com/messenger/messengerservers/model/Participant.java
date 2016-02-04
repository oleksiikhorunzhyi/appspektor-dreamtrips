package com.messenger.messengerservers.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Participant {

    private String userId;
    private String affiliation;
    private String conversationId;

    public Participant(String userId, String affiliation, String conversationId) {
        this.userId = userId;
        this.affiliation = affiliation;
        this.conversationId = conversationId;
    }

    public Participant(Participant participant, String conversationId) {
        this(participant.getUserId(), participant.getAffiliation(), conversationId);
    }

    public String getUserId() {
        return userId;
    }

    @Affiliation.AffiliationType
    public String getAffiliation() {
        return affiliation;
    }

    public String getConversationId() {
        return conversationId;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Misc
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Participant that = (Participant) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return conversationId != null ? conversationId.equals(that.conversationId) : that.conversationId == null;

    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (conversationId != null ? conversationId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "user=" + userId +
                ", affiliation='" + affiliation + '\'' +
                ", conversationId='" + conversationId + '\'' +
                '}';
    }

    public static final class Affiliation {
        public static final String OWNER = "owner";
        public static final String MEMBER = "member";
        public static final String NONE = "none";

        @Retention(RetentionPolicy.SOURCE)
        @StringDef({OWNER, MEMBER, NONE})
        public @interface AffiliationType {
        }
    }
}

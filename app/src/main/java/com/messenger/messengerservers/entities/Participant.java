package com.messenger.messengerservers.entities;

import android.database.Cursor;
import android.support.annotation.StringDef;

import com.raizlabs.android.dbflow.sql.SqlUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Participant {

    private User user;
    private String affiliation;
    private String conversationId;

    public Participant(User user, String affiliation, String conversationId) {
        this.user = user;
        this.affiliation = affiliation;
        this.conversationId = conversationId;
    }

    public Participant(Participant participant, String conversationId) {
        this(participant.user, participant.affiliation, conversationId);
    }

    public User getUser() {
        return user;
    }

    @Affiliation.AffiliationType
    public String getAffiliation() {
        return affiliation;
    }

    public String getConversationId() {
        return conversationId;
    }

    public static Participant from(Cursor cursor){
        User user = SqlUtils.convertToModel(true, User.class, cursor);
        ParticipantsRelationship relationship = SqlUtils.convertToModel(true, ParticipantsRelationship.class, cursor);
        return new Participant(user, relationship.getAffiliation(), relationship.getConversationId());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Misc
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Participant that = (Participant) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return conversationId != null ? conversationId.equals(that.conversationId) : that.conversationId == null;

    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (conversationId != null ? conversationId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "user=" + user +
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

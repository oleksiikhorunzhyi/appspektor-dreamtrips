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

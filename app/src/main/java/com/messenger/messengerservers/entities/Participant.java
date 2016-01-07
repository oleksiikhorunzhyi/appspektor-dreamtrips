package com.messenger.messengerservers.entities;

import android.support.annotation.StringDef;

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

    public String getAffiliation() {
        return affiliation;
    }

    public String getConversationId() {
        return conversationId;
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

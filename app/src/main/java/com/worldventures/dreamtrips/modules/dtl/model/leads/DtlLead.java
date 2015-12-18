package com.worldventures.dreamtrips.modules.dtl.model.leads;

import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
public class DtlLead {

    public static final String NAME = "merchant.name";
    public static final String CITY = "merchant.city";
    public static final String CONTACT = "contact.name";
    public static final String COMMENT = "comment";
    public static final String PHONE = "contact.phone";

    private Merchant merchant;
    private Contact contact;
    private List<Rating> ratings;
    private String comment;

    private DtlLead() {
        ratings = new ArrayList<>();
    }

    public static class Builder {

        private DtlLead instance;

        public Builder() {
            instance = new DtlLead();
        }

        public Builder merchant(Merchant merchant) {
            instance.merchant = merchant;
            return this;
        }

        public Builder contact(Contact contact) {
            instance.contact = contact;
            return this;
        }

        public Builder rating(@Rating.LeadRatingType String type, int rating) {
            instance.ratings.add(new Rating(type, rating));
            return this;
        }

        public Builder comment(String comment) {
            instance.comment = comment;
            return this;
        }

        public DtlLead build() {
            return instance;
        }
    }

    public static class Merchant {

        private String id;
        private String name;
        private String city;

        public Merchant(@Nullable String id, String name, String city) {
            this.id = id;
            this.name = name;
            this.city = city;
        }
    }

    public static class Contact {

        private String name;
        private String phone;
        private String email;
        private DtlLeadContactTime time;

        public Contact(String name, String phone, String email, DtlLeadContactTime time) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.time = time;
        }
    }

    public static class Rating {

        @LeadRatingType
        private String type;
        private int rating;

        public Rating(@LeadRatingType String type, int rating) {
            this.type = type;
            this.rating = rating;
        }

        @Retention(SOURCE)
        @StringDef({FOOD, CLEANLINESS, UNIQUENESS, SERVICE})
        public @interface LeadRatingType {
        }

        public static final String FOOD = "food";
        public static final String CLEANLINESS = "cleanliness";
        public static final String UNIQUENESS = "uniqueness";
        public static final String SERVICE = "service";
    }
}

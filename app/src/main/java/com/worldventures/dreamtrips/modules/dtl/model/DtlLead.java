package com.worldventures.dreamtrips.modules.dtl.model;

import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
public class DtlLead {

    // TODO finish new error handling codes once server ready
    public static final String NAME = "name";
    public static final String CITY = "city";
    public static final String CONTACT = "contact_name";
    public static final String DESCRIPTION = "description";
    public static final String PHONE = "phone";

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
        private ContactTime time;

        public Contact(String name, String phone, ContactTime time) {
            this.name = name;
            this.phone = phone;
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

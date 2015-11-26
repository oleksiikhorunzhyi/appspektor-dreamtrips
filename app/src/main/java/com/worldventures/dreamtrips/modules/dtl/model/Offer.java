package com.worldventures.dreamtrips.modules.dtl.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class Offer {
    @OfferName
    public final String name;

    public Offer(@OfferName String name) {
        this.name = name;
    }

    @Retention(SOURCE)
    @StringDef({POINT_REWARD, PERKS})
    public @interface OfferName {
    }

    public static final String POINT_REWARD = "points";
    public static final String PERKS = "perks";
}

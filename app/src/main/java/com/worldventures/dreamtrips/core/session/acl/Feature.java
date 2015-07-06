package com.worldventures.dreamtrips.core.session.acl;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class Feature {
    @FeatureName
    public final String name;

    public Feature(@FeatureName String name) {
        this.name = name;
    }

    @Retention(SOURCE)
    @StringDef({TRIPS, REP_TOOLS})
    public @interface FeatureName {
    }

    public static final String TRIPS = "trips";
    public static final String REP_TOOLS = "rep_tools";

}

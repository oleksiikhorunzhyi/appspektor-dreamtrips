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
    @StringDef({})
    public @interface FeatureName {
    }

}

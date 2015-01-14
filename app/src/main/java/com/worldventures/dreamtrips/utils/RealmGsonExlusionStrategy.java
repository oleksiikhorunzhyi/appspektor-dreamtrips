package com.worldventures.dreamtrips.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import io.realm.Realm;
import io.realm.internal.Row;

public class RealmGsonExlusionStrategy implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return (f.getDeclaringClass() == Realm.class || f.getDeclaringClass() == Row.class);
    }

}
package com.worldventures.dreamtrips.modules.friends.model;

import java.io.Serializable;

public class Circle implements Serializable {

    public static Circle all(String title) {
        Circle all = new Circle();
        all.name = title;
        return all;
    }

    String id;
    String name;
    boolean predefined;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isPredefined() {
        return predefined;
    }

    @Override
    public String toString() {
        return name;
    }
}

package com.worldventures.dreamtrips.modules.friends.model;

import java.io.Serializable;

public class Group implements Serializable {
    String id;
    String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

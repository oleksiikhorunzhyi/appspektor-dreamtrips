package com.worldventures.dreamtrips.core.model;

import java.io.Serializable;

public class BaseEntity implements Serializable {
    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

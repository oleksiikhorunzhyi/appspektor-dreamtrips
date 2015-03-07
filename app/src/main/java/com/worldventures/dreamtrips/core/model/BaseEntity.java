package com.worldventures.dreamtrips.core.model;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;

import java.io.Serializable;

public class BaseEntity implements Serializable {
    @TaggedFieldSerializer.Tag(0)
    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

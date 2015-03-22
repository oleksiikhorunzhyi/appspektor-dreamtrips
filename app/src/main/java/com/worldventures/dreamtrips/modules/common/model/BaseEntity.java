package com.worldventures.dreamtrips.modules.common.model;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

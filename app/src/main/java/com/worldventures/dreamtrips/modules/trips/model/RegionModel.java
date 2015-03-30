package com.worldventures.dreamtrips.modules.trips.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;


@DefaultSerializer(CompatibleFieldSerializer.class)
public class RegionModel extends BaseEntity {

    private String name;
    private transient boolean checked = true;
    private transient boolean shouldBeGone = true;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShouldBeGone() {
        return shouldBeGone;
    }

    public void setShouldBeGone(boolean shouldBeGone) {
        this.shouldBeGone = shouldBeGone;
    }
}

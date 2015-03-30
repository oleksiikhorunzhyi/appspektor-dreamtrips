package com.worldventures.dreamtrips.modules.trips.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class ActivityModel extends BaseEntity {

    int parent_id;
    int position;
    String icon;
    String name;

    private transient boolean isChecked = true;
    private transient boolean shouldBeGone = true;

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isShouldBeGone() {
        return shouldBeGone;
    }

    public void setShouldBeGone(boolean shouldBeGone) {
        this.shouldBeGone = shouldBeGone;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ActivityModel activity = (ActivityModel) o;

        if (id != activity.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        return result;
    }
}

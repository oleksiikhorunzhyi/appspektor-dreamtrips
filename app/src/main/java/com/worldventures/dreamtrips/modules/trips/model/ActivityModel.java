package com.worldventures.dreamtrips.modules.trips.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class ActivityModel extends BaseEntity {

    @SerializedName("parent_id")
    private int parentId;
    private int position;
    private String icon;
    private String name;

    private transient boolean checked = true;
    private transient boolean shouldBeGone = true;

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
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
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isShouldBeGone() {
        return shouldBeGone;
    }

    public void setShouldBeGone(boolean shouldBeGone) {
        this.shouldBeGone = shouldBeGone;
    }
}

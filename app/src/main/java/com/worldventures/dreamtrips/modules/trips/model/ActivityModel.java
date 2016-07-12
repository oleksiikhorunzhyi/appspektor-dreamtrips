package com.worldventures.dreamtrips.modules.trips.model;

import android.os.Parcel;

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

    public ActivityModel() {
        super();
    }

    private boolean checked = true;

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

    public boolean isParent() {
        return parentId == 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.parentId);
        dest.writeInt(this.position);
        dest.writeString(this.icon);
        dest.writeString(this.name);
    }

    public ActivityModel(Parcel in) {
        super(in);
        this.parentId = in.readInt();
        this.position = in.readInt();
        this.icon = in.readString();
        this.name = in.readString();
    }

    public static final Creator<ActivityModel> CREATOR = new Creator<ActivityModel>() {
        @Override
        public ActivityModel createFromParcel(Parcel in) {
            return new ActivityModel(in);
        }

        @Override
        public ActivityModel[] newArray(int size) {
            return new ActivityModel[size];
        }
    };
}

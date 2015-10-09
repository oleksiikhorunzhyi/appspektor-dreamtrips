package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlPlace implements Parcelable {

    private int id;
    private String name;
    private String state;
    private String address1;
    private String address2;
    private String description;
    private DtlPlaceType type;
    private String website;
    private String cityName;
    private Location location;
    private String phone;
    private String zip;
    private int avgPrice;
    private List<DtlPlaceCategory> categories;
    private List<DtlPlaceMedia> mediaList;

    public DtlPlace() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DtlPlaceType getType() {
        return type;
    }

    public void setType(DtlPlaceType type) {
        this.type = type;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public int getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(int avgPrice) {
        this.avgPrice = avgPrice;
    }

    public List<DtlPlaceCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<DtlPlaceCategory> categories) {
        this.categories = categories;
    }

    public List<DtlPlaceMedia> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<DtlPlaceMedia> mediaList) {
        this.mediaList = mediaList;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlPlace(Parcel in) {
        id = in.readInt();
        name = in.readString();
        state = in.readString();
        address1 = in.readString();
        address2 = in.readString();
        description = in.readString();
        type = (DtlPlaceType) in.readSerializable();
        website = in.readString();
        cityName = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        phone = in.readString();
        zip = in.readString();
        avgPrice = in.readInt();
        categories = in.createTypedArrayList(DtlPlaceCategory.CREATOR);
        mediaList = in.createTypedArrayList(DtlPlaceMedia.CREATOR);
    }

    public static final Creator<DtlPlace> CREATOR = new Creator<DtlPlace>() {
        @Override
        public DtlPlace createFromParcel(Parcel in) {
            return new DtlPlace(in);
        }

        @Override
        public DtlPlace[] newArray(int size) {
            return new DtlPlace[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(state);
        dest.writeString(address1);
        dest.writeString(address2);
        dest.writeString(description);
        dest.writeSerializable(type);
        dest.writeString(website);
        dest.writeString(cityName);
        dest.writeParcelable(location, flags);
        dest.writeString(phone);
        dest.writeString(zip);
        dest.writeInt(avgPrice);
        dest.writeTypedList(categories);
        dest.writeTypedList(mediaList);
    }
}

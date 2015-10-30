package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.core.utils.LocationHelper;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlPlace implements Parcelable {

    String merchantId;
    String merchantType;
    DtlPlaceType partnerStatus;
    String legalName;
    String displayName;
    String address1;
    String address2;
    String city;
    String state;
    String country;
    String zip;
    Location coordinates;
    String phone;
    String email;
    String description;
    String website;
    int budget;
    float rating;
    List<DtlPlaceAttribute> attributes;
    List<DtlPlaceMedia> images;

    public DtlPlace() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getMerchantType() {
        return merchantType;
    }

    public String getLegalName() {
        return legalName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getZip() {
        return zip;
    }

    public Location getCoordinates() {
        return coordinates;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public int getBudget() {
        return budget;
    }

    public float getRating() {
        return rating;
    }

    public List<DtlPlaceAttribute> getAttributes() {
        return attributes;
    }

    public List<DtlPlaceMedia> getImages() {
        return images;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlPlace(Parcel in) {
        merchantId = in.readString();
        merchantType = in.readString();
        legalName = in.readString();
        displayName = in.readString();
        address1 = in.readString();
        address2 = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        zip = in.readString();
        coordinates = in.readParcelable(Location.class.getClassLoader());
        phone = in.readString();
        email = in.readString();
        description = in.readString();
        website = in.readString();
        budget = in.readInt();
        rating = in.readFloat();
        attributes = in.createTypedArrayList(DtlPlaceAttribute.CREATOR);
        images = in.createTypedArrayList(DtlPlaceMedia.CREATOR);
        partnerStatus = (DtlPlaceType) in.readSerializable();
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
        dest.writeString(merchantId);
        dest.writeString(merchantType);
        dest.writeString(legalName);
        dest.writeString(displayName);
        dest.writeString(address1);
        dest.writeString(address2);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(zip);
        dest.writeParcelable(coordinates, flags);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(description);
        dest.writeString(website);
        dest.writeInt(budget);
        dest.writeFloat(rating);
        dest.writeTypedList(attributes);
        dest.writeTypedList(images);
        dest.writeSerializable(partnerStatus);
    }

    public DtlPlaceType getPartnerStatus() {
        return partnerStatus;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Filtering part
    ///////////////////////////////////////////////////////////////////////////

    public boolean applyFilter(DtlFilterData filterObject, LatLng currentLocation) {
        return checkPrice(filterObject.getMinPrice(), filterObject.getMaxPrice())
                && (!filterObject.isDistanceEnabled() || LocationHelper.checkLocation(filterObject.getMaxDistance(), currentLocation,
                new LatLng(coordinates.getLat(), coordinates.getLng())));
    }

    private boolean checkPrice(int minPrice, int maxPrice) {
        return budget >= minPrice && budget <= maxPrice;
    }

}

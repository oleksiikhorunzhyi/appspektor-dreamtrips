package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.LocationHelper;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlPlace implements Parcelable {

    public static final String AMENITIES = "amenities";
    public static final String CATEGORIES = "categories";

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
    String perksDescription;
    String website;
    int budget;
    float rating;
    @SerializedName("attribute_sets")
    List<DtlPlaceAttributeSet> attributeSets;
    List<DtlPlaceMedia> images;
    List<OperationDay> operationDays;

    private transient Map<String, List<DtlPlacesFilterAttribute>> attributeMap;

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

    public List<DtlPlaceAttributeSet> getAttributesSet() {
        return attributeSets;
    }

    public Map<String, List<DtlPlacesFilterAttribute>> getAttributesAsMap() {
        if (attributeMap != null) return attributeMap;
        attributeMap = new HashMap<>();
        if (attributeSets != null)
            Queryable.from(attributeSets).forEachR(attribute ->
                    attributeMap.put(attribute.getName(), attribute.getFilterAttributes()));
        return attributeMap;
    }

    public List<DtlPlaceMedia> getImages() {
        return images;
    }

    public List<OperationDay> getOperationDays() {
        return operationDays;
    }

    public DtlPlaceType getPartnerStatus() {
        return partnerStatus;
    }

    public String getPerksDescription() {
        return perksDescription;
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
        perksDescription = in.readString();
        website = in.readString();
        budget = in.readInt();
        rating = in.readFloat();
        attributeSets = in.createTypedArrayList(DtlPlaceAttributeSet.CREATOR);
        images = in.createTypedArrayList(DtlPlaceMedia.CREATOR);
        partnerStatus = (DtlPlaceType) in.readSerializable();
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
        dest.writeString(perksDescription);
        dest.writeString(website);
        dest.writeInt(budget);
        dest.writeFloat(rating);
        dest.writeTypedList(attributeSets);
        dest.writeTypedList(images);
        dest.writeSerializable(partnerStatus);
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

    ///////////////////////////////////////////////////////////////////////////
    // Filtering part
    ///////////////////////////////////////////////////////////////////////////

    public boolean containsQuery(String query) {
        List<DtlPlacesFilterAttribute> categories = getAttributesAsMap().get(CATEGORIES);

        return displayName.toLowerCase().contains(query.toLowerCase()) || (categories != null &&
                Queryable.from(categories).firstOrDefault(element ->
                        element.getAttributeName().toLowerCase().contains(query.toLowerCase())) != null);
    }

    public boolean applyFilter(DtlFilterData filterData, LatLng currentLocation) {
        return checkPrice(filterData.getMinPrice(), filterData.getMaxPrice())
                && checkDistance(filterData, currentLocation);
               //TODO restore later
               // && checkAmenities(filterData)
    }

    private boolean checkPrice(int minPrice, int maxPrice) {
        return budget >= minPrice && budget <= maxPrice;
    }

    private boolean checkDistance(DtlFilterData filterData, LatLng currentLocation) {
        return !filterData.isDistanceEnabled()
                || currentLocation == null
                || LocationHelper.checkLocation(filterData.getMaxDistance(), currentLocation,
                new LatLng(coordinates.getLat(), coordinates.getLng()), filterData.getDistance());
    }

    private boolean checkAmenities(DtlFilterData filterData) {
        List<DtlPlacesFilterAttribute> selectedAmenities = filterData.getSelectedAmenities();
        if (selectedAmenities == null) return true;

        List<DtlPlacesFilterAttribute> placeAmenities = getAttributesAsMap().get(AMENITIES);

        return placeAmenities == null || !Collections.disjoint(selectedAmenities, placeAmenities);
    }

}

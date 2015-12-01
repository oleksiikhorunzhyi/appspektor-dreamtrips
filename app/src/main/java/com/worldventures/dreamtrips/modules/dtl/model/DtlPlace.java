package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.LocationHelper;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unused")
@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlPlace implements Parcelable {

    String id;
    String type;
    PartnerStatus partnerStatus;
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
    String timeZone;
    List<DtlOffer> offers;
    List<DtlPlaceAttribute> categories;
    List<DtlPlaceAttribute> amenities;
    List<DtlPlaceMedia> images;
    List<OperationDay> operationDays;

    public DtlPlace() {
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
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

    public List<DtlOffer> getOffers() {
        return offers;
    }

    public List<DtlPlaceAttribute> getCategories() {
        return categories;
    }

    public List<DtlPlaceAttribute> getAmenities() {
        return amenities;
    }

    public List<DtlPlaceMedia> getImages() {
        return images;
    }

    public List<OperationDay> getOperationDays() {
        return operationDays;
    }

    public DtlPlaceType getPlaceType() {
        return hasNoOffers() ? DtlPlaceType.DINING : DtlPlaceType.OFFER;
    }

    public boolean hasOffer(DtlOffer dtlOffer) {
        return offers != null && offers.contains(dtlOffer);
    }

    public String getPerkDescription() {
        return Queryable.from(offers)
                .first(element -> element.equals(DtlOffer.TYPE_PERK)).getOffer().getDescription();
    }

    public boolean hasNoOffers() {
        return offers == null || offers.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtlPlace dtlPlace = (DtlPlace) o;

        return !(id != null ? !id.equals(dtlPlace.id) : dtlPlace.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlPlace(Parcel in) {
        id = in.readString();
        type = in.readString();
        partnerStatus = (PartnerStatus) in.readSerializable();
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
        offers = in.createTypedArrayList(DtlOffer.CREATOR);
        categories = in.createTypedArrayList(DtlPlaceAttribute.CREATOR);
        amenities = in.createTypedArrayList(DtlPlaceAttribute.CREATOR);
        images = in.createTypedArrayList(DtlPlaceMedia.CREATOR);
        operationDays = in.createTypedArrayList(OperationDay.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeSerializable(partnerStatus);
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
        dest.writeTypedList(offers);
        dest.writeTypedList(categories);
        dest.writeTypedList(amenities);
        dest.writeTypedList(images);
        dest.writeTypedList(operationDays);
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
        List<DtlPlaceAttribute> categories = getCategories();

        return displayName.toLowerCase().contains(query.toLowerCase()) || (categories != null &&
                Queryable.from(categories).firstOrDefault(element ->
                        element.getName().toLowerCase().contains(query.toLowerCase())) != null);
    }

    public boolean applyFilter(DtlFilterData filterData, LatLng currentLocation) {
        return checkPrice(filterData.getMinPrice(), filterData.getMaxPrice())
                && checkDistance(filterData, currentLocation)
                && checkAmenities(filterData);
    }

    private boolean checkPrice(int minPrice, int maxPrice) {
        return budget >= minPrice && budget <= maxPrice;
    }

    private boolean checkDistance(DtlFilterData filterData, LatLng currentLocation) {
        return filterData.getMaxDistance() == DtlFilterData.MAX_DISTANCE
                || currentLocation == null
                || LocationHelper.checkLocation(filterData.getMaxDistance(), currentLocation,
                new LatLng(coordinates.getLat(), coordinates.getLng()), filterData.getDistanceType());
    }

    private boolean checkAmenities(DtlFilterData filterData) {
        List<DtlPlacesFilterAttribute> selectedAmenities = filterData.getSelectedAmenities();
        return selectedAmenities == null || getAmenities() == null ||
                !Collections.disjoint(selectedAmenities, Queryable.from(getAmenities()).map(element ->
                                new DtlPlacesFilterAttribute(element.getName())
                ).toList());
    }

    private transient double distanceInMiles;

    public void calculateDistance(LatLng currentLocation) {
        distanceInMiles = LocationHelper.distanceInMiles(currentLocation,
                getCoordinates().asLatLng());
    }

    @Override
    public String toString() {
        return displayName + " " + distanceInMiles;
    }

    public static Comparator<DtlPlace> DISTANCE_COMPARATOR = new Comparator<DtlPlace>() {
        @Override
        public int compare(DtlPlace lhs, DtlPlace rhs) {
            return Double.valueOf(lhs.distanceInMiles - rhs.distanceInMiles).intValue();
        }
    };
}

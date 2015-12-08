package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.LocationHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unused")
@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlMerchant implements Parcelable {

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
    List<DtlMerchantAttribute> categories;
    List<DtlMerchantAttribute> amenities;
    List<DtlMerchantMedia> images;
    List<OperationDay> operationDays;

    public DtlMerchant() {
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

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public void setOffers(List<DtlOffer> offers) {
        this.offers = offers;
    }

    public List<DtlMerchantAttribute> getCategories() {
        return categories;
    }

    public void setCategories(List<DtlMerchantAttribute> categories) {
        this.categories = categories;
    }

    public List<DtlMerchantAttribute> getAmenities() {
        return amenities;
    }

    public List<DtlMerchantMedia> getImages() {
        return images;
    }

    public List<OperationDay> getOperationDays() {
        return operationDays;
    }

    public DtlMerchantType getMerchantType() {
        return hasNoOffers() ? DtlMerchantType.DINING : DtlMerchantType.OFFER;
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

        DtlMerchant DtlMerchant = (DtlMerchant) o;

        return !(id != null ? !id.equals(DtlMerchant.id) : DtlMerchant.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlMerchant(Parcel in) {
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
        categories = in.createTypedArrayList(DtlMerchantAttribute.CREATOR);
        amenities = in.createTypedArrayList(DtlMerchantAttribute.CREATOR);
        images = in.createTypedArrayList(DtlMerchantMedia.CREATOR);
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

    public static final Creator<DtlMerchant> CREATOR = new Creator<DtlMerchant>() {
        @Override
        public DtlMerchant createFromParcel(Parcel in) {
            return new DtlMerchant(in);
        }

        @Override
        public DtlMerchant[] newArray(int size) {
            return new DtlMerchant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Sorting part
    ///////////////////////////////////////////////////////////////////////////

    private transient double distanceInMiles;

    public void setDistanceInMiles(double distanceInMiles) {
        this.distanceInMiles = distanceInMiles;
    }

    public void calculateDistance(LatLng currentLocation) {
        setDistanceInMiles(LocationHelper.distanceInMiles(currentLocation,
                getCoordinates().asLatLng()));
    }

    @Override
    public String toString() {
        return displayName + " " + distanceInMiles;
    }

    public static Comparator<DtlMerchant> DISTANCE_COMPARATOR = new Comparator<DtlMerchant>() {
        @Override
        public int compare(DtlMerchant lhs, DtlMerchant rhs) {
            if (lhs.distanceInMiles == rhs.distanceInMiles) return 0;
            if (lhs.distanceInMiles > rhs.distanceInMiles) return 1;
            else return -1;
        }
    };
}

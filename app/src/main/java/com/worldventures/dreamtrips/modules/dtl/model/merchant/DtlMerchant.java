package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.DtlDisclaimer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPerkData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPointsData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import timber.log.Timber;

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
    List<DtlDisclaimer> disclaimers;

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

    public String getAnalyticsName() {
        return String.format("%s:%s:%s",
                adoptForAnalytics(getCity()),
                adoptForAnalytics(getState()),
                adoptForAnalytics(getCountry()));
    }

    private String adoptForAnalytics(String string) {
        return TextUtils.isEmpty(string) ? "-" : string;
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

    public List<DtlDisclaimer> getDisclaimers() {
        return disclaimers;
    }

    //from 1 to 5
    public void setBudget(int budget) {
        this.budget = budget;
    }

    public void setDisclaimers(List<DtlDisclaimer> disclaimers) {
        this.disclaimers = disclaimers;
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

    public void setAmenities(List<DtlMerchantAttribute> amenities) {
        this.amenities = amenities;
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
        DtlOffer<DtlOfferPerkData> dtlOffer = (DtlOffer<DtlOfferPerkData>)
                Queryable.from(offers).first(element -> element.equals(DtlOffer.TYPE_PERK));
        return dtlOffer.getOffer().getDescription();
    }

    public DtlCurrency getDefaultCurrency() {
        DtlOffer<DtlOfferPointsData> dtlOffer = (DtlOffer<DtlOfferPointsData>)
                Queryable.from(offers).first(element -> element.equals(DtlOffer.TYPE_POINTS));

        return Queryable.from(dtlOffer.getOffer().getCurrencies()).firstOrDefault(DtlCurrency::isDefault);
    }

    public boolean hasNoOffers() {
        return offers == null || offers.isEmpty();
    }

    public int getOffsetHours() {
        int offset;
        try {
            offset = Integer.valueOf(timeZone);
        } catch (NumberFormatException e) {
            Timber.e(e, "Failed to parse timezone");
            offset = 0;
        }
        return offset;
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
    // Sorting part
    ///////////////////////////////////////////////////////////////////////////

    private transient double distance;
    private transient DistanceType distanceType;

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setDistanceType(DistanceType distanceType) {
        this.distanceType = distanceType;
    }

    public double getDistance() {
        return distanceType == DistanceType.KMS ?
                DtlLocationHelper.metresToKilometers(distance) :
                DtlLocationHelper.metresToMiles(distance);
    }

    public DistanceType getDistanceType() {
        return distanceType;
    }

    @Override
    public String toString() {
        return displayName + " " + distance;
    }

    public static final Comparator<DtlMerchant> DISTANCE_COMPARATOR = new Comparator<DtlMerchant>() {
        @Override
        public int compare(DtlMerchant lhs, DtlMerchant rhs) {
            if (lhs.distance == rhs.distance) return 0;
            if (lhs.distance > rhs.distance) return 1;
            else return -1;
        }
    };

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    protected DtlMerchant(Parcel in) {
        this.id = in.readString();
        this.type = in.readString();
        int tmpPartnerStatus = in.readInt();
        this.partnerStatus = tmpPartnerStatus == -1 ? null : PartnerStatus.values()[tmpPartnerStatus];
        this.displayName = in.readString();
        this.address1 = in.readString();
        this.address2 = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.country = in.readString();
        this.zip = in.readString();
        this.coordinates = in.readParcelable(Location.class.getClassLoader());
        this.phone = in.readString();
        this.email = in.readString();
        this.description = in.readString();
        this.website = in.readString();
        this.budget = in.readInt();
        this.rating = in.readFloat();
        this.timeZone = in.readString();
        this.offers = new ArrayList<DtlOffer>();
        in.readList(this.offers, List.class.getClassLoader());
        this.categories = new ArrayList<DtlMerchantAttribute>();
        in.readList(this.categories, List.class.getClassLoader());
        this.amenities = new ArrayList<DtlMerchantAttribute>();
        in.readList(this.amenities, List.class.getClassLoader());
        this.images = in.createTypedArrayList(DtlMerchantMedia.CREATOR);
        this.operationDays = new ArrayList<OperationDay>();
        in.readList(this.operationDays, List.class.getClassLoader());
        this.disclaimers = in.createTypedArrayList(DtlDisclaimer.CREATOR);
        this.distance = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeInt(this.partnerStatus == null ? -1 : this.partnerStatus.ordinal());
        dest.writeString(this.displayName);
        dest.writeString(this.address1);
        dest.writeString(this.address2);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeString(this.country);
        dest.writeString(this.zip);
        dest.writeParcelable(this.coordinates, 0);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.description);
        dest.writeString(this.website);
        dest.writeInt(this.budget);
        dest.writeFloat(this.rating);
        dest.writeString(this.timeZone);
        dest.writeList(this.offers);
        dest.writeList(this.categories);
        dest.writeList(this.amenities);
        dest.writeTypedList(images);
        dest.writeList(this.operationDays);
        dest.writeList(this.disclaimers);
        dest.writeDouble(this.distance);
    }

    public static final Parcelable.Creator<DtlMerchant> CREATOR = new Parcelable.Creator<DtlMerchant>() {
        public DtlMerchant createFromParcel(Parcel source) {
            return new DtlMerchant(source);
        }

        public DtlMerchant[] newArray(int size) {
            return new DtlMerchant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}

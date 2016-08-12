package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import android.text.TextUtils;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.dtl.merchats.model.Merchant;
import com.worldventures.dreamtrips.api.dtl.merchats.model.MerchantType;
import com.worldventures.dreamtrips.api.dtl.merchats.model.PartnerStatus;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.OfferMapper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.DtlDisclaimer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPoints;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import timber.log.Timber;

@SuppressWarnings("unused")
@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlMerchant {

    String id;
    MerchantType type;
    PartnerStatus partnerStatus;
    String displayName;
    String address1;
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
    double rating;
    String timeZone;
    List<DtlOffer> offers;
    List<DtlMerchantAttribute> categories;
    List<DtlMerchantAttribute> amenities;
    List<DtlMerchantMedia> images;
    List<OperationDay> operationDays;
    List<DtlDisclaimer> disclaimers;

    private transient boolean expanded = false;

    public DtlMerchant() {
    }

    public DtlMerchant(Merchant merchant) {
        id = merchant.id();
        type = merchant.type();
        partnerStatus = merchant.partnerStatus();
        displayName = merchant.displayName();
        address1 = merchant.address();
        city = merchant.city();
        state = merchant.state();
        country = merchant.country();
        zip = merchant.zip();
        coordinates = new Location(merchant.coordinates().lat(), merchant.coordinates().lng());
        phone = merchant.phone();
        email = merchant.email();
        description = merchant.description();
        website = merchant.website();
        budget = merchant.budget();
        rating = merchant.rating();
        timeZone = merchant.timeZone();
        //
        offers = Queryable.from(merchant.offers())
                .map(offer -> new OfferMapper().map(offer)).toList();
        categories = Queryable.from(merchant.categories())
                .map(category -> new DtlMerchantAttribute(category.name())).toList();
        amenities = Queryable.from(merchant.amenities())
                        .map(amenity -> new DtlMerchantAttribute(amenity.name())).toList();
        images = Queryable.from(merchant.images()).map(DtlMerchantMedia::new).toList();
        operationDays = Queryable.from(merchant.operationDays()).map(OperationDay::new).toList();
        disclaimers = Queryable.from(merchant.disclaimers()).map(DtlDisclaimer::new).toList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MerchantType getType() {
        return type;
    }

    public void setType(MerchantType type) {
        this.type = type;
    }

    public PartnerStatus getPartnerStatus() {
        return partnerStatus;
    }

    public void setPartnerStatus(PartnerStatus partnerStatus) {
        this.partnerStatus = partnerStatus;
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

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public double getRating() {
        return rating;
    }

    public List<DtlOffer> getOffers() {
        return hasNoOffers() ? new ArrayList<>() : offers;
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

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void toggleExpanded() {
        expanded = !expanded;
    }

    public boolean hasOffer(DtlOffer dtlOffer) {
        return offers != null && offers.contains(dtlOffer);
    }

    public boolean hasPerks() {
        return !hasNoOffers() && Queryable.from(offers).count(DtlOffer::isPerk) > 0;
    }

    public boolean hasPoints() {
        return !hasNoOffers() && Queryable.from(offers).count(DtlOffer::isPoint) > 0;
    }

    public void sortPerks() {
        if (!hasPerks()) return;
        offers = Queryable.from(offers)
                .filter(offer -> !(TextUtils.isEmpty(offer.getTitle()) && offer.isPerk()))
                .sort(DtlOffer.END_DATE_COMPARATOR)
                .toList();
    }

    public DtlCurrency getDefaultCurrency() {
        DtlOfferPoints points = (DtlOfferPoints) Queryable.from(getOffers())
                .filter(DtlOffer::isPoint)
                .firstOrDefault();
        return points != null ?
                Queryable.from(points.getCurrencies())
                        .firstOrDefault(DtlCurrency::isDefault) : null;
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

    public static final Comparator<DtlMerchant> DISTANCE_COMPARATOR =
            new Comparator<DtlMerchant>() {
                @Override
                public int compare(DtlMerchant lhs, DtlMerchant rhs) {
                    if (lhs.distance == rhs.distance) return 0;
                    if (lhs.distance > rhs.distance) return 1;
                    else return -1;
                }
            };
}

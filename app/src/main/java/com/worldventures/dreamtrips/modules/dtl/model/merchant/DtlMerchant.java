package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPerkDescription;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPointsDescription;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unused")
@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlMerchant {

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

    //from 1 to 5
    public void setBudget(int budget) {
        this.budget = budget;
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
        DtlOffer<DtlOfferPerkDescription> dtlOffer = (DtlOffer<DtlOfferPerkDescription>)
                Queryable.from(offers).first(element -> element.equals(DtlOffer.TYPE_PERK));
        return dtlOffer.getOffer().getDescription();
    }

    public DtlCurrency getDefaultCurrency() {
        DtlOffer<DtlOfferPointsDescription> dtlOffer = (DtlOffer<DtlOfferPointsDescription>)
                Queryable.from(offers).first(element -> element.equals(DtlOffer.TYPE_POINTS));

        return Queryable.from(dtlOffer.getOffer().getCurrencies()).firstOrDefault(DtlCurrency::isDefault);
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
    // Sorting part
    ///////////////////////////////////////////////////////////////////////////

    private transient double distance;

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return displayName + " " + distance;
    }

    public static Comparator<DtlMerchant> DISTANCE_COMPARATOR = new Comparator<DtlMerchant>() {
        @Override
        public int compare(DtlMerchant lhs, DtlMerchant rhs) {
            if (lhs.distance == rhs.distance) return 0;
            if (lhs.distance > rhs.distance) return 1;
            else return -1;
        }
    };
}

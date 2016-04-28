package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

@SuppressWarnings("unused")
public class DtlOffer<T extends DtlOfferData> implements Comparable<T>{

    @Offer.OfferType
    String type;
    T offer;

    public DtlOffer() {
    }

    public DtlOffer(@Offer.OfferType String type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public T getOffer() {
        return offer;
    }

    public void setOffer(T offer) {
        this.offer = offer;
    }

    public static final DtlOffer TYPE_POINTS = new DtlOffer(Offer.POINT_REWARD);
    public static final DtlOffer TYPE_PERK = new DtlOffer(Offer.PERKS);


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtlOffer dtlOffer = (DtlOffer) o;

        return !(type != null ? !type.equals(dtlOffer.type) : dtlOffer.type != null);
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }

    @Override public int compareTo(T another) {
        return getOffer().compareTo(another);
    }
}

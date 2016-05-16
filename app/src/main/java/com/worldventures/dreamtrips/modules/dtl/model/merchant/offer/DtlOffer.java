package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import java.util.Comparator;

@SuppressWarnings("unused")
public class DtlOffer<T extends DtlOfferData> implements Comparable<T> {

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

    public boolean isPerk() {
        return type.equals(TYPE_PERK.getType());
    }

    public boolean isPointReward() {
        return type.equals(TYPE_POINTS.getType());
    }

    public static final Comparator<DtlOffer> END_DATE_COMPARATOR = new Comparator<DtlOffer>() {

        @Override
        public int compare(DtlOffer lhs, DtlOffer rhs) {
            if (lhs.getOffer().getEndDate() == null && rhs.getOffer().getEndDate() == null)
                return 0;
            if (lhs.getOffer().getEndDate() == null && rhs.getOffer().getEndDate() != null)
                return 1;
            if (rhs.getOffer().getEndDate() == null && lhs.getOffer().getEndDate() != null)
                return -1;
            if (lhs.getOffer().getEndDate().getTime() == rhs.getOffer().getEndDate().getTime())
                return 0;
            return lhs.getOffer().getEndDate().getTime() >
                    rhs.getOffer().getEndDate().getTime() ? 1 : -1;
        }
    };

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

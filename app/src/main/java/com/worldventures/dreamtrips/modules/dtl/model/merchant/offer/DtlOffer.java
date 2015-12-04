package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class DtlOffer implements Parcelable {

    @Offer.OfferType
    String type;
    DtlOfferDescription offer;

    public DtlOffer() {
    }

    public DtlOffer(@Offer.OfferType String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public DtlOfferDescription getOffer() {
        return offer;
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
///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    protected DtlOffer(Parcel in) {
        //noinspection ResourceType
        type = in.readString();
        offer = in.readParcelable(DtlOfferDescription.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeParcelable(offer, flags);
    }

    public static final Creator<DtlOffer> CREATOR = new Creator<DtlOffer>() {
        @Override
        public DtlOffer createFromParcel(Parcel in) {
            return new DtlOffer(in);
        }

        @Override
        public DtlOffer[] newArray(int size) {
            return new DtlOffer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}

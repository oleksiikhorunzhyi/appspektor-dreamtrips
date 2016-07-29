package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.os.Parcel;

import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

import java.util.ArrayList;
import java.util.List;

public class DtlMerchantDetailsState extends ViewState {

    private List<Integer> offers;
    private boolean hoursViewExpanded;

    public DtlMerchantDetailsState() {
    }

    public void setOffers(List<Integer> offers) {
        this.offers = offers;
    }

    public void setHoursViewExpanded(boolean hoursViewExpanded) {
        this.hoursViewExpanded = hoursViewExpanded;
    }

    public List<Integer> getOffers() {
        return offers;
    }

    public boolean isHoursViewExpanded() {
        return hoursViewExpanded;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    protected DtlMerchantDetailsState(Parcel in) {
        super(in);
        this.offers = in.readArrayList(Integer.class.getClassLoader());
        this.hoursViewExpanded = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.offers);
        dest.writeByte(this.hoursViewExpanded ? (byte) 1 : (byte) 0);
    }

    public static final Creator<DtlMerchantDetailsState> CREATOR = new Creator<DtlMerchantDetailsState>() {
        @Override
        public DtlMerchantDetailsState createFromParcel(Parcel in) {
            return new DtlMerchantDetailsState(in);
        }

        @Override
        public DtlMerchantDetailsState[] newArray(int size) {
            return new DtlMerchantDetailsState[size];
        }
    };
}

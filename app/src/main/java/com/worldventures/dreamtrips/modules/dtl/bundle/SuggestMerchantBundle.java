package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public class SuggestMerchantBundle implements Parcelable {

    private DtlMerchant merchant;

    public SuggestMerchantBundle(DtlMerchant merchant) {
        this.merchant = merchant;
    }

    public DtlMerchant getMerchant() {
        return merchant;
    }

    public void setMerchant(DtlMerchant merchant) {
        this.merchant = merchant;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected SuggestMerchantBundle(Parcel in) {
        merchant = in.readParcelable(DtlMerchant.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(merchant, flags);
    }

    public static final Creator<SuggestMerchantBundle> CREATOR = new Creator<SuggestMerchantBundle>() {
        @Override
        public SuggestMerchantBundle createFromParcel(Parcel in) {
            return new SuggestMerchantBundle(in);
        }

        @Override
        public SuggestMerchantBundle[] newArray(int size) {
            return new SuggestMerchantBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}

package com.worldventures.dreamtrips.modules.dtl.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantsListFragment;

/**
 * Bundle to be supplied for {@link DtlMerchantsListFragment}
 */
public class MerchantsListBundle implements Parcelable {

    private DtlMerchantType merchantType;

    public MerchantsListBundle(DtlMerchantType merchantType) {
        this.merchantType = merchantType;
    }

    public DtlMerchantType getMerchantType() {
        return merchantType;
    }

    public void setMerchantType(DtlMerchantType merchantType) {
        this.merchantType = merchantType;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected MerchantsListBundle(Parcel in) {
        merchantType = (DtlMerchantType) in.readSerializable();
    }

    public static final Creator<MerchantsListBundle> CREATOR = new Creator<MerchantsListBundle>() {
        @Override
        public MerchantsListBundle createFromParcel(Parcel in) {
            return new MerchantsListBundle(in);
        }

        @Override
        public MerchantsListBundle[] newArray(int size) {
            return new MerchantsListBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(merchantType);
    }
}

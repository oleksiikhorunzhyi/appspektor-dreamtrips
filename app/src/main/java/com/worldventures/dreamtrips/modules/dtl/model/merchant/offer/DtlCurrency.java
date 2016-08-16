package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.api.dtl.merchants.model.Currency;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlCurrency implements Parcelable {

    private String code;
    private String prefix;
    private String suffix;
    private String name;
    private boolean isDefault;

    public DtlCurrency() {
    }

    public DtlCurrency(Currency currency) {
        code = currency.code();
        prefix = currency.prefix();
        suffix = currency.suffix();
        name = currency.name();
        isDefault = currency.isDefault();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getCurrencyHint() {
        return !TextUtils.isEmpty(suffix) ? suffix : code;
    }

    public String getName() {
        return name;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    protected DtlCurrency(Parcel in) {
        code = in.readString();
        prefix = in.readString();
        suffix = in.readString();
        name = in.readString();
        isDefault = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(prefix);
        dest.writeString(suffix);
        dest.writeString(name);
        dest.writeByte((byte) (isDefault ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DtlCurrency> CREATOR = new Creator<DtlCurrency>() {
        @Override
        public DtlCurrency createFromParcel(Parcel in) {
            return new DtlCurrency(in);
        }

        @Override
        public DtlCurrency[] newArray(int size) {
            return new DtlCurrency[size];
        }
    };
}

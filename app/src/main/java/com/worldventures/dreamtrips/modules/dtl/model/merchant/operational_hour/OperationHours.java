package com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class OperationHours implements Parcelable {

    private String from;
    private String to;

    public OperationHours() {
    }

    public OperationHours(com.worldventures.dreamtrips.api.dtl.merchats.model.OperationHours operationHours) {
        from = operationHours.fromTime();
        to = operationHours.toTime();
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    protected OperationHours(Parcel in) {
        from = in.readString();
        to = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(from);
        dest.writeString(to);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OperationHours> CREATOR = new Creator<OperationHours>() {
        @Override
        public OperationHours createFromParcel(Parcel in) {
            return new OperationHours(in);
        }

        @Override
        public OperationHours[] newArray(int size) {
            return new OperationHours[size];
        }
    };
}

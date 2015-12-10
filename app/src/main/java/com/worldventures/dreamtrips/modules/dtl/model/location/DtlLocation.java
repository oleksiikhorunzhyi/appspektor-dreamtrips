package com.worldventures.dreamtrips.modules.dtl.model.location;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.core.utils.TextUtils;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlLocation implements Parcelable {

    String id;
    DtlLocationType type;
    String shortName;
    String longName;
    Location coordinates;
    int merchantCount;
    List<DtlLocation> locatedIn;

    public DtlLocation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DtlLocationType getType() {
        return type;
    }

    public String getLongName() {
        return longName;
    }

    public Location getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Location coordinates) {
        this.coordinates = coordinates;
    }

    public List<DtlLocation> getLocatedIn() {
        return locatedIn != null ? locatedIn : Collections.emptyList();
    }

    public android.location.Location asAndroidLocation() {
        android.location.Location location = new android.location.Location("");
        location.setLatitude(coordinates.getLat());
        location.setLongitude(coordinates.getLng());
        return location;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlLocation(Parcel in) {
        id = in.readString();
        shortName = in.readString();
        longName = in.readString();
        type = (DtlLocationType) in.readSerializable();
        coordinates = in.readParcelable(Location.class.getClassLoader());
        merchantCount = in.readInt();
        locatedIn = in.createTypedArrayList(DtlLocation.CREATOR);
    }

    public static final Creator<DtlLocation> CREATOR = new Creator<DtlLocation>() {
        @Override
        public DtlLocation createFromParcel(Parcel in) {
            return new DtlLocation(in);
        }

        @Override
        public DtlLocation[] newArray(int size) {
            return new DtlLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(shortName);
        dest.writeString(longName);
        dest.writeSerializable(type);
        dest.writeParcelable(coordinates, flags);
        dest.writeInt(merchantCount);
        dest.writeTypedList(locatedIn);
    }

    public static class DtlNearestComparator implements Comparator<DtlLocation> {

        private LatLng currentLocation;

        public DtlNearestComparator(android.location.Location currentLocation) {
            this.currentLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }

        @Override
        public int compare(DtlLocation lhs, DtlLocation rhs) {
            double distanceToLeft = DtlLocationHelper.distanceInMiles(currentLocation,
                    lhs.getCoordinates().asLatLng());
            double distanceToRight = DtlLocationHelper.distanceInMiles(currentLocation,
                    rhs.getCoordinates().asLatLng());
            return Double.valueOf(distanceToLeft - distanceToRight).intValue();
        }
    }

    public static Comparator<DtlLocation> CATEGORY_COMPARATOR = (lhs, rhs) ->
            lhs.type.ordinal() - rhs.type.ordinal();

    public static Comparator<DtlLocation> ALPHABETICAL_COMPARATOR = (lhs, rhs) ->
            lhs.getLongName().compareToIgnoreCase(rhs.getLongName());

    public static class DtlLocationRangeComparator implements Comparator<DtlLocation> {

        private String subString;

        public DtlLocationRangeComparator(String subString) {
            this.subString = subString;
        }

        @Override
        public int compare(DtlLocation lhs, DtlLocation rhs) {
            int rangeSortResult = TextUtils.substringLocation(lhs.getLongName(), subString) -
                    TextUtils.substringLocation(rhs.getLongName(), subString);
            if (rangeSortResult != 0) {
                return rangeSortResult;
            } else {
                return ALPHABETICAL_COMPARATOR.compare(lhs, rhs);
            }
        }
    }
}



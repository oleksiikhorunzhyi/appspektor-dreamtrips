package com.worldventures.dreamtrips.modules.dtl.model.location;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.core.utils.TextUtils;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
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
    int partnerCount;
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

    public int getPartnerCount() {
        return partnerCount;
    }

    public void setPartnerCount(int partnerCount) {
        this.partnerCount = partnerCount;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    protected DtlLocation(Parcel in) {
        id = in.readString();
        type = (DtlLocationType) in.readSerializable();
        shortName = in.readString();
        longName = in.readString();
        coordinates = in.readParcelable(Location.class.getClassLoader());
        merchantCount = in.readInt();
        partnerCount = in.readInt();
        locatedIn = in.createTypedArrayList(DtlLocation.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeSerializable(type);
        dest.writeString(shortName);
        dest.writeString(longName);
        dest.writeParcelable(coordinates, flags);
        dest.writeInt(merchantCount);
        dest.writeInt(partnerCount);
        dest.writeTypedList(locatedIn);
    }

    @Override
    public int describeContents() {
        return 0;
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

    ///////////////////////////////////////////////////////////////////////////
    // Filtering and stuff
    ///////////////////////////////////////////////////////////////////////////

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
            //
            if (distanceToLeft == distanceToRight) return 0;
            if (distanceToLeft > distanceToRight) return 1;
            else return -1;
        }
    }

    public static Comparator<DtlLocation> CATEGORY_COMPARATOR = (lhs, rhs) ->
            lhs.type.ordinal() - rhs.type.ordinal();

    public static Comparator<DtlLocation> ALPHABETICAL_COMPARATOR = (lhs, rhs) ->
            lhs.getLongName().compareToIgnoreCase(rhs.getLongName());

    public static Comparator<DtlLocation> provideComparator(String query)  {
        return new DtlLocationRangeComparator(query);
    }

    public static class DtlLocationRangeComparator implements Comparator<DtlLocation> {

        private String subString;

        private DtlLocationRangeComparator(String subString) {
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



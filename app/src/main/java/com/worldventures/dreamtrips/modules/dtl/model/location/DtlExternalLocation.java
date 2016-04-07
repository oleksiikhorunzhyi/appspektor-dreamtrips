package com.worldventures.dreamtrips.modules.dtl.model.location;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.TextUtils;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlExternalLocation implements DtlLocation, Parcelable {

    String id;
    DtlLocationType type;
    String shortName;
    String longName;
    Location coordinates;
    int merchantCount;
    int partnerCount;
    List<DtlExternalLocation> locatedIn;
    LocationSourceType locationSourceType = LocationSourceType.EXTERNAL;

    public DtlExternalLocation() {
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

    @Override
    public String getLongName() {
        return longName;
    }

    @Override
    public Location getCoordinates() {
        return coordinates;
    }

    @Override
    public LocationSourceType getLocationSourceType() {
        return locationSourceType;
    }

    @Override
    public String getAnalyticsName() {
        return String.format("%s:%s:%s", longName, getLongNameFor(DtlLocationType.STATE),
                getLongNameFor(DtlLocationType.COUNTRY));
    }

    public void setCoordinates(Location coordinates) {
        this.coordinates = coordinates;
    }

    public List<DtlExternalLocation> getLocatedIn() {
        return locatedIn != null ? locatedIn : Collections.emptyList();
    }

    private String getLongNameFor(DtlLocationType type) {
        DtlExternalLocation location = Queryable.from(locatedIn)
                .firstOrDefault(tempLocation -> tempLocation.type == type);
        return location == null ? "-" : location.getLongName();
    }

    public android.location.Location asAndroidLocation() {
        android.location.Location location = new android.location.Location("");
        location.setLatitude(coordinates.getLat());
        location.setLongitude(coordinates.getLng());
        return location;
    }

    public String asStringLatLong() {
        return coordinates.getLat() + "," + coordinates.getLng();
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

    protected DtlExternalLocation(Parcel in) {
        id = in.readString();
        type = (DtlLocationType) in.readSerializable();
        shortName = in.readString();
        longName = in.readString();
        coordinates = in.readParcelable(Location.class.getClassLoader());
        merchantCount = in.readInt();
        partnerCount = in.readInt();
        locatedIn = in.createTypedArrayList(DtlExternalLocation.CREATOR);
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

    public static final Creator<DtlExternalLocation> CREATOR = new Creator<DtlExternalLocation>() {
        @Override
        public DtlExternalLocation createFromParcel(Parcel in) {
            return new DtlExternalLocation(in);
        }

        @Override
        public DtlExternalLocation[] newArray(int size) {
            return new DtlExternalLocation[size];
        }
    };

    ///////////////////////////////////////////////////////////////////////////
    // Filtering and stuff
    ///////////////////////////////////////////////////////////////////////////

    public static class DtlNearestComparator implements Comparator<DtlExternalLocation> {

        private LatLng currentLocation;

        public DtlNearestComparator(android.location.Location currentLocation) {
            this.currentLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }

        @Override
        public int compare(DtlExternalLocation lhs, DtlExternalLocation rhs) {
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

    public static Comparator<DtlExternalLocation> CATEGORY_COMPARATOR = (lhs, rhs) ->
            lhs.type.ordinal() - rhs.type.ordinal();

    public static Comparator<DtlExternalLocation> ALPHABETICAL_COMPARATOR = (lhs, rhs) ->
            lhs.getLongName().compareToIgnoreCase(rhs.getLongName());

    public static Comparator<DtlExternalLocation> provideComparator(String query) {
        return new DtlLocationRangeComparator(query);
    }

    public static class DtlLocationRangeComparator implements Comparator<DtlExternalLocation> {

        private String subString;

        private DtlLocationRangeComparator(String subString) {
            this.subString = subString;
        }

        @Override
        public int compare(DtlExternalLocation lhs, DtlExternalLocation rhs) {
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



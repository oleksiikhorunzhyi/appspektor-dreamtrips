package com.worldventures.dreamtrips.modules.dtl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.utils.LocationHelper;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlLocation implements Parcelable {

    String locationId;
    DtlLocationCategory category;
    String shortName;
    String longName;
    Location coordinates;
    int merchantCount;
    List<DtlLocation> withinLocations;

    public DtlLocation() {
    }

    public String getLocationId() {
        return locationId;
    }

    public DtlLocationCategory getCategory() {
        return category;
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

    public List<DtlLocation> getWithinLocations() {
        return withinLocations != null ? withinLocations : Collections.emptyList();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable part
    ///////////////////////////////////////////////////////////////////////////

    protected DtlLocation(Parcel in) {
        locationId = in.readString();
        shortName = in.readString();
        longName = in.readString();
        category = (DtlLocationCategory) in.readSerializable();
        coordinates = in.readParcelable(Location.class.getClassLoader());
        merchantCount = in.readInt();
        withinLocations = in.createTypedArrayList(DtlLocation.CREATOR);
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
        dest.writeString(locationId);
        dest.writeString(shortName);
        dest.writeString(longName);
        dest.writeSerializable(category);
        dest.writeParcelable(coordinates, flags);
        dest.writeInt(merchantCount);
        dest.writeTypedList(withinLocations);
    }

    public static class DtlNearestComparator implements Comparator<DtlLocation> {

        private LatLng currentLocation;

        public DtlNearestComparator(android.location.Location currentLocation) {
            this.currentLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }

        @Override
        public int compare(DtlLocation lhs, DtlLocation rhs) {
            double distanceToLeft = LocationHelper.distanceInMiles(currentLocation,
                    lhs.getCoordinates().asLatLng());
            double distanceToRight = LocationHelper.distanceInMiles(currentLocation,
                    rhs.getCoordinates().asLatLng());
            return Double.valueOf(distanceToLeft - distanceToRight).intValue();
        }
    }

}



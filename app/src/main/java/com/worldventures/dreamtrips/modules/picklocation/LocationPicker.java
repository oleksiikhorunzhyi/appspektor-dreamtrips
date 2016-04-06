package com.worldventures.dreamtrips.modules.picklocation;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;

import com.worldventures.dreamtrips.modules.picklocation.view.PickLocationActivity;

public class LocationPicker {

    private static final int REQUEST_CODE_PICK_LOCATION = 29135;

    public static final String BASE_EXTRA = LocationPicker.class.getSimpleName();
    public static final String LOCATION_EXTRA = BASE_EXTRA + "ERROR_EXTRA";
    public static final String ERROR_EXTRA = BASE_EXTRA + "LOCATION_EXTRA";

    public static void start(Activity activity) {
        activity.startActivityForResult(new Intent(activity, PickLocationActivity.class),
                REQUEST_CODE_PICK_LOCATION);
    }

    public static boolean onActivityResult(int requestCode, int resultCode, Intent data,
                                           LocationPickerListener locationPickerListener) {
        if (requestCode != REQUEST_CODE_PICK_LOCATION || resultCode != Activity.RESULT_OK) {
            return false;
        }
        Location location = null;
        if (data.hasExtra(LOCATION_EXTRA)) {
            location = data.getParcelableExtra(LOCATION_EXTRA);
        }
        Throwable error = null;
        if (data.hasExtra(ERROR_EXTRA)) {
            error = (Throwable) data.getSerializableExtra(ERROR_EXTRA);
        }
        if (locationPickerListener != null) {
            locationPickerListener.onLocationPickedResult(location, error);
        }
        return true;
    }

    public interface LocationPickerListener {
        void onLocationPickedResult(Location location, Throwable error);
    }
}

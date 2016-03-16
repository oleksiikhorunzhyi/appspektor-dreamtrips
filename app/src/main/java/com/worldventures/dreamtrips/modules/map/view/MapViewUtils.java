package com.worldventures.dreamtrips.modules.map.view;

import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

public class MapViewUtils {

    /**
     * Move location button with custom direction
     * @param mapView - map
     * @param margin - margin button
     * @param relativeDirections - array with RelativeLayout rules for adjusting button inside map
     */
    public static void setLocationButtonGravity(View mapView, int margin, int...relativeDirections) {
        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);

        if (locationButton != null && locationButton.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams params = createParamsFromDirections(relativeDirections);
            int marginDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, locationButton.getContext().getResources().getDisplayMetrics());
            params.setMargins(marginDp, marginDp, marginDp, marginDp);

            locationButton.setLayoutParams(params);
        }
    }

    private static RelativeLayout.LayoutParams createParamsFromDirections(int... rules) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        for (int rule : rules) {
            params.addRule(rule, RelativeLayout.TRUE);
        }
        return params;
    }


}

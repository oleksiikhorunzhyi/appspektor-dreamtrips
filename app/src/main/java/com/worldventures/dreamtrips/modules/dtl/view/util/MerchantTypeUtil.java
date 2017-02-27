package com.worldventures.dreamtrips.modules.dtl.view.util;

import android.view.View;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import java.util.ArrayList;
import java.util.List;

public class MerchantTypeUtil {

    public static List<String> getMerchantTypeList(String type) {

        List<String> merchantType = new ArrayList<>();

        if (type.equals(FilterData.SPAS)) {
            merchantType.add(FilterData.SPAS);
        } else if (type.equals(FilterData.ENTERTAINMENT)) {
            merchantType.add(FilterData.ENTERTAINMENT);
        } else {
            merchantType.add(FilterData.RESTAURANT);
            merchantType.add(FilterData.BAR);
        }
        return merchantType;
    }

    public static int getStringResource(String type) {

        int stringResource = R.string.dtlt_search_hint;

        if (type.equals(FilterData.SPAS)) {
            stringResource = R.string.filter_merchant_spa;
        } else if (type.equals(FilterData.ENTERTAINMENT)) {
            stringResource = R.string.filter_merchant_entertainment;
        }
        return stringResource;
    }

    public static int filterMapDrawable(View view) {
        return view.isSelected() ?
                R.drawable.custom_button_filters_map_pressed : R.drawable.custom_button_filters_map_focused;
    }

    public static int filterMerchantDrawable(View view) {
        return view.isSelected() ?
                R.drawable.circle_merchant_button_selected : R.drawable.circle_merchant_button;
    }

    public static int filterMerchantColor(View view) {
        return view.isSelected() ?
                R.color.white : R.color.dtl_text_color_disabled_filters;
    }

    public static void toggleState(View filterFood, View filterEntertainment, View filterSpa, String type) {
        if (type.equals(FilterData.SPAS)) {
            filterFood.setSelected(false);
            filterEntertainment.setSelected(false);
            filterSpa.setSelected(true);
        } else if (type.equals(FilterData.ENTERTAINMENT)) {
            filterFood.setSelected(false);
            filterEntertainment.setSelected(true);
            filterSpa.setSelected(false);
        } else {
            filterFood.setSelected(true);
            filterEntertainment.setSelected(false);
            filterSpa.setSelected(false);
        }
    }
}

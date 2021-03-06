package com.worldventures.dreamtrips.modules.dtl.view.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;

import java.util.ArrayList;
import java.util.List;

public final class MerchantTypeUtil {

   private MerchantTypeUtil() {
   }

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
      int stringResource = R.string.filter_merchant_dining;
      if (type.equals(FilterData.SPAS)) {
         stringResource = R.string.filter_merchant_spas;
      } else if (type.equals(FilterData.ENTERTAINMENT)) {
         stringResource = R.string.filter_merchant_entertainment;
      }
      return stringResource;
   }

   public static int filterMapDrawable(View view) {
      return view.isSelected()
            ? R.drawable.custom_button_filters_map_pressed : R.drawable.custom_button_filters_map_focused;
   }

   public static int filterMerchantColor(View view) {
      return view.isSelected()
            ? R.color.white : R.color.dtl_text_color_disabled_filters;
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

   public static @Nullable String getSearchHintForMerchantTypes(Context context, List<String> types) {
      if (types == null || types.isEmpty()) {
         return null;
      }
      if (types.size() == 1) {
         if (types.get(0).equals(FilterData.ENTERTAINMENT)) {
            return context.getString(R.string.filter_merchant_entertainment);
         } else if (types.get(0).equals(FilterData.SPAS)) {
            return context.getString(R.string.filter_merchant_spas);
         }
      } else {
         if (types.get(0).equals(FilterData.RESTAURANT) && types.get(1).equals(FilterData.BAR)) {
            return context.getString(R.string.dtlt_search_hint);
         }
      }
      return null;
   }
}

package com.worldventures.dreamtrips.modules.settings.util;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.dialog.SelectDialogModel;
import com.worldventures.dreamtrips.modules.settings.model.Setting;

import java.util.ArrayList;
import java.util.List;

import static com.worldventures.dreamtrips.modules.settings.util.SettingsFactory.DISTANCE_UNITS;
import static com.worldventures.dreamtrips.modules.settings.util.SettingsFactory.FRIEND_REQUEST;
import static com.worldventures.dreamtrips.modules.settings.util.SettingsFactory.KILOMETERS;
import static com.worldventures.dreamtrips.modules.settings.util.SettingsFactory.MILES;
import static com.worldventures.dreamtrips.modules.settings.util.SettingsFactory.NEW_MESSAGE;
import static com.worldventures.dreamtrips.modules.settings.util.SettingsFactory.PHOTO_TAGGING;

public class SettingsManager {

   private SettingsManager() {
   }

   public static List<Setting> merge(List<Setting> fromServer, List<Setting> local) {
      return Queryable.from(fromServer).filter(local::contains).map(setting -> {
         Setting localSetting = Queryable.from(local).firstOrDefault(setting::equals);
         if (localSetting != null) setting.setType(localSetting.getType());
         return setting;
      }).toList();
   }

   public static int getLocalizedTitleResource(String title) {
      switch (title) {
         case DISTANCE_UNITS:
            return R.string.settings_distance_units;
         case FRIEND_REQUEST:
            return R.string.settings_friend_requests;
         case NEW_MESSAGE:
            return R.string.settings_new_message;
         case PHOTO_TAGGING:
            return R.string.settings_photo_tagging;
         default:
            return 0;
      }
   }

   public static int getLocalizedOptionResource(String option) {
      switch (option) {
         case MILES:
            return R.string.abbreviated_miles;
         case KILOMETERS:
            return R.string.abbreviated_kilometers;
         default:
            return 0;
      }
   }

   public static SelectDialogModel getSelectDialogModel(@NonNull Resources res, @NonNull String title, @NonNull List<String> options, String value) {
      SelectDialogModel model = new SelectDialogModel();
      //title
      switch (title) {
         case DISTANCE_UNITS:
            model.setTitleId(R.string.show_distance_in);
            break;
         default:
            break;
      }
      //items
      int optionsSize = options.size();
      ArrayList<String> items = new ArrayList<>(optionsSize);
      for (String s : options) {
         switch (s) {
            case MILES:
               items.add(res.getString(R.string.settings_miles));
               break;
            case KILOMETERS:
               items.add(res.getString(R.string.settings_kilometers));
               break;
         }
      }
      model.setItems(items);
      //checked position
      int selectedPosition = -1;
      for (int i = 0; i < optionsSize; i++) {
         if (options.get(i).equals(value)) {
            selectedPosition = i;
         }
      }
      model.setSelectedPosition(selectedPosition);
      return model;
   }
}

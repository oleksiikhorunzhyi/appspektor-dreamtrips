package com.worldventures.dreamtrips.modules.settings.view.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;
import com.worldventures.dreamtrips.modules.settings.util.SettingsGroupFactory;

import java.util.List;

public class SettingsGroupPresenter extends Presenter<SettingsGroupPresenter.View> {

   @Override
   public void takeView(View view) {
      super.takeView(view);
      SettingsGroupFactory settingsGroupFactory = new SettingsGroupFactory(context);
      view.setSettings(settingsGroupFactory.createSettingsGroups());
   }

   @Override
   public void onResume() {
      super.onResume();
      TrackingHelper.settings();
   }

   public void handleCellClick(SettingsGroup model) {
      Route route;
      switch (model.getType()) {
         case GENERAL:
            route = Route.SETTINGS_GENERAL;
            break;
         case NOTIFICATIONS:
            route = Route.SETTINGS_NOTIFICATIONS;
            break;
         default:
            route = null;
            break;
      }
      //
      if (route != null) view.openSettings(route, model);
   }

   public interface View extends Presenter.View {

      void setSettings(List<SettingsGroup> settings);

      void openSettings(Route route, SettingsGroup model);
   }
}

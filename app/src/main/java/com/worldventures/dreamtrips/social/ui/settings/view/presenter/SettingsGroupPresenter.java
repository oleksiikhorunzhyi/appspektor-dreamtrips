package com.worldventures.dreamtrips.social.ui.settings.view.presenter;

import com.worldventures.core.modules.settings.model.SettingsGroup;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.settings.service.analytics.TrackSettingsOpenedAction;
import com.worldventures.dreamtrips.social.ui.settings.util.SettingsGroupFactory;

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
      analyticsInteractor.analyticsActionPipe().send(new TrackSettingsOpenedAction());
   }

   public void handleCellClick(SettingsGroup model) {
      switch (model.getType()) {
         case GENERAL:
            view.openGeneralSettings(model);
            break;
         case NOTIFICATIONS:
            view.openNotificationSettings(model);
            break;
         default:
            break;
      }
   }

   public interface View extends Presenter.View {

      void setSettings(List<SettingsGroup> settings);

      void openGeneralSettings(SettingsGroup model);

      void openNotificationSettings(SettingsGroup model);
   }
}

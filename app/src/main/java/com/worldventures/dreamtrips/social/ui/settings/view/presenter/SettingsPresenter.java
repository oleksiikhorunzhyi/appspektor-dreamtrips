package com.worldventures.dreamtrips.social.ui.settings.view.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.modules.settings.command.SettingsCommand;
import com.worldventures.core.modules.settings.model.Setting;
import com.worldventures.core.modules.settings.model.SettingsGroup;
import com.worldventures.core.modules.settings.service.SettingsInteractor;
import com.worldventures.core.modules.settings.storage.SettingsStorage;
import com.worldventures.core.modules.settings.util.SettingsFactory;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.social.ui.settings.service.analytics.TrackGeneralSettingsOpened;
import com.worldventures.dreamtrips.social.ui.settings.service.analytics.TrackNotificationSettingsOpened;
import com.worldventures.dreamtrips.social.ui.settings.util.SettingsManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class SettingsPresenter extends Presenter<SettingsPresenter.View> {

   @Inject SettingsStorage db;
   @Inject FilterDataInteractor dtlFilterDataInteractor;
   @Inject SettingsInteractor settingsInteractor;

   @State ArrayList<Setting> settingsList;
   @State ArrayList<Setting> immutableSettingsList;

   private SettingsGroup group;

   public SettingsPresenter(SettingsGroup group) {
      this.group = group;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      if (settingsList == null)
         settingsList = (ArrayList<Setting>) SettingsManager.merge(db.getSettings(), SettingsFactory.createSettings(group));
      //
      if (immutableSettingsList == null) immutableSettingsList = cloneList(settingsList);
      //
      view.setSettings(settingsList);
      subscribeUpdateSettings();
   }

   @Override
   public void onResume() {
      super.onResume();
      trackDetailedSettingsOpened();
   }

   private void trackDetailedSettingsOpened() {
      switch (group.getType()) {
         case GENERAL:
            analyticsInteractor.analyticsActionPipe().send(new TrackGeneralSettingsOpened());
            break;
         case NOTIFICATIONS:
            analyticsInteractor.analyticsActionPipe().send(new TrackNotificationSettingsOpened());
            break;
      }
   }

   private void subscribeUpdateSettings() {
      settingsInteractor.settingsActionPipe()
            .observe()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<SettingsCommand>().onSuccess(settingsCommand -> onSettingsSaved())
                  .onFail(this::onSaveError));
   }

   public void applyChanges() {
      if (isSettingsChanged()) {
         view.showLoading();
         settingsInteractor.settingsActionPipe().send(new SettingsCommand(getChanges()));
      }
   }

   private void onSettingsSaved() {
      List<Setting> changes = getChanges();
      db.saveSettings(changes, false);
      immutableSettingsList = cloneList(settingsList);
      // reset DTL filtering since it depends on DistanceType setting
      Observable.from(changes)
            .filter((element) -> element.getName().equals(SettingsFactory.DISTANCE_UNITS))
            .take(1)
            .compose(bindView())
            .subscribe(setting -> dtlFilterDataInteractor.reset());
      view.hideLoading();
      view.onAppliedChanges();
   }

   private void onSaveError(CommandWithError command, Throwable exception) {
      super.handleError(command, exception);
      view.hideLoading();
   }

   private ArrayList<Setting> cloneList(List<Setting> settingsList) {
      ArrayList<Setting> cloneList = new ArrayList<>();
      Queryable.from(settingsList).forEachR(setting -> {
         Setting<?> clone = new Setting<>(setting.getName(), setting.getType(), setting.getValue());
         cloneList.add(clone);
      });
      return cloneList;
   }

   private List<Setting> getChanges() {
      return Queryable.from(settingsList).filter(setting -> Queryable.from(immutableSettingsList)
            .filter(changeSetting -> changeSetting.equals(setting) && !setting.getValue()
                  .equals(changeSetting.getValue()))
            .firstOrDefault() != null).toList();
   }

   public boolean isSettingsChanged() {
      return getChanges().size() > 0;
   }

   public interface View extends RxView {

      void setSettings(List<Setting> settingsList);

      void showLoading();

      void hideLoading();

      void onAppliedChanges();
   }
}

package com.worldventures.dreamtrips.modules.settings.view.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.settings.command.SettingsCommand;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;
import com.worldventures.dreamtrips.modules.settings.service.SettingsInteractor;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;
import com.worldventures.dreamtrips.modules.settings.util.SettingsManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class SettingsPresenter extends Presenter<SettingsPresenter.View> {

   @Inject SnappyRepository db;
   @Inject DtlFilterMerchantInteractor dtlFilterMerchantInteractor;
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
      TrackingHelper.settingsDetailed(group.getType());
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
      //update state of DtlFilterMerchantStore
      Observable.from(changes)
            .filter((element) -> element.getName().equals(SettingsFactory.DISTANCE_UNITS))
            .take(1)
            .subscribe(setting -> dtlFilterMerchantInteractor.filterDataPipe().send(DtlFilterDataAction.init()));
      //
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

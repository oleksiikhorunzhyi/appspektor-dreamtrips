package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfilePath;

import javax.inject.Inject;

import io.techery.janet.Command;
import timber.log.Timber;

public class DisplayOptionsSettingsPresenter extends WalletPresenter<DisplayOptionsSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;

   DisplayOptionsSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

      smartCardInteractor.smartCardUserPipe().createObservableResult(SmartCardUserCommand.fetch())
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(view::setUser, throwable -> Timber.e(throwable, ""));
   }

   public void openEditProfileScreen() {
      navigator.go(new WalletSettingsProfilePath());
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void setUser(SmartCardUser user);

   }
}
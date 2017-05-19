package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwarePath;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class WalletPuckConnectionPresenter extends WalletPresenter<WalletPuckConnectionPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;

   public WalletPuckConnectionPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      fetchUserPhoto();
   }
   private void fetchUserPhoto() {
      smartCardInteractor.smartCardUserPipe()
            .createObservable(SmartCardUserCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SmartCardUserCommand>()
                  .onSuccess(command -> getView().userPhoto(command.getResult().userPhoto().photoUrl()))
            );
   }
   void goNext() {
      navigator.withoutLast(new WalletDownloadFirmwarePath());
   }

   void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void userPhoto(String photoUrl);
   }

}

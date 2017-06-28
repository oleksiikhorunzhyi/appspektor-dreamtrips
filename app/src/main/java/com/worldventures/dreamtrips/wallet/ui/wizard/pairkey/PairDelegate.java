package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePath;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.WizardUploadProfilePath;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.SyncAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.SyncRecordsPath;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

abstract class PairDelegate {

   protected final Navigator navigator;
   protected final SmartCardInteractor smartCardInteractor;

   private PairDelegate(Navigator navigator, SmartCardInteractor smartCardInteractor) {
      this.navigator = navigator;
      this.smartCardInteractor = smartCardInteractor;
   }

   public abstract void prepareView(PairView view);

   public abstract void navigateOnNextScreen(PairView view);

   public static PairDelegate create(ProvisioningMode mode, Navigator navigator, SmartCardInteractor smartCardInteractor) {
      if (mode == ProvisioningMode.SETUP_NEW_DEVICE) {
         return new NewDeviceDelegate(navigator, smartCardInteractor);
      } else { // ProvisioningMode.STANDARD or ProvisioningMode.SETUP_NEW_CARD
         return new SetupDelegate(navigator, smartCardInteractor);
      }
   }

   private static class NewDeviceDelegate extends PairDelegate {

      private NewDeviceDelegate(Navigator navigator, SmartCardInteractor smartCardInteractor) {
         super(navigator, smartCardInteractor);
      }

      @Override
      public void prepareView(PairView view) {
         view.hideBackButton();
      }

      @Override
      public void navigateOnNextScreen(PairView view) {
         navigator.withoutLast(new SyncRecordsPath(SyncAction.TO_DEVICE));
      }
   }

   private static class SetupDelegate extends PairDelegate {

      private SetupDelegate(Navigator navigator, SmartCardInteractor smartCardInteractor) {
         super(navigator, smartCardInteractor);
      }

      @Override
      public void prepareView(PairView view) {
         view.showBackButton();
      }

      @Override
      public void navigateOnNextScreen(PairView view) {
         smartCardInteractor.smartCardUserPipe()
               .createObservable(SmartCardUserCommand.fetch())
               .compose(RxLifecycle.bindView(view.getView()))
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new ActionStateSubscriber<SmartCardUserCommand>()
                     .onSuccess(command -> handleSmartCardUserExisting(command.getResult()))
               );
      }

      private void handleSmartCardUserExisting(SmartCardUser smartCardUser) {
         if (smartCardUser != null) {
            navigator.withoutLast(new WizardUploadProfilePath());
         } else {
            navigator.withoutLast(new WizardEditProfilePath());
         }
      }
   }
}

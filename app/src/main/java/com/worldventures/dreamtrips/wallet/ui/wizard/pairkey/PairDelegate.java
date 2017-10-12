package com.worldventures.dreamtrips.wallet.ui.wizard.pairkey;

import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.SyncAction;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public abstract class PairDelegate {

   protected final Navigator navigator;
   protected final SmartCardInteractor smartCardInteractor;
   protected final ProvisioningMode provisioningMode;

   private PairDelegate(Navigator navigator, SmartCardInteractor smartCardInteractor, ProvisioningMode provisioningMode) {
      this.navigator = navigator;
      this.smartCardInteractor = smartCardInteractor;
      this.provisioningMode = provisioningMode;
   }

   public abstract void prepareView(PairView view);

   public abstract void navigateOnNextScreen(PairView view);

   public static PairDelegate create(ProvisioningMode mode, Navigator navigator, SmartCardInteractor smartCardInteractor) {
      if (mode == ProvisioningMode.SETUP_NEW_DEVICE) {
         return new NewDeviceDelegate(navigator, smartCardInteractor, mode);
      } else { // ProvisioningMode.STANDARD or ProvisioningMode.SETUP_NEW_CARD
         return new SetupDelegate(navigator, smartCardInteractor, mode);
      }
   }

   private final static class NewDeviceDelegate extends PairDelegate {

      private NewDeviceDelegate(Navigator navigator, SmartCardInteractor smartCardInteractor, ProvisioningMode provisioningMode) {
         super(navigator, smartCardInteractor, provisioningMode);
      }

      @Override
      public void prepareView(PairView view) {
         view.hideBackButton();
      }

      @Override
      public void navigateOnNextScreen(PairView view) {
         navigator.goSyncRecordsPath(SyncAction.TO_DEVICE);
      }
   }

   private final static class SetupDelegate extends PairDelegate {

      private SetupDelegate(Navigator navigator, SmartCardInteractor smartCardInteractor, ProvisioningMode provisioningMode) {
         super(navigator, smartCardInteractor, provisioningMode);
      }

      @Override
      public void prepareView(PairView view) {
         view.showBackButton();
      }

      @Override
      public void navigateOnNextScreen(PairView view) {
         smartCardInteractor.smartCardUserPipe()
               .createObservable(SmartCardUserCommand.fetch())
               .compose(RxLifecycleAndroid.bindView(view.getView()))
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new ActionStateSubscriber<SmartCardUserCommand>()
                     .onSuccess(command -> handleSmartCardUserExisting(command.getResult()))
               );
      }

      private void handleSmartCardUserExisting(SmartCardUser smartCardUser) {
         if (smartCardUser != null) {
            navigator.goWizardUploadProfile(provisioningMode);
         } else {
            navigator.goWizardEditProfile(provisioningMode);
         }
      }
   }
}

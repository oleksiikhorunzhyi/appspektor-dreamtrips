package com.worldventures.dreamtrips.wallet.ui.wizard.manual;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePath;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class WizardManualInputPresenter extends WalletPresenter<WizardManualInputPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject Activity activity;

   private int scidLength;

   public WizardManualInputPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      // TODO: 9/1/16 investigate and fix this problem (photo piker brake SoftInputMode too)
      // hotfix, web view brake SoftInputMode (set ADJUST_RESIZE)
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

      scidLength = getContext().getResources().getInteger(R.integer.wallet_smart_card_id_length);
      connectToSmartCard();
      observeScidInput();
   }

   private void connectToSmartCard() {
      wizardInteractor.createAndConnectActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.createAndConnectActionPipe()))
            .subscribe(OperationSubscriberWrapper.<CreateAndConnectToCardCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> navigator.go(new WizardWelcomePath(command.getSmartCardId())))
                  .onFail(throwable -> new OperationSubscriberWrapper.MessageActionHolder<>(getContext().getString(R.string.wallet_wizard_scid_validation_error),
                        command -> Timber.e("Could not connect to device")))
                  .wrap());
   }

   private void observeScidInput() {
      getView().scidInput()
            .compose(bindView())
            .subscribe(scid -> getView().buttonEnable(scid.length() == scidLength));
   }

   public void checkBarcode(String barcode) {
      wizardInteractor.associateCardUserCommandPipe().send(new AssociateCardUserCommand(barcode));
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void buttonEnable(boolean isEnable);

      @NonNull
      Observable<CharSequence> scidInput();
   }
}

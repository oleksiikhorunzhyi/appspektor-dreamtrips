package com.worldventures.dreamtrips.wallet.ui.wizard.input.manual;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.ManualCardInputAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.ScidScannedAction;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.SmartCardStatusHandler;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPath;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;
import timber.log.Timber;

public class WizardManualInputPresenter extends WalletPresenter<WizardManualInputPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject Activity activity;

   private int scidLength;

   public WizardManualInputPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observerAvailabilitySmartCard();

      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new ManualCardInputAction()));
   }

   private void observerAvailabilitySmartCard() {
      wizardInteractor.getSmartCardStatusCommandActionPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationFetchCardStatus())
                  .onSuccess(command -> SmartCardStatusHandler.handleSmartCardStatus(command.getResult(),
                        statusUnassigned -> cardIsUnassigned(command.getSmartCardId()),
                        statusAssignToAnotherDevice -> Timber.d("This card is assigned to another your device"), //todo: remove this after implement Assign new phone feature.
                        statusAssignedToAnotherUser -> getView().showErrorCardIsAssignedDialog()
                  ))
                  .onFail((command, throwable) -> Timber.e(throwable, ""))
                  .create());
   }

   private void cardIsUnassigned(String smartCardId) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new ScidScannedAction(smartCardId)));
      navigator.go(new PairKeyPath(smartCardId));
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      // TODO: 9/1/16 investigate and fix this problem (photo piker brake SoftInputMode too)
      // hotfix, web view brake SoftInputMode (set ADJUST_RESIZE)
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

      scidLength = getContext().getResources().getInteger(R.integer.wallet_smart_card_id_length);
      observeScidInput();
   }

   private void observeScidInput() {
      getView().scidInput()
            .compose(bindView())
            .subscribe(scid -> getView().buttonEnable(scid.length() == scidLength));
   }

   void checkBarcode(String barcode) {
      wizardInteractor.getSmartCardStatusCommandActionPipe().send(new GetSmartCardStatusCommand(barcode));
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void buttonEnable(boolean isEnable);

      @NonNull
      Observable<CharSequence> scidInput();

      OperationView<GetSmartCardStatusCommand> provideOperationFetchCardStatus();

      void showErrorCardIsAssignedDialog();
   }
}

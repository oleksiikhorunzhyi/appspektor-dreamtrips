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
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputAnalyticsDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputBarcodeDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputDelegateView;

import javax.inject.Inject;

import rx.Observable;

public class WizardManualInputPresenter extends WalletPresenter<WizardManualInputPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject Activity activity;

   private final int scidLength;
   private InputBarcodeDelegate inputBarcodeDelegate;

   public WizardManualInputPresenter(Context context, Injector injector) {
      super(context, injector);
      scidLength = context.getResources().getInteger(R.integer.wallet_smart_card_id_length);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new ManualCardInputAction()));

      inputBarcodeDelegate = new InputBarcodeDelegate(navigator, wizardInteractor,
            getView(), InputAnalyticsDelegate.createForManualInputScreen(analyticsInteractor));
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      // TODO: 9/1/16 investigate and fix this problem (photo piker brake SoftInputMode too)
      // hotfix, web view brake SoftInputMode (set ADJUST_RESIZE)
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


      observeScidInput();
   }

   private void observeScidInput() {
      //noinspection ConstantConditions
      getView().scidInput()
            .compose(bindView())
            .subscribe(scid -> getView().buttonEnable(scid.length() == scidLength));
   }

   void checkBarcode(String barcode) {
      inputBarcodeDelegate.barcodeEntered(barcode);
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen, InputDelegateView {

      void buttonEnable(boolean isEnable);

      @NonNull
      Observable<CharSequence> scidInput();

   }
}

package com.worldventures.dreamtrips.wallet.ui.settings.help.support;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.settings.customer_support.Contact;
import com.worldventures.dreamtrips.wallet.service.command.settings.SettingsHelpInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.GetCustomerSupportContactCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.SendFeedbackPath;
import com.worldventures.dreamtrips.wallet.util.GuaranteedProgressVisibilityTransformer;
import com.worldventures.dreamtrips.wallet.util.NoProgressAfterSuccessTransformer;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import timber.log.Timber;

public class WalletCustomerSupportSettingsPresenter extends WalletPresenter<WalletCustomerSupportSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SettingsHelpInteractor settingsHelpInteractor;

   WalletCustomerSupportSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeContact();
      fetchCustomerSupportContact();
   }

   private void observeContact() {
      settingsHelpInteractor.customerSupportContactPipe()
            .observeWithReplay()
            .compose(new NoProgressAfterSuccessTransformer<>())
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> getView().bindData(command.getResult()))
                  .create());
   }

   void fetchCustomerSupportContact() {
      settingsHelpInteractor.customerSupportContactPipe().send(new GetCustomerSupportContactCommand());
   }

   void goBack() {
      navigator.goBack();
   }

   void dialPhoneNumber(String phoneNumber) {
      Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
      try {
         getContext().startActivity(intent);
      } catch (ActivityNotFoundException e) {
         Timber.e(e, "");
      }
   }

   void openCustomerSupportFeedbackScreen() {
      navigator.go(new SendFeedbackPath(SendFeedbackPath.FeedbackType.CustomerSupport));
   }

   interface Screen extends WalletScreen {

      void bindData(Contact contact);

      OperationView<GetCustomerSupportContactCommand> provideOperationView();
   }

}

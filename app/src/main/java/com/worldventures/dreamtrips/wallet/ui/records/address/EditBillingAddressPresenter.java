package com.worldventures.dreamtrips.wallet.ui.records.address;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.BillingAddressSavedAction;
import com.worldventures.dreamtrips.wallet.analytics.EditBillingAddressAction;
import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.command.record.UpdateRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import javax.inject.Inject;

public class EditBillingAddressPresenter extends WalletPresenter<EditBillingAddressPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject RecordInteractor recordInteractor;

   private final Record record;

   public EditBillingAddressPresenter(Context context, Injector injector, Record record) {
      super(context, injector);
      this.record = record;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
      connectToUpdateCardDetailsPipe();
      getView().address(record.addressInfo());
   }

   private void trackScreen() {
      analyticsInteractor.paycardAnalyticsCommandPipe()
            .send(new PaycardAnalyticsCommand(new EditBillingAddressAction(), record));
   }

   private void connectToUpdateCardDetailsPipe() {
      recordInteractor.updateRecordPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<UpdateRecordCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> addressChanged())
                  .onFail(ErrorHandler.<UpdateRecordCommand>builder(getContext())
                        .handle(FormatException.class, R.string.wallet_add_card_details_error_message)
                        .build())
                  .wrap());
   }

   private void addressChanged() {
      navigator.goBack();
      analyticsInteractor.paycardAnalyticsCommandPipe()
            .send(new PaycardAnalyticsCommand(new BillingAddressSavedAction(), record));
   }

   void onCardAddressConfirmed(AddressInfo addressInfo) {
      recordInteractor.updateRecordPipe().send(UpdateRecordCommand.updateAddress(record, addressInfo));
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void address(AddressInfo addressInfo);
   }

}

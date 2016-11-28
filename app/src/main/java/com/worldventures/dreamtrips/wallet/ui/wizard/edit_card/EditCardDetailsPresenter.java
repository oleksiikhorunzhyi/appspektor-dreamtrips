package com.worldventures.dreamtrips.wallet.ui.wizard.edit_card;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.BillingAddressSavedAction;
import com.worldventures.dreamtrips.wallet.analytics.EditBillingAddressAction;
import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.UpdateCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import javax.inject.Inject;

import flow.Flow;

public class EditCardDetailsPresenter extends WalletPresenter<EditCardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject LocaleHelper localeHelper;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final BankCard bankCard;

   public EditCardDetailsPresenter(Context context, Injector injector, BankCard bankCard) {
      super(context, injector);
      this.bankCard = bankCard;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
      connectToUpdateCardDetailsPipe();
      getView().address(ImmutableAddressInfoWithLocale.builder()
            .addressInfo(bankCard.addressInfo())
            .locale(localeHelper.getDefaultLocale())
            .build());
   }

   private void trackScreen() {
      analyticsInteractor.paycardAnalyticsCommandPipe()
            .send(new PaycardAnalyticsCommand(new EditBillingAddressAction(), bankCard));
   }

   private void connectToUpdateCardDetailsPipe() {
      smartCardInteractor.updatePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<UpdateCardDetailsDataCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> addressChanged())
                  .onFail(ErrorHandler.<UpdateCardDetailsDataCommand>builder(getContext())
                        .handle(FormatException.class, R.string.wallet_add_card_details_error_message)
                        .build())
                  .wrap());
   }

   private void addressChanged() {
      navigator.single(new CardListPath(), Flow.Direction.REPLACE);
      analyticsInteractor.paycardAnalyticsCommandPipe()
            .send(new PaycardAnalyticsCommand(new BillingAddressSavedAction(), bankCard));
   }

   void onCardAddressConfirmed(AddressInfo addressInfo) {
      smartCardInteractor.updatePipe().send(new UpdateCardDetailsDataCommand(bankCard, addressInfo));
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void address(AddressInfoWithLocale defaultAddress);
   }

}
